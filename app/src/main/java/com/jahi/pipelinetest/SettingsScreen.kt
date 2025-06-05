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
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import com.jahi.pipelinetest.util.openDateTimePicker
import com.jahi.pipelinetest.scheduleEventAlarms
import com.jahi.pipelinetest.cancelEventAlarms
import com.jahi.pipelinetest.parseEventDateTimeOrNull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.ui.theme.Green500
import com.jahi.pipelinetest.ui.theme.Green600
import com.jahi.pipelinetest.ui.theme.OutlineDark
import com.jahi.pipelinetest.ui.theme.SurfaceDark
import com.jahi.pipelinetest.ui.theme.Slate400
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var tabIndex by remember { mutableStateOf(0) }
    var showYear by remember { mutableStateOf(viewModel.showYearCountdown) }
    var currentAge by remember { mutableStateOf(viewModel.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(viewModel.targetAge.toString()) }
    var elevenLabsApiKey by remember { mutableStateOf(viewModel.elevenLabsApiKey) }

    val events = remember {
        mutableStateListOf<CustomEvent>().also { it.addAll(viewModel.events) }
    }

    BackHandler { onDismiss() }

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
                Button(
                    onClick = {
                        viewModel.showYearCountdown = showYear
                        viewModel.currentAge = currentAge.toIntOrNull() ?: viewModel.currentAge
                        viewModel.targetAge = targetAge.toIntOrNull() ?: viewModel.targetAge
                        viewModel.elevenLabsApiKey = elevenLabsApiKey
                        val oldEvents = viewModel.events.toList()
                        viewModel.setEvents(events)

                        // Send broadcast to update widget
                        val intent = Intent(EventCountdownWidget.ACTION_UPDATE_EVENT_WIDGET)
                        intent.setPackage(context.packageName)
                        context.sendBroadcast(intent)

                        cancelEventAlarms(context, oldEvents)
                        val scheduled = scheduleEventAlarms(context, events)
                        if (scheduled > 0) {
                            android.widget.Toast.makeText(
                                context,
                                context.getString(R.string.events_scheduled),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }

                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green500,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) { Text("Save") }
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
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }) { Text("History") }
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
                    DarkTextField(
                        value = elevenLabsApiKey,
                        onValueChange = { elevenLabsApiKey = it },
                        label = { Text("ElevenLabs API Key (for audio generation)") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            } else if (tabIndex == 1) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val currentInstant by viewModel.now.collectAsState()
                    val currentLocalDateTimeForEditCheck = remember(currentInstant) {
                        LocalDateTime.ofInstant(currentInstant, ZoneId.systemDefault())
                    }

                    events.forEachIndexed { index, event ->
                        val time = parseEventDateTimeOrNull(event.date)
                        val editable = time == null || time.isAfter(currentLocalDateTimeForEditCheck)

                        DarkTextField(
                            value = event.name,
                            onValueChange = { events[index] = event.copy(name = it) },
                            label = { Text("Event Name") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = !editable,
                            enabled = editable
                        )
                        val context = LocalContext.current
                        Box(
                            modifier = if (editable) {
                                Modifier
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
                            } else {
                                Modifier.fillMaxWidth()
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

                        RowCheckbox("Show Time", event.showTime, enabled = editable) {
                            events[index] = event.copy(showTime = it)
                        }
                        RowCheckbox("Show in Widget", event.showInWidget, enabled = editable) {
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
            } else {
                val nowInstant by viewModel.now.collectAsState()
                val nowLocal = remember(nowInstant) {
                    LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault())
                }
                val allEventsParsedAndSorted = remember(events) {
                    events
                        .mapNotNull { ev ->
                            parseEventDateTimeOrNull(ev.date)?.let { time -> ev to time }
                        }
                        .sortedByDescending { (_, time) -> time }
                }
                val pastEvents = remember(allEventsParsedAndSorted, nowLocal) {
                    allEventsParsedAndSorted.filter { (_, time) -> time.isBefore(nowLocal) }
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (pastEvents.isEmpty()) {
                        Text("No past events", color = Slate400)
                    } else {
                        val zone = ZoneId.systemDefault()
                        pastEvents.forEach { (event, time) ->
                            val formattedDate = remember(time) {
                                time.atZone(zone).format(
                                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                )
                            }
                            Text(event.name)
                            Text(
                                formattedDate,
                                color = Slate400,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowCheckbox(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onChecked: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { if (enabled) onChecked(it) },
            enabled = enabled
        )
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedBorderColor = Green500,
            unfocusedBorderColor = OutlineDark, // Use OutlineDark instead of Slate700 for accessibility
            disabledBorderColor = OutlineDark,  // Use OutlineDark instead of Slate700 for accessibility
            cursorColor = Green500
        )
    )
}

