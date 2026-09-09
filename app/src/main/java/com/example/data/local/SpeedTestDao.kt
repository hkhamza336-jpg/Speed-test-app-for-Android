package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SpeedTestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedTestDao {
    @Query("SELECT * FROM speed_test_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<SpeedTestRecord>>

    @Query("SELECT * FROM speed_test_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): Flow<SpeedTestRecord?>

    @Query("SELECT * FROM speed_test_records WHERE id = :id")
    fun getRecordById(id: Long): Flow<SpeedTestRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SpeedTestRecord): Long

    @Query("DELETE FROM speed_test_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM speed_test_records")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM speed_test_records")
    fun getRecordCount(): Flow<Int>

    @Query("SELECT MAX(download_speed_mbps) FROM speed_test_records")
    fun getMaxDownload(): Flow<Double?>

    @Query("SELECT MAX(upload_speed_mbps) FROM speed_test_records")
    fun getMaxUpload(): Flow<Double?>

    @Query("SELECT AVG(ping_ms) FROM speed_test_records")
    fun getAveragePing(): Flow<Double?>
}

