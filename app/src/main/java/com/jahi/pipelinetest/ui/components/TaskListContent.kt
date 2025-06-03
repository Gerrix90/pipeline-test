package com.jahi.pipelinetest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import com.jahi.pipelinetest.util.openDateTimePicker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListContent(
    eventId: Int,
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size

    var isAdding by remember { mutableStateOf(false) }
    var newDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Handle date picker
    if (showDatePicker) {
        openDateTimePicker(
            context = context,
            value = selectedDate ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            showTime = true,
            onSelected = { dateTime ->
                selectedDate = dateTime
                showDatePicker = false
            }
        )
    }
    
    Column(modifier = modifier) {
        // Task progress header
        if (totalTasks > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasks ($completedTasks/$totalTasks)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp)
                )
            }
        } else {
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Add task button
        if (isAdding) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Task description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Due date selection
                    val displayDate = selectedDate?.let {
                        try {
                            val dateTime = LocalDateTime.parse(it)
                            dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        } catch (e: Exception) {
                            it
                        }
                    } ?: ""
                    
                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = { },
                        label = { Text("Due date (optional)") },
                        placeholder = { Text("Select date and time") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        trailingIcon = {
                            Row {
                                if (selectedDate != null) {
                                    IconButton(onClick = { selectedDate = null }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear date"
                                        )
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            }
                        }
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            isAdding = false
                            newDescription = ""
                            selectedDate = null
                        }) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                if (newDescription.isNotBlank()) {
                                    taskViewModel.addTask(eventId, newDescription, selectedDate)
                                    isAdding = false
                                    newDescription = ""
                                    selectedDate = null
                                }
                            },
                            enabled = newDescription.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        } else {
            Button(
                onClick = { isAdding = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Task")
            }
        }
        
        // Task items (no LazyColumn here)
        tasks.forEach { task ->
            TaskItem(
                task = task,
                onToggleCompletion = { taskViewModel.toggleTaskCompletion(task) },
                onDelete = { taskViewModel.deleteTask(task) },
                onUpdate = { updated -> taskViewModel.updateTask(updated) },
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}