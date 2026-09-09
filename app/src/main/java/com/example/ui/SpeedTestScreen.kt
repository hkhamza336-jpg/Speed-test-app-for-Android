package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SpeedTestRecord
import com.example.engine.SpeedTestState
import com.example.engine.TestPhase
import com.example.ui.components.SpeedometerGauge
import com.example.ui.theme.SpeedAmber
import com.example.ui.theme.SpeedCyan
import com.example.ui.theme.SpeedDarkBg
import com.example.ui.theme.SpeedDarkCard
import com.example.ui.theme.SpeedDarkCardStroke
import com.example.ui.theme.SpeedEmerald
import com.example.ui.theme.SpeedMagenta
import com.example.ui.theme.SpeedNeonBlue
import com.example.ui.theme.SpeedRose
import com.example.ui.theme.SpeedSurfaceElevated
import com.example.ui.theme.SpeedTextMuted
import com.example.ui.theme.SpeedTextPrimary
import com.example.ui.theme.SpeedTextSecondary
import com.example.ui.theme.SpeedViolet
import com.example.util.NetworkStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpeedTestScreen(viewModel: SpeedTestViewModel) {
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val testState by viewModel.testState.collectAsStateWithLifecycle()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()
    val selectedUnit by viewModel.selectedUnit.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalTestsCount.collectAsStateWithLifecycle()
    val maxDownload by viewModel.maxDownloadSpeed.collectAsStateWithLifecycle()

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("speed_test_scaffold"),
        containerColor = SpeedDarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = SpeedDarkCard,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("app_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentMode == AppMode.SPEED_TEST,
                    onClick = { viewModel.setMode(AppMode.SPEED_TEST) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Speed Test") },
                    label = { Text("Speed Test") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpeedDarkBg,
                        selectedTextColor = SpeedCyan,
                        indicatorColor = SpeedCyan,
                        unselectedIconColor = SpeedTextMuted,
                        unselectedTextColor = SpeedTextMuted
                    ),
                    modifier = Modifier.testTag("nav_speed_test")
                )
                NavigationBarItem(
                    selected = currentMode == AppMode.HISTORY,
                    onClick = { viewModel.setMode(AppMode.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpeedDarkBg,
                        selectedTextColor = SpeedCyan,
                        indicatorColor = SpeedCyan,
                        unselectedIconColor = SpeedTextMuted,
                        unselectedTextColor = SpeedTextMuted
                    ),
                    modifier = Modifier.testTag("nav_history")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Brand Logo & Title Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .testTag("app_brand_logo_header"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_speedtest_gauge_logo),
                    contentDescription = "Speed Test Logo",
                    tint = SpeedCyan,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SPEED TEST",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = SpeedTextPrimary,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Header: Branding & Network Info Card
            HeaderSection(
                networkStatus = networkStatus,
                selectedUnit = selectedUnit,
                onUnitSelected = { viewModel.setUnit(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body content based on active mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentMode) {
                    AppMode.SPEED_TEST -> {
                        SpeedTestTabContent(
                            testState = testState,
                            networkStatus = networkStatus,
                            selectedUnit = selectedUnit,
                            onStartTest = { viewModel.startSpeedTest() },
                            onCancelTest = { viewModel.cancelSpeedTest() },
                            onResetTest = { viewModel.resetTest() }
                        )
                    }

                    AppMode.HISTORY -> {
                        HistoryTabContent(
                            records = historyRecords,
                            totalCount = totalCount,
                            maxDownload = maxDownload,
                            selectedUnit = selectedUnit,
                            onDeleteRecord = { viewModel.deleteHistoryRecord(it) },
                            onRequestClearAll = { showClearHistoryDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All History?", color = SpeedTextPrimary) },
            text = { Text("This will permanently delete all past speed test records.", color = SpeedTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_history_button")
                ) {
                    Text("Delete All", color = SpeedRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = SpeedTextSecondary)
                }
            },
            containerColor = SpeedDarkCard
        )
    }
}

@Composable
private fun HeaderSection(
    networkStatus: NetworkStatus,
    selectedUnit: SpeedUnit,
    onUnitSelected: (SpeedUnit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .testTag("header_network_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
        border = BorderStroke(1.dp, SpeedDarkCardStroke)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (networkStatus.isConnected) SpeedCyan.copy(alpha = 0.15f) else SpeedRose.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (networkStatus.connectionType == "Wi-Fi") Icons.Default.Wifi else Icons.Default.SignalCellularAlt,
                        contentDescription = "Connection Type",
                        tint = if (networkStatus.isConnected) SpeedCyan else SpeedRose,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ISP: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpeedCyan
                        )
                        Text(
                            text = networkStatus.ispName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpeedTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val displayIp = if (networkStatus.publicIp != "---") networkStatus.publicIp else networkStatus.ipAddress
                    val locationPart = if (networkStatus.serverOrLocation.isNotBlank()) " • ${networkStatus.serverOrLocation}" else ""
                    Text(
                        text = "${networkStatus.connectionType} • IP $displayIp$locationPart",
                        fontSize = 11.sp,
                        color = SpeedTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Unit toggle pills
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpeedSurfaceElevated)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpeedUnit.values().take(2).forEach { unit ->
                    val isSelected = selectedUnit == unit
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) SpeedCyan else Color.Transparent)
                            .clickable { onUnitSelected(unit) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unit.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SpeedDarkBg else SpeedTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedTestTabContent(
    testState: SpeedTestState,
    networkStatus: NetworkStatus,
    selectedUnit: SpeedUnit,
    onStartTest: () -> Unit,
    onCancelTest: () -> Unit,
    onResetTest: () -> Unit
) {
    val isRunning = testState.phase in listOf(TestPhase.PING, TestPhase.DOWNLOAD, TestPhase.UPLOAD)
    val isCompleted = testState.phase == TestPhase.COMPLETED

    val activeColor = when (testState.phase) {
        TestPhase.DOWNLOAD -> SpeedCyan
        TestPhase.UPLOAD -> SpeedViolet
        TestPhase.COMPLETED -> SpeedEmerald
        else -> SpeedNeonBlue
    }

    val phaseLabel = when (testState.phase) {
        TestPhase.PING -> "Testing Ping & Jitter"
        TestPhase.DOWNLOAD -> "Testing Download Speed"
        TestPhase.UPLOAD -> "Testing Upload Speed"
        TestPhase.COMPLETED -> "Test Complete"
        TestPhase.ERROR -> "Test Interrupted"
        else -> "Ready To Test"
    }

    val displaySpeed = (testState.currentSpeedMbps * selectedUnit.multiplier)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .testTag("speed_test_content"),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Metric Telemetry Cards (Ping, Download, Upload)
        item {
            TelemetryRow(
                testState = testState,
                selectedUnit = selectedUnit
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Speedometer Dial
        item {
            SpeedometerGauge(
                currentSpeedMbps = testState.currentSpeedMbps,
                label = phaseLabel,
                unit = selectedUnit.label,
                accentColor = activeColor
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Dedicated Internet Service Provider & Server Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("isp_server_info_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
                border = BorderStroke(1.dp, SpeedDarkCardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: ISP Info
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SpeedCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (networkStatus.connectionType == "Wi-Fi") Icons.Default.Wifi else Icons.Default.Public,
                                contentDescription = "ISP",
                                tint = SpeedCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "INTERNET PROVIDER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpeedTextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = networkStatus.ispName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpeedTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val ispSub = if (networkStatus.serverOrLocation.isNotBlank()) {
                                networkStatus.serverOrLocation
                            } else {
                                networkStatus.carrierOrWifi
                            }
                            Text(
                                text = ispSub,
                                fontSize = 10.sp,
                                color = SpeedTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right: Server Info
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SpeedViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = "Server",
                                tint = SpeedViolet,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TEST SERVER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpeedTextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = testState.serverName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpeedTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val ipSub = if (networkStatus.publicIp != "---") "IP ${networkStatus.publicIp}" else "Edge CDN"
                            Text(
                                text = ipSub,
                                fontSize = 10.sp,
                                color = SpeedTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Progress indicator
        if (isRunning) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { testState.overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .testTag("test_progress_bar"),
                        color = activeColor,
                        trackColor = SpeedSurfaceElevated
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        // Primary Action Button (GO / Cancel / Test Again)
        item {
            if (!isRunning && !isCompleted) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SpeedCyan, SpeedNeonBlue, SpeedDarkCard)
                            )
                        )
                        .clickable { onStartTest() }
                        .testTag("start_test_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(SpeedDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "GO",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = SpeedCyan,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "START",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpeedTextMuted
                            )
                        }
                    }
                }
            } else if (isRunning) {
                Button(
                    onClick = onCancelTest,
                    colors = ButtonDefaults.buttonColors(containerColor = SpeedSurfaceElevated),
                    border = BorderStroke(1.dp, SpeedRose.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("cancel_test_button")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Cancel", tint = SpeedRose)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stop Test", color = SpeedRose, fontWeight = FontWeight.Bold)
                }
            } else if (isCompleted) {
                // Completed Summary Card
                CompletedResultsCard(
                    testState = testState,
                    networkStatus = networkStatus,
                    selectedUnit = selectedUnit,
                    onTestAgain = onResetTest
                )
            }
        }
    }
}

@Composable
private fun TelemetryRow(
    testState: SpeedTestState,
    selectedUnit: SpeedUnit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("telemetry_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Ping Card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
            border = BorderStroke(1.dp, SpeedDarkCardStroke)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SpeedTextMuted)
                Text(
                    text = if (testState.pingMs > 0) "${testState.pingMs} ms" else "---",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (testState.pingMs > 0) SpeedEmerald else SpeedTextSecondary
                )
                Text(
                    text = if (testState.jitterMs > 0) "±${testState.jitterMs} ms" else "Jitter",
                    fontSize = 9.sp,
                    color = SpeedTextMuted
                )
            }
        }

        // Download Card
        Card(
            modifier = Modifier.weight(1.2f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
            border = BorderStroke(
                1.dp,
                if (testState.phase == TestPhase.DOWNLOAD) SpeedCyan else SpeedDarkCardStroke
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = "Download",
                        tint = SpeedCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("DOWNLOAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SpeedCyan)
                }
                val dlVal = if (testState.downloadMbps > 0) {
                    testState.downloadMbps * selectedUnit.multiplier
                } else if (testState.phase == TestPhase.DOWNLOAD) {
                    testState.currentSpeedMbps * selectedUnit.multiplier
                } else {
                    0.0
                }
                Text(
                    text = if (dlVal > 0) String.format(Locale.US, "%.1f", dlVal) else "---",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SpeedTextPrimary
                )
                Text(selectedUnit.label, fontSize = 9.sp, color = SpeedTextMuted)
            }
        }

        // Upload Card
        Card(
            modifier = Modifier.weight(1.2f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
            border = BorderStroke(
                1.dp,
                if (testState.phase == TestPhase.UPLOAD) SpeedViolet else SpeedDarkCardStroke
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Upload",
                        tint = SpeedViolet,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("UPLOAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SpeedViolet)
                }
                val ulVal = if (testState.uploadMbps > 0) {
                    testState.uploadMbps * selectedUnit.multiplier
                } else if (testState.phase == TestPhase.UPLOAD) {
                    testState.currentSpeedMbps * selectedUnit.multiplier
                } else {
                    0.0
                }
                Text(
                    text = if (ulVal > 0) String.format(Locale.US, "%.1f", ulVal) else "---",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SpeedTextPrimary
                )
                Text(selectedUnit.label, fontSize = 9.sp, color = SpeedTextMuted)
            }
        }
    }
}

@Composable
private fun CompletedResultsCard(
    testState: SpeedTestState,
    networkStatus: NetworkStatus,
    selectedUnit: SpeedUnit,
    onTestAgain: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_completed_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
        border = BorderStroke(1.dp, SpeedEmerald.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = SpeedEmerald, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BENCHMARK COMPLETED",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpeedEmerald,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ISP & Server Result Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SpeedSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Public, contentDescription = "ISP", tint = SpeedCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ISP: ${networkStatus.ispName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpeedTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = networkStatus.connectionType,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SpeedTextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suitability capabilities
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CapabilityBadge(
                    label = "4K Streaming",
                    isGood = testState.downloadMbps >= 25.0
                )
                CapabilityBadge(
                    label = "Gaming Latency",
                    isGood = testState.pingMs in 1..40
                )
                CapabilityBadge(
                    label = "Video Calls",
                    isGood = testState.uploadMbps >= 5.0 && testState.pingMs <= 80
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTestAgain,
                colors = ButtonDefaults.buttonColors(containerColor = SpeedCyan),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_again_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Test Again", tint = SpeedDarkBg)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Again", color = SpeedDarkBg, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CapabilityBadge(label: String, isGood: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isGood) SpeedEmerald.copy(alpha = 0.2f) else SpeedAmber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isGood) Icons.Default.Check else Icons.Default.NetworkCheck,
                contentDescription = label,
                tint = if (isGood) SpeedEmerald else SpeedAmber,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = SpeedTextSecondary)
    }
}

@Composable
private fun HistoryTabContent(
    records: List<SpeedTestRecord>,
    totalCount: Int,
    maxDownload: Double?,
    selectedUnit: SpeedUnit,
    onDeleteRecord: (Long) -> Unit,
    onRequestClearAll: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .testTag("history_content"),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Stats Summary Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
                border = BorderStroke(1.dp, SpeedDarkCardStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOTAL TESTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SpeedTextMuted)
                        Text("$totalCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SpeedTextPrimary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BEST DOWNLOAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SpeedTextMuted)
                        val bestDl = (maxDownload ?: 0.0) * selectedUnit.multiplier
                        Text(
                            text = String.format(Locale.US, "%.1f %s", bestDl, selectedUnit.label),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SpeedCyan
                        )
                    }

                    if (records.isNotEmpty()) {
                        IconButton(
                            onClick = onRequestClearAll,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("clear_all_history_button")
                        ) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear All", tint = SpeedRose)
                        }
                    }
                }
            }
        }

        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = SpeedTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Speed Tests Yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SpeedTextPrimary)
                        Text("Run a speed test to track your connection history.", fontSize = 12.sp, color = SpeedTextSecondary)
                    }
                }
            }
        } else {
            items(records, key = { it.id }) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_record_card_${record.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SpeedDarkCard),
                    border = BorderStroke(1.dp, SpeedDarkCardStroke)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "ISP",
                                    tint = SpeedCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = record.ispOrServer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpeedCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${record.connectionType} • ${record.rating}",
                                    fontSize = 11.sp,
                                    color = SpeedTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Download
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = SpeedCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    val dl = record.downloadSpeedMbps * selectedUnit.multiplier
                                    Text(
                                        text = String.format(Locale.US, "%.1f %s", dl, selectedUnit.label),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SpeedTextPrimary
                                    )
                                }

                                // Upload
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = SpeedViolet, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    val ul = record.uploadSpeedMbps * selectedUnit.multiplier
                                    Text(
                                        text = String.format(Locale.US, "%.1f %s", ul, selectedUnit.label),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SpeedTextPrimary
                                    )
                                }

                                // Ping
                                Text(
                                    text = "${record.pingMs} ms",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SpeedEmerald
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateFormat.format(Date(record.timestamp)),
                                fontSize = 10.sp,
                                color = SpeedTextMuted
                            )
                        }

                        IconButton(
                            onClick = { onDeleteRecord(record.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("delete_record_${record.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SpeedTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
