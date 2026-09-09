package com.speedtest.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "speed_test_results")
data class SpeedTestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val downloadSpeed: Double,
    val uploadSpeed: Double,
    val ping: Long,
    val jitter: Double = 0.0,
    val packetLoss: Double = 0.0,
    val testDate: Long = System.currentTimeMillis(),
    val serverName: String? = null,
    val serverCountry: String? = null,
    val userIsp: String? = null,
    val userIp: String? = null
)
