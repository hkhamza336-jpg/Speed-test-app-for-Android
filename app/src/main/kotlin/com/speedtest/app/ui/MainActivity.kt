package com.speedtest.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.speedtest.app.R
import com.speedtest.app.database.SpeedTestDatabase
import com.speedtest.app.repository.SpeedTestRepository
import com.speedtest.app.viewmodel.SpeedTestViewModel
import com.speedtest.app.viewmodel.SpeedTestViewModelFactory

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewModel: SpeedTestViewModel
    
    private lateinit var startButton: Button
    private lateinit var downloadSpeedText: TextView
    private lateinit var uploadSpeedText: TextView
    private lateinit var pingText: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var progressText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViewModel()
        initializeViews()
        observeViewModel()
        
        startButton.setOnClickListener {
            if (!viewModel.isLoading.value!!) {
                viewModel.startSpeedTest()
            }
        }
    }
    
    private fun initializeViewModel() {
        val database = SpeedTestDatabase.getInstance(this)
        val repository = SpeedTestRepository(database.speedTestDao())
        val factory = SpeedTestViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SpeedTestViewModel::class.java)
    }
    
    private fun initializeViews() {
        startButton = findViewById(R.id.start_button)
        downloadSpeedText = findViewById(R.id.download_speed)
        uploadSpeedText = findViewById(R.id.upload_speed)
        pingText = findViewById(R.id.ping)
        loadingIndicator = findViewById(R.id.loading_indicator)
        progressText = findViewById(R.id.progress_text)
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            startButton.isEnabled = !isLoading
            startButton.text = if (isLoading) "Testing..." else "Start Speed Test"
        }
        
        viewModel.downloadSpeed.observe(this) { speed ->
            downloadSpeedText.text = String.format("Download: %.2f Mbps", speed)
        }
        
        viewModel.uploadSpeed.observe(this) { speed ->
            uploadSpeedText.text = String.format("Upload: %.2f Mbps", speed)
        }
        
        viewModel.ping.observe(this) { ping ->
            pingText.text = String.format("Ping: %d ms", ping)
        }
        
        viewModel.testProgress.observe(this) { progress ->
            progressText.text = "Progress: $progress%"
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
