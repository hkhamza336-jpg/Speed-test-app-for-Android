package com.speedtest.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedTestDao {
    
    @Insert
    suspend fun insertResult(result: SpeedTestEntity): Long
    
    @Query("SELECT * FROM speed_test_results ORDER BY testDate DESC")
    fun getAllResults(): Flow<List<SpeedTestEntity>>
    
    @Query("SELECT * FROM speed_test_results ORDER BY testDate DESC LIMIT :limit")
    fun getRecentResults(limit: Int): Flow<List<SpeedTestEntity>>
    
    @Query("SELECT * FROM speed_test_results WHERE id = :id")
    suspend fun getResultById(id: Long): SpeedTestEntity?
    
    @Query("DELETE FROM speed_test_results WHERE id = :id")
    suspend fun deleteResult(id: Long)
    
    @Delete
    suspend fun deleteResult(result: SpeedTestEntity)
    
    @Query("DELETE FROM speed_test_results")
    suspend fun deleteAllResults()
    
    @Query("SELECT COUNT(*) FROM speed_test_results")
    fun getResultCount(): Flow<Int>
    
    @Query("SELECT AVG(downloadSpeed) FROM speed_test_results")
    fun getAverageDownloadSpeed(): Flow<Double?>
    
    @Query("SELECT AVG(uploadSpeed) FROM speed_test_results")
    fun getAverageUploadSpeed(): Flow<Double?>
}
