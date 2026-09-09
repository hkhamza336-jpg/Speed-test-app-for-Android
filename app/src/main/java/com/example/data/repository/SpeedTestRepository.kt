package com.example.data.repository

import com.example.data.local.SpeedTestDao
import com.example.data.model.SpeedTestRecord
import kotlinx.coroutines.flow.Flow

class SpeedTestRepository(private val speedTestDao: SpeedTestDao) {
    val allRecords: Flow<List<SpeedTestRecord>> = speedTestDao.getAllRecords()
    val latestRecord: Flow<SpeedTestRecord?> = speedTestDao.getLatestRecord()
    val recordCount: Flow<Int> = speedTestDao.getRecordCount()
    val maxDownload: Flow<Double?> = speedTestDao.getMaxDownload()
    val maxUpload: Flow<Double?> = speedTestDao.getMaxUpload()
    val averagePing: Flow<Double?> = speedTestDao.getAveragePing()

    fun getRecordById(id: Long): Flow<SpeedTestRecord?> {
        return speedTestDao.getRecordById(id)
    }

    suspend fun insertRecord(record: SpeedTestRecord): Long {
        return speedTestDao.insert(record)
    }

    suspend fun deleteRecord(id: Long) {
        speedTestDao.deleteById(id)
    }

    suspend fun clearHistory() {
        speedTestDao.clearAll()
    }
}

