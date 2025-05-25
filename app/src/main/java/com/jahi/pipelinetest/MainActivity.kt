package com.jahi.pipelinetest

import android.os.Bundle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jahi.pipelinetest.ui.theme.PipelineTestTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = Prefs(this)
        enableEdgeToEdge()
        setContent {
            PipelineTestTheme {
                var screen by rememberSaveable { mutableStateOf(0) }
                var showSettings by rememberSaveable { mutableStateOf(false) }
                val gradient = remember { Brush.linearGradient(listOf(com.jahi.pipelinetest.ui.theme.Slate900, com.jahi.pipelinetest.ui.theme.Slate800)) }
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
                                IconButton(onClick = { showSettings = true }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = com.jahi.pipelinetest.ui.theme.SurfaceDark.copy(alpha = 0.8f)) {
                            NavigationBarItem(
                                selected = screen == 0,
                                onClick = { screen = 0 },
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
                                selected = screen == 1,
                                onClick = { screen = 1 },
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
                        }
                    }
                ) { innerPadding ->
                    if (screen == 0) {
                        CountdownsScreen(prefs, Modifier.padding(innerPadding))
                    } else {
                        LifeHourglassScreen(prefs, Modifier.padding(innerPadding))
                    }
                    if (showSettings) {
                        SettingsDialog(prefs) { showSettings = false }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PipelineTestTheme {
        CountdownsScreen(Prefs(androidx.compose.ui.platform.LocalContext.current))
    }
}
