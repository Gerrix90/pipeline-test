package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.ui.theme.Green500
import com.jahi.pipelinetest.ui.theme.Slate100
import com.jahi.pipelinetest.ui.theme.Slate400
import com.jahi.pipelinetest.ui.theme.Slate700
import com.jahi.pipelinetest.ui.theme.SurfaceDark
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
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
                    
                    // Send broadcast to update widget
                    val intent = Intent(EventCountdownWidget.ACTION_UPDATE_EVENT_WIDGET)
                    intent.setPackage(context.packageName)
                    context.sendBroadcast(intent)
                    
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
                    DarkTextField(
                        value = currentAge,
                        onValueChange = { currentAge = it },
                        label = { Text("Current Age") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DarkTextField(
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
                        DarkTextField(
                            value = event.name,
                            onValueChange = { events[index] = event.copy(name = it) },
                            label = { Text("Event Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    openDateTimePicker(
                                        context,
                                        event.date,
                                        event.showTime
                                    ) { selected ->
                                        events[index] = event.copy(date = selected)
                                    }
                                }
                        ) {
                            DarkTextField(
                                value = event.date,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text("Event Date") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        RowCheckbox("Show Time", event.showTime) {
                            events[index] = event.copy(showTime = it)
                        }
                        RowCheckbox("Show in Widget", event.showInWidget) {
                            events[index] = event.copy(showInWidget = it)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        enabled = enabled,
        label = label,
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = SurfaceDark,
            focusedBorderColor = Green500,
            unfocusedBorderColor = Slate700,
            disabledBorderColor = Slate700,
            cursorColor = Green500
        )
    )
}

private fun openDateTimePicker(
    context: android.content.Context,
    value: String,
    showTime: Boolean,
    onSelected: (String) -> Unit
) {
    var dateTime = run {
        try {
            LocalDateTime.parse(value)
        } catch (_: Exception) {
            try {
                LocalDate.parse(value).atStartOfDay()
            } catch (_: Exception) {
                LocalDateTime.now()
            }
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            dateTime = dateTime.withYear(year).withMonth(month + 1).withDayOfMonth(day)
            if (showTime) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        dateTime = dateTime.withHour(hour).withMinute(minute)
                        onSelected(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    },
                    dateTime.hour,
                    dateTime.minute,
                    true
                ).show()
            } else {
                onSelected(dateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
        },
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth
    ).show()
}
