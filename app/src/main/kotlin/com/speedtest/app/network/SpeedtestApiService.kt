package com.speedtest.app.network

import retrofit2.http.GET
import retrofit2.http.Query

interface SpeedtestApiService {
    
    @GET("https://www.speedtest.net/api/js/servers")
    suspend fun getServers(): ServerListResponse
    
    @GET("https://www.speedtest.net/api/js/config")
    suspend fun getConfig(): ConfigResponse
}

data class ServerListResponse(
    val servers: List<Server>
)

data class Server(
    val id: String,
    val name: String,
    val url: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val cc: String,
    val sponsor: String,
    val host: String? = null
)

data class ConfigResponse(
    val client: Client,
    val times: Times,
    val download: Download,
    val upload: Upload
)

data class Client(
    val ip: String,
    val lat: Double,
    val lon: Double,
    val isp: String,
    val isprating: String? = null,
    val ispdlavg: String? = null,
    val ispulavg: String? = null
)

data class Times(
    val dl: Int,
    val ul: Int
)

data class Download(
    val testlength: Int,
    val initialtest: Double,
    val mintestsize: Int,
    val threadsperurl: Int,
    val maxthreads: Int,
    val maxchunkcount: Int,
    val maxchunksize: Int,
    val quartiletimeout: Int,
    val delaytime: Int,
    val delaytimeunit: String
)

data class Upload(
    val testlength: Int,
    val initialtest: Double,
    val mintestsize: Int,
    val threads: Int,
    val maxchunkcount: Int,
    val maxchunksize: Int
)
