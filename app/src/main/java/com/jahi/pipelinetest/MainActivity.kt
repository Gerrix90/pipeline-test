package com.jahi.pipelinetest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.stringResource
import com.jahi.pipelinetest.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.jahi.pipelinetest.ui.theme.PipelineTestTheme
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import com.jahi.pipelinetest.viewmodel.PlannerViewModel
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private val plannerViewModel: PlannerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            taskViewModel.allTasks.collect { tasks ->
                cancelTaskAlarms(this@MainActivity, tasks)
                scheduleTaskAlarms(this@MainActivity, tasks)
            }
        }
        
        // Check if launched from notification and navigate to Planner screen
        intent?.let { launchIntent ->
            if (launchIntent.getBooleanExtra(INTENT_EXTRA_NAVIGATE_TO_TASKS, false)) {
                // Navigate to Planner screen when launched from task notification
                mainViewModel.selectScreen(SCREEN_PLANNER)
            } else {
                val eventId = launchIntent.getIntExtra(EventAlarmReceiver.EXTRA_EVENT_ID, -1)
                if (eventId != -1) {
                    // Navigate to Planner screen when launched from event notification
                    mainViewModel.selectScreen(SCREEN_PLANNER)
                }
            }
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
        enableEdgeToEdge()
        setContent {
            PipelineTestTheme {
                if (mainViewModel.showSettings) {
                    SettingsScreen(mainViewModel) { mainViewModel.closeSettings() }
                } else if (mainViewModel.screen == SCREEN_GALLERY) {
                    // AI Gallery fullscreen without Time Fomo app bars
                    GalleryScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBackPressed = {
                            // Return to the previous screen (default to Countdowns)
                            mainViewModel.selectScreen(SCREEN_COUNTDOWNS)
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(com.jahi.pipelinetest.ui.theme.Slate900),
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(R.string.app_name)) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = com.jahi.pipelinetest.ui.theme.SurfaceDark.copy(alpha = 0.8f),
                                    titleContentColor = com.jahi.pipelinetest.ui.theme.Slate100
                                ),
                                actions = {
                                    IconButton(onClick = { mainViewModel.openSettings() }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                                NavigationBar(containerColor = com.jahi.pipelinetest.ui.theme.SurfaceDark.copy(alpha = 0.8f)) {
                                NavigationBarItem(
                                    selected = mainViewModel.screen == SCREEN_COUNTDOWNS,
                                    onClick = { mainViewModel.selectScreen(SCREEN_COUNTDOWNS) },
                                    label = { Text(stringResource(R.string.nav_countdowns)) },
                                    icon = { },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        selectedTextColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        unselectedIconColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        unselectedTextColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        indicatorColor = com.jahi.pipelinetest.ui.theme.Indigo600
                                    )
                                )
                                NavigationBarItem(
                                    selected = mainViewModel.screen == SCREEN_PLANNER,
                                    onClick = { mainViewModel.selectScreen(SCREEN_PLANNER) },
                                    label = { Text(stringResource(R.string.nav_planner)) },
                                    icon = { },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        selectedTextColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        unselectedIconColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        unselectedTextColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        indicatorColor = com.jahi.pipelinetest.ui.theme.Indigo600
                                    )
                                )
                                NavigationBarItem(
                                    selected = mainViewModel.screen == SCREEN_GALLERY,
                                    onClick = { mainViewModel.selectScreen(SCREEN_GALLERY) },
                                    label = { Text(stringResource(R.string.nav_gallery)) },
                                    icon = { },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        selectedTextColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        unselectedIconColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        unselectedTextColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        indicatorColor = com.jahi.pipelinetest.ui.theme.Indigo600
                                    )
                                )
                                NavigationBarItem(
                                    selected = mainViewModel.screen == SCREEN_LIFE,
                                    onClick = { mainViewModel.selectScreen(SCREEN_LIFE) },
                                    label = { Text(stringResource(R.string.nav_life)) },
                                    icon = { },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        selectedTextColor = com.jahi.pipelinetest.ui.theme.Slate100,
                                        unselectedIconColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        unselectedTextColor = com.jahi.pipelinetest.ui.theme.Slate400,
                                        indicatorColor = com.jahi.pipelinetest.ui.theme.Indigo600
                                    )
                                )
                            }
                        }
                    ) { innerPadding ->
                        when (mainViewModel.screen) {
                            SCREEN_COUNTDOWNS -> CountdownsScreen(
                                viewModel = mainViewModel, 
                                taskViewModel = taskViewModel,
                                onEventClick = { eventId ->
                                    // Navigate to planner screen when event is clicked
                                    mainViewModel.selectScreen(SCREEN_PLANNER)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            SCREEN_PLANNER -> PlannerScreen(plannerViewModel, taskViewModel, Modifier.padding(innerPadding))
                            SCREEN_LIFE -> LifeHourglassScreen(mainViewModel, Modifier.padding(innerPadding))
                            else -> CountdownsScreen(
                                viewModel = mainViewModel, 
                                taskViewModel = taskViewModel,
                                onEventClick = { eventId ->
                                    // Navigate to planner screen when event is clicked
                                    mainViewModel.selectScreen(SCREEN_PLANNER)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle intent when app is already running
        if (intent.getBooleanExtra(INTENT_EXTRA_NAVIGATE_TO_TASKS, false)) {
            // Navigate to Planner screen when launched from task notification
            mainViewModel.selectScreen(SCREEN_PLANNER)
        } else {
            val eventId = intent.getIntExtra(EventAlarmReceiver.EXTRA_EVENT_ID, -1)
            if (eventId != -1) {
                // Navigate to Planner screen when launched from event notification
                mainViewModel.selectScreen(SCREEN_PLANNER)
            }
        }
    }
    
    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
        
        // Screen indices
        private const val SCREEN_COUNTDOWNS = 0
        private const val SCREEN_PLANNER = 1
        private const val SCREEN_GALLERY = 2
        private const val SCREEN_LIFE = 3
        
        // Intent extras
        private const val INTENT_EXTRA_NAVIGATE_TO_TASKS = "navigateToTasks"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PipelineTestTheme {
        // Preview disabled due to Hilt dependency injection
        Text("Preview disabled - use device/emulator for testing")
    }
}
