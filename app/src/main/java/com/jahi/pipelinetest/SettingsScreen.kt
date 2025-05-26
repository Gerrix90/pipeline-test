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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.model.CustomEvent
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

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

    val context = LocalContext.current

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
                            onValueChange = {},
                            label = { Text("Event Date (yyyy-MM-ddTHH:mm)") },
                            isError = !isValidDate(event.date),
                            supportingText = {
                                if (!isValidDate(event.date)) {
                                    Text("Use yyyy-MM-dd or yyyy-MM-ddTHH:mm")
                                }
                            },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openDateTimePicker(
                                        context,
                                        event.date
                                    ) { result ->
                                        events[index] = event.copy(date = result)
                                    }
                                }
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

private fun isValidDate(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return false
    return try {
        LocalDateTime.parse(trimmed)
        true
    } catch (e: DateTimeParseException) {
        try {
            LocalDate.parse(trimmed)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }
}

private fun openDateTimePicker(
    context: Context,
    initial: String,
    onResult: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val initialDateTime = try {
        LocalDateTime.parse(initial, formatter)
    } catch (_: Exception) {
        LocalDateTime.now()
    }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = LocalDate.of(year, month + 1, day)
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val dt = LocalDateTime.of(date, LocalTime.of(hour, minute))
                    onResult(dt.format(formatter))
                },
                initialDateTime.hour,
                initialDateTime.minute,
                true
            ).show()
        },
        initialDateTime.year,
        initialDateTime.monthValue - 1,
        initialDateTime.dayOfMonth
    ).show()
}
