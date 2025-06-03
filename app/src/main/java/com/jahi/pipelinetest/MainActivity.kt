package com.jahi.pipelinetest

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jahi.pipelinetest.ui.theme.PipelineTestTheme
import com.jahi.pipelinetest.SettingsScreen
import com.jahi.pipelinetest.repository.TaskRepository
import com.jahi.pipelinetest.domain.*
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import com.jahi.pipelinetest.viewmodel.TaskViewModelFactory
import com.jahi.pipelinetest.TaskOverviewScreen
import com.jahi.pipelinetest.scheduleTaskAlarms
import com.jahi.pipelinetest.cancelTaskAlarms
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = Prefs(this)
        val viewModel = ViewModelProvider(this, MainViewModelFactory(prefs))[MainViewModel::class.java]
        
        // Initialize task dependencies
        val taskRepository = TaskRepository(prefs)
        val createTaskUseCase = CreateTaskUseCase(taskRepository)
        val getTasksUseCase = GetTasksUseCase(taskRepository)
        val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
        val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
        val toggleTaskCompletionUseCase = ToggleTaskCompletionUseCase(taskRepository)
        
        val taskViewModelFactory = TaskViewModelFactory(
            createTaskUseCase,
            getTasksUseCase,
            updateTaskUseCase,
            deleteTaskUseCase,
            toggleTaskCompletionUseCase,
            taskRepository
        )
        val taskViewModel = ViewModelProvider(this, taskViewModelFactory)[TaskViewModel::class.java]

        lifecycleScope.launch {
            taskViewModel.allTasks.collect { tasks ->
                cancelTaskAlarms(this@MainActivity, tasks)
                scheduleTaskAlarms(this@MainActivity, tasks)
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
                if (viewModel.showSettings) {
                    SettingsScreen(viewModel) { viewModel.closeSettings() }
                } else {
                    val gradient = remember {
                        Brush.linearGradient(
                            listOf(
                                com.jahi.pipelinetest.ui.theme.Slate900,
                                com.jahi.pipelinetest.ui.theme.Slate800
                            )
                        )
                    }
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(gradient),
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "Time Fomo") },
                                colors = TopAppBarDefaults.smallTopAppBarColors(
                                    containerColor = com.jahi.pipelinetest.ui.theme.SurfaceDark.copy(alpha = 0.8f),
                                    titleContentColor = com.jahi.pipelinetest.ui.theme.Slate100
                                ),
                                actions = {
                                    IconButton(onClick = { viewModel.openSettings() }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar(containerColor = com.jahi.pipelinetest.ui.theme.SurfaceDark.copy(alpha = 0.8f)) {
                                NavigationBarItem(
                                    selected = viewModel.screen == 0,
                                    onClick = { viewModel.selectScreen(0) },
                                    label = { Text("Countdowns") },
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
                                    selected = viewModel.screen == 1,
                                    onClick = { viewModel.selectScreen(1) },
                                    label = { Text("Life") },
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
                                    selected = viewModel.screen == 2,
                                    onClick = { viewModel.selectScreen(2) },
                                    label = { Text("Tasks") },
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
                        when (viewModel.screen) {
                            0 -> CountdownsScreen(viewModel, taskViewModel, Modifier.padding(innerPadding))
                            1 -> LifeHourglassScreen(viewModel, Modifier.padding(innerPadding))
                            else -> TaskOverviewScreen(viewModel, taskViewModel, Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PipelineTestTheme {
        val context = LocalContext.current
        val vm = remember { MainViewModel(Prefs(context)) }
        CountdownsScreen(vm)
    }
}
