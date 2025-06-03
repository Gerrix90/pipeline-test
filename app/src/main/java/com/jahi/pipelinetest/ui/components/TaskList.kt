package com.jahi.pipelinetest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import com.jahi.pipelinetest.util.openDateTimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    eventId: Int,
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size
    val context = LocalContext.current

    var isAdding by remember { mutableStateOf(false) }
    var newDescription by remember { mutableStateOf("") }
    var newDueDate by remember { mutableStateOf("") }
    
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openDateTimePicker(context, newDueDate, true) { selected ->
                                    newDueDate = selected
                                }
                            }
                    ) {
                        OutlinedTextField(
                            value = newDueDate,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Due Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            isAdding = false
                            newDescription = ""
                            newDueDate = ""
                        }) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                if (newDescription.isNotBlank()) {
                                    taskViewModel.addTask(
                                        context,
                                        eventId,
                                        newDescription,
                                        newDueDate.ifBlank { null }
                                    )
                                    isAdding = false
                                    newDescription = ""
                                    newDueDate = ""
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
        
        // Task list
        if (tasks.isNotEmpty()) {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleCompletion = { taskViewModel.toggleTaskCompletion(context, task) },
                        onDelete = { taskViewModel.deleteTask(context, task) },
                        onUpdate = { updated -> taskViewModel.updateTask(context, updated) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Task) -> Unit,
    modifier: Modifier = Modifier,
    context: android.content.Context = LocalContext.current
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        var isEditing by remember { mutableStateOf(false) }
        var editText by remember { mutableStateOf(task.description) }
        var editDueDate by remember { mutableStateOf(task.dueDate ?: "") }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )

            if (isEditing) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                openDateTimePicker(context, editDueDate, true) { selected ->
                                    editDueDate = selected
                                }
                            }
                    ) {
                        OutlinedTextField(
                            value = editDueDate,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Due Date") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                IconButton(
                    onClick = {
                        onUpdate(
                            task.copy(
                                description = editText,
                                dueDate = editDueDate.ifBlank { null }
                            )
                        )
                        isEditing = false
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save task")
                }
                IconButton(
                    onClick = {
                        isEditing = false
                        editText = task.description
                        editDueDate = task.dueDate ?: ""
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel edit")
                }
            } else {
                Text(
                    text = task.description,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                task.dueDate?.let { due ->
                    Text(
                        text = due,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                IconButton(
                    onClick = {
                        editText = task.description
                        editDueDate = task.dueDate ?: ""
                        isEditing = true
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit task")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}