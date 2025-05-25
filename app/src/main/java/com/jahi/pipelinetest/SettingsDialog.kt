package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var showYear by remember { mutableStateOf(viewModel.showYearCountdown) }
    var eventName by remember { mutableStateOf(viewModel.eventName) }
    var eventDate by remember { mutableStateOf(viewModel.eventDate) }
    var eventTime by remember { mutableStateOf(viewModel.eventShowTime) }
    var currentAge by remember { mutableStateOf(viewModel.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(viewModel.targetAge.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                viewModel.showYearCountdown = showYear
                viewModel.eventName = eventName
                viewModel.eventDate = eventDate
                viewModel.eventShowTime = eventTime
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
                    modifier = Modifier.fillMaxWidth()
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
