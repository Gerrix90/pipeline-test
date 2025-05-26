package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.model.CustomEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var tabIndex by remember { mutableStateOf(0) }
    var showYear by remember { mutableStateOf(viewModel.showYearCountdown) }
    var currentAge by remember { mutableStateOf(viewModel.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(viewModel.targetAge.toString()) }

    val events = remember {
        mutableStateListOf<CustomEvent>().also { it.addAll(viewModel.events) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(modifier = Modifier.padding(16.dp)) {
                Button(onClick = {
                    viewModel.showYearCountdown = showYear
                    viewModel.currentAge = currentAge.toIntOrNull() ?: viewModel.currentAge
                    viewModel.targetAge = targetAge.toIntOrNull() ?: viewModel.targetAge
                    viewModel.setEvents(events)
                    onDismiss()
                }) { Text("Save") }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) { Text("General") }
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) { Text("Events") }
            }
            if (tabIndex == 0) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RowCheckbox("Show Year Countdown", showYear) { showYear = it }
                    TextField(
                        value = currentAge,
                        onValueChange = { currentAge = it },
                        label = { Text("Current Age") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = targetAge,
                        onValueChange = { targetAge = it },
                        label = { Text("Target Age") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    events.forEachIndexed { index, event ->
                        TextField(
                            value = event.name,
                            onValueChange = { events[index] = event.copy(name = it) },
                            label = { Text("Event Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = event.date,
                            onValueChange = { events[index] = event.copy(date = it) },
                            label = { Text("Event Date (yyyy-MM-ddTHH:mm)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        RowCheckbox("Show Time", event.showTime) {
                            events[index] = event.copy(showTime = it)
                        }
                        Button(onClick = { events.removeAt(index) }) {
                            Text("Remove")
                        }
                    }
                    Button(onClick = { events.add(CustomEvent()) }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Add Event")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowCheckbox(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(text = label)
    }
}
