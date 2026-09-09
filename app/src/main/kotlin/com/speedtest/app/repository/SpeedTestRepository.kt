package com.speedtest.app.repository

import com.speedtest.app.database.SpeedTestDao
import com.speedtest.app.database.SpeedTestEntity
import com.speedtest.app.network.RetrofitClient
import com.speedtest.app.network.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class SpeedTestRepository(private val speedTestDao: SpeedTestDao) {
    
    suspend fun getServers(): List<Server> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.speedtestApi.getServers()
            response.servers
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getConfig() = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.speedtestApi.getConfig()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun saveSpeedTestResult(result: SpeedTestEntity): Long =
        withContext(Dispatchers.IO) {
            speedTestDao.insertResult(result)
        }
    
    fun getAllResults() = speedTestDao.getAllResults()
    
    fun getRecentResults(limit: Int = 10) = speedTestDao.getRecentResults(limit)
    
    fun getAverageDownloadSpeed() = speedTestDao.getAverageDownloadSpeed()
    
    fun getAverageUploadSpeed() = speedTestDao.getAverageUploadSpeed()
    
    suspend fun deleteResult(id: Long) = withContext(Dispatchers.IO) {
        speedTestDao.deleteResult(id)
    }
    
    suspend fun deleteAllResults() = withContext(Dispatchers.IO) {
        speedTestDao.deleteAllResults()
    }
    
    suspend fun performPingTest(): Long = withContext(Dispatchers.IO) {
        val times = mutableListOf<Long>()
        try {
            repeat(4) {
                val startTime = System.currentTimeMillis()
                try {
                    RetrofitClient.speedtestApi.getConfig()
                    val endTime = System.currentTimeMillis()
                    times.add(endTime - startTime)
                } catch (e: Exception) {
                    // Skip failed ping
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
        return@withContext if (times.isNotEmpty()) times.average().toLong() else 0L
    }
    
    suspend fun performDownloadTest(testDurationMs: Long = 10000): Double = withContext(Dispatchers.IO) {
        return@withContext try {
            val testFileUrl = "https://speedtest.ftp.otenet.gr/files/test10Mb.db"
            val startTime = System.currentTimeMillis()
            var totalBytes = 0L
            
            // Simulated download test - in production, implement actual file download
            val testData = ByteArray(1024 * 1024) // 1MB chunks
            while (System.currentTimeMillis() - startTime < testDurationMs) {
                totalBytes += testData.size
            }
            
            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0
            (totalBytes * 8 / 1_000_000) / elapsedSeconds // Convert to Mbps
        } catch (e: Exception) {
            0.0
        }
    }
    
    suspend fun performUploadTest(testDurationMs: Long = 10000): Double = withContext(Dispatchers.IO) {
        return@withContext try {
            val startTime = System.currentTimeMillis()
            var totalBytes = 0L
            
            // Simulated upload test - in production, implement actual file upload
            val testData = ByteArray(1024 * 1024) // 1MB chunks
            while (System.currentTimeMillis() - startTime < testDurationMs) {
                totalBytes += testData.size
            }
            
            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0
            (totalBytes * 8 / 1_000_000) / elapsedSeconds // Convert to Mbps
        } catch (e: Exception) {
            0.0
        }
    }
}
