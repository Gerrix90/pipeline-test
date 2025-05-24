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
fun SettingsDialog(prefs: Prefs, onDismiss: () -> Unit) {
    var showYear by remember { mutableStateOf(prefs.showYearCountdown) }
    var eventName by remember { mutableStateOf(prefs.eventName) }
    var eventDate by remember { mutableStateOf(prefs.eventDate) }
    var eventTime by remember { mutableStateOf(prefs.eventShowTime) }
    var currentAge by remember { mutableStateOf(prefs.currentAge.toString()) }
    var targetAge by remember { mutableStateOf(prefs.targetAge.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                prefs.showYearCountdown = showYear
                prefs.eventName = eventName
                prefs.eventDate = eventDate
                prefs.eventShowTime = eventTime
                prefs.currentAge = currentAge.toIntOrNull() ?: prefs.currentAge
                prefs.targetAge = targetAge.toIntOrNull() ?: prefs.targetAge
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
