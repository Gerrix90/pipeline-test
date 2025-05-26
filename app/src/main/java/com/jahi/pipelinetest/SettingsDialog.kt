package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var showYear by remember { mutableStateOf(viewModel.showYearCountdown) }
    var eventName by remember { mutableStateOf(viewModel.eventName) }
    var eventDate by remember { mutableStateOf(viewModel.eventDate) }
    var eventTime by remember { mutableStateOf(viewModel.eventShowTime) }
    var showCustomEvent by remember { mutableStateOf(viewModel.showCustomEvent) }
    var currentAge by remember { mutableStateOf(viewModel.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(viewModel.targetAge.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val initialMillis = try {
        val dt = LocalDateTime.parse(eventDate, formatter)
        dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                viewModel.showYearCountdown = showYear
                viewModel.eventName = eventName
                viewModel.eventDate = eventDate
                viewModel.eventShowTime = eventTime
                viewModel.showCustomEvent = showCustomEvent
                viewModel.currentAge = currentAge.toIntOrNull() ?: viewModel.currentAge
                viewModel.targetAge = targetAge.toIntOrNull() ?: viewModel.targetAge
                onDismiss()
            }) { Text("Save") }
        },
        title = { Text("Settings") },
        text = {
            Column {
                RowCheckbox(label = "Show Year Countdown", checked = showYear) { showYear = it }
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Event Date (yyyy-MM-ddTHH:mm)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick date"
                            )
                        }
                    }
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    val date = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    val time = try {
                                        LocalDateTime.parse(eventDate, formatter).toLocalTime()
                                    } catch (e: Exception) {
                                        LocalTime.MIDNIGHT
                                    }
                                    val dt = LocalDateTime.of(date, time)
                                    eventDate = dt.format(formatter)
                                }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                RowRadioButtons(
                    label = "Display Custom Event",
                    selected = showCustomEvent,
                    onSelected = { showCustomEvent = it }
                )
                RowCheckbox(label = "Show Event Time", checked = eventTime) { eventTime = it }
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
        }
    )
}

@Composable
private fun RowCheckbox(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(text = label)
    }
}

@Composable
private fun RowRadioButtons(label: String, selected: Boolean, onSelected: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(text = label, modifier = Modifier.padding(end = 8.dp))
        RadioButton(selected = selected, onClick = { onSelected(true) })
        Text(text = "Show", modifier = Modifier.padding(end = 8.dp))
        RadioButton(selected = !selected, onClick = { onSelected(false) })
        Text(text = "Hide")
    }
}
