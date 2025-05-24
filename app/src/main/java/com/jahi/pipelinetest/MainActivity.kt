package com.jahi.pipelinetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Time Fomo") },
                            actions = {
                                IconButton(onClick = { showSettings = true }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = screen == 0,
                                onClick = { screen = 0 },
                                label = { Text("Countdowns") },
                                icon = { }
                            )
                            NavigationBarItem(
                                selected = screen == 1,
                                onClick = { screen = 1 },
                                label = { Text("Life") },
                                icon = { }
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
