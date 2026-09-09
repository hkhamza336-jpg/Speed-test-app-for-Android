package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "speed_test_records",
    indices = [Index(value = ["timestamp"])]
)
data class SpeedTestRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "download_speed_mbps")
    val downloadSpeedMbps: Double,

    @ColumnInfo(name = "upload_speed_mbps")
    val uploadSpeedMbps: Double,

    @ColumnInfo(name = "ping_ms")
    val pingMs: Long,

    @ColumnInfo(name = "jitter_ms")
    val jitterMs: Long,

    @ColumnInfo(name = "connection_type")
    val connectionType: String,

    @ColumnInfo(name = "isp_or_server")
    val ispOrServer: String,

    @ColumnInfo(name = "rating")
    val rating: String
)

