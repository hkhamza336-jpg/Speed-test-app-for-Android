package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SpeedTestDatabase
import com.example.data.model.SpeedTestRecord
import com.example.data.repository.SpeedTestRepository
import com.example.engine.SpeedTestEngine
import com.example.engine.SpeedTestState
import com.example.engine.TestPhase
import com.example.util.NetworkInfoHelper
import com.example.util.NetworkStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppMode {
    SPEED_TEST,
    HISTORY
}

enum class SpeedUnit(val label: String, val multiplier: Double) {
    MBPS("Mbps", 1.0),
    MB_S("MB/s", 0.125), // 1 Byte = 8 bits
    KBPS("Kbps", 1000.0)
}

class SpeedTestViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SpeedTestDatabase.getDatabase(application)
    private val repository = SpeedTestRepository(database.speedTestDao())

    private val speedEngine = SpeedTestEngine()
    val testState: StateFlow<SpeedTestState> = speedEngine.state

    val historyRecords: StateFlow<List<SpeedTestRecord>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalTestsCount: StateFlow<Int> = repository.recordCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val maxDownloadSpeed: StateFlow<Double?> = repository.maxDownload
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _networkStatus = MutableStateFlow(NetworkInfoHelper.getNetworkStatus(application))
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    private val _currentMode = MutableStateFlow(AppMode.SPEED_TEST)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    private val _selectedUnit = MutableStateFlow(SpeedUnit.MBPS)
    val selectedUnit: StateFlow<SpeedUnit> = _selectedUnit.asStateFlow()

    private val _lastCompletedRecord = MutableStateFlow<SpeedTestRecord?>(null)
    val lastCompletedRecord: StateFlow<SpeedTestRecord?> = _lastCompletedRecord.asStateFlow()

    init {
        // Observe network connectivity
        viewModelScope.launch {
            NetworkInfoHelper.observeNetworkStatus(application).collect { status ->
                _networkStatus.value = status
            }
        }
    }

    fun setMode(mode: AppMode) {
        _currentMode.value = mode
    }

    fun setUnit(unit: SpeedUnit) {
        _selectedUnit.value = unit
    }

    fun startSpeedTest() {
        if (testState.value.phase != TestPhase.IDLE && testState.value.phase != TestPhase.COMPLETED && testState.value.phase != TestPhase.ERROR) {
            return
        }

        viewModelScope.launch {
            val result = speedEngine.runFullSpeedTest()
            if (result.phase == TestPhase.COMPLETED) {
                val rating = calculateRating(result.downloadMbps, result.pingMs)
                val resolvedIsp = if (_networkStatus.value.ispName.isNotBlank() && _networkStatus.value.ispName != "Detecting ISP...") {
                    _networkStatus.value.ispName
                } else {
                    _networkStatus.value.carrierOrWifi
                }
                val record = SpeedTestRecord(
                    downloadSpeedMbps = result.downloadMbps,
                    uploadSpeedMbps = result.uploadMbps,
                    pingMs = result.pingMs,
                    jitterMs = result.jitterMs,
                    connectionType = _networkStatus.value.connectionType,
                    ispOrServer = resolvedIsp,
                    rating = rating
                )
                repository.insertRecord(record)
                _lastCompletedRecord.value = record
            }
        }
    }

    fun cancelSpeedTest() {
        speedEngine.cancelTest()
    }

    fun resetTest() {
        speedEngine.reset()
        _lastCompletedRecord.value = null
    }

    fun deleteHistoryRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun calculateRating(downloadMbps: Double, pingMs: Long): String {
        return when {
            downloadMbps >= 100.0 && pingMs <= 30 -> "Ultra 4K & Gaming"
            downloadMbps >= 50.0 && pingMs <= 50 -> "Fast HD Streaming"
            downloadMbps >= 25.0 -> "Standard Broadband"
            downloadMbps >= 10.0 -> "Basic Web & Video"
            else -> "Low Bandwidth"
        }
    }

    override fun onCleared() {
        super.onCleared()
        speedEngine.cancelTest()
    }
}
