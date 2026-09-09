package com.speedtest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.speedtest.app.database.SpeedTestEntity
import com.speedtest.app.network.Server
import com.speedtest.app.repository.SpeedTestRepository
import kotlinx.coroutines.launch

class SpeedTestViewModel(private val repository: SpeedTestRepository) : ViewModel() {
    
    private val _downloadSpeed = MutableLiveData<Double>(0.0)
    val downloadSpeed: LiveData<Double> = _downloadSpeed
    
    private val _uploadSpeed = MutableLiveData<Double>(0.0)
    val uploadSpeed: LiveData<Double> = _uploadSpeed
    
    private val _ping = MutableLiveData<Long>(0)
    val ping: LiveData<Long> = _ping
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    private val _testProgress = MutableLiveData<Int>(0)
    val testProgress: LiveData<Int> = _testProgress
    
    private val _servers = MutableLiveData<List<Server>>(emptyList())
    val servers: LiveData<List<Server>> = _servers
    
    private val _selectedServer = MutableLiveData<Server?>(null)
    val selectedServer: LiveData<Server?> = _selectedServer
    
    val allResults = repository.getAllResults()
    val recentResults = repository.getRecentResults(10)
    val averageDownloadSpeed = repository.getAverageDownloadSpeed()
    val averageUploadSpeed = repository.getAverageUploadSpeed()
    
    fun startSpeedTest() {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                _error.postValue(null)
                _testProgress.postValue(0)
                
                // Load servers
                _testProgress.postValue(10)
                val serversList = repository.getServers()
                _servers.postValue(serversList)
                
                // Load config
                _testProgress.postValue(20)
                val config = repository.getConfig()
                
                // Perform ping test
                _testProgress.postValue(30)
                val pingTime = repository.performPingTest()
                _ping.postValue(pingTime)
                
                // Perform download test
                _testProgress.postValue(50)
                val downloadSpeed = repository.performDownloadTest(10000)
                _downloadSpeed.postValue(downloadSpeed)
                
                // Perform upload test
                _testProgress.postValue(80)
                val uploadSpeed = repository.performUploadTest(10000)
                _uploadSpeed.postValue(uploadSpeed)
                
                // Save result to database
                _testProgress.postValue(90)
                val result = SpeedTestEntity(
                    downloadSpeed = downloadSpeed,
                    uploadSpeed = uploadSpeed,
                    ping = pingTime,
                    serverName = _selectedServer.value?.name,
                    serverCountry = _selectedServer.value?.country,
                    userIsp = config?.client?.isp
                )
                repository.saveSpeedTestResult(result)
                
                _testProgress.postValue(100)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error occurred")
                _isLoading.postValue(false)
            }
        }
    }
    
    fun selectServer(server: Server) {
        _selectedServer.postValue(server)
    }
    
    fun deleteResult(resultId: Long) {
        viewModelScope.launch {
            repository.deleteResult(resultId)
        }
    }
    
    fun clearAllResults() {
        viewModelScope.launch {
            repository.deleteAllResults()
        }
    }
}

class SpeedTestViewModelFactory(private val repository: SpeedTestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeedTestViewModel::class.java)) {
            return SpeedTestViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
