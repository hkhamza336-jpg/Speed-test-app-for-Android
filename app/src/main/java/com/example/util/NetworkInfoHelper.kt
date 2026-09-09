package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.telephony.TelephonyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.concurrent.TimeUnit

data class NetworkStatus(
    val isConnected: Boolean = false,
    val connectionType: String = "Unknown",
    val ipAddress: String = "---",
    val publicIp: String = "---",
    val ispName: String = "Detecting ISP...",
    val serverOrLocation: String = "",
    val carrierOrWifi: String = "Online"
)

object NetworkInfoHelper {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var cachedIspName: String? = null
    @Volatile
    private var cachedPublicIp: String? = null
    @Volatile
    private var cachedLocation: String? = null

    fun getNetworkStatus(context: Context): NetworkStatus {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return NetworkStatus()

        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkStatus()
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus()

        val isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val connectionType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            else -> "Connected"
        }

        val localIp = getLocalIpAddress()

        var carrierOrWifi = connectionType
        var initialIsp = cachedIspName ?: "Detecting ISP..."
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val operatorName = telephonyManager?.networkOperatorName?.takeIf { it.isNotBlank() }
                ?: telephonyManager?.simOperatorName?.takeIf { it.isNotBlank() }
            if (!operatorName.isNullOrBlank()) {
                carrierOrWifi = operatorName
                if (cachedIspName == null) {
                    initialIsp = operatorName
                }
            } else {
                carrierOrWifi = "Mobile Data"
            }
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            carrierOrWifi = "Wi-Fi Network"
        }

        return NetworkStatus(
            isConnected = isConnected,
            connectionType = connectionType,
            ipAddress = localIp,
            publicIp = cachedPublicIp ?: localIp,
            ispName = initialIsp,
            serverOrLocation = cachedLocation ?: "",
            carrierOrWifi = carrierOrWifi
        )
    }

    suspend fun fetchPublicIspDetails(): Triple<String, String, String>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://ipwho.is/")
                .header("User-Agent", "Mozilla/5.0 (Android SpeedTest)")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val json = JSONObject(bodyString)
                        val success = json.optBoolean("success", true)
                        if (success) {
                            val ip = json.optString("ip", "")
                            val city = json.optString("city", "")
                            val country = json.optString("country", "")
                            val location = when {
                                city.isNotBlank() && country.isNotBlank() -> "$city, $country"
                                country.isNotBlank() -> country
                                else -> ""
                            }

                            val connection = json.optJSONObject("connection")
                            var isp = connection?.optString("isp", "")?.trim() ?: ""
                            if (isp.isBlank()) {
                                isp = connection?.optString("org", "")?.trim() ?: ""
                            }

                            if (isp.isNotBlank()) {
                                cachedIspName = isp
                                cachedPublicIp = ip
                                cachedLocation = location
                                return@withContext Triple(isp, ip, location)
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
        null
    }

    fun observeNetworkStatus(context: Context): Flow<NetworkStatus> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (connectivityManager == null) {
            trySend(NetworkStatus())
            close()
            return@callbackFlow
        }

        fun updateAndFetchIsp(baseStatus: NetworkStatus) {
            trySend(baseStatus)
            if (baseStatus.isConnected) {
                launch(Dispatchers.IO) {
                    val result = fetchPublicIspDetails()
                    if (result != null) {
                        val (isp, ip, location) = result
                        trySend(
                            baseStatus.copy(
                                ispName = isp,
                                publicIp = if (ip.isNotBlank()) ip else baseStatus.publicIp,
                                serverOrLocation = location
                            )
                        )
                    } else if (baseStatus.ispName == "Detecting ISP...") {
                        val fallbackIsp = if (baseStatus.carrierOrWifi != "Wi-Fi Network" && baseStatus.carrierOrWifi != "Cellular") {
                            baseStatus.carrierOrWifi
                        } else {
                            "Broadband ISP"
                        }
                        trySend(baseStatus.copy(ispName = fallbackIsp))
                    }
                }
            }
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateAndFetchIsp(getNetworkStatus(context))
            }

            override fun onLost(network: Network) {
                cachedIspName = null
                cachedPublicIp = null
                cachedLocation = null
                trySend(getNetworkStatus(context))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                updateAndFetchIsp(getNetworkStatus(context))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        updateAndFetchIsp(getNetworkStatus(context))

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return "127.0.0.1"
            for (networkInterface in interfaces) {
                if (networkInterface.isLoopback || !networkInterface.isUp) continue
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {
        }
        return "192.168.1.1"
    }
}
