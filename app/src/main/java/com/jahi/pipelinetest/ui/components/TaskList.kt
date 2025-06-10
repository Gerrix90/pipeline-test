package com.jahi.pipelinetest.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.R
import com.jahi.pipelinetest.model.Task
import com.jahi.pipelinetest.util.openDateTimePicker
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    eventId: Int,
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    useLazyList: Boolean = true
) {
    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size

    var isAdding by remember { mutableStateOf(false) }
    var newDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (showDatePicker) {
        openDateTimePicker(
            context = context,
            value = selectedDate ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            showTime = true
        ) { dateTime ->
            selectedDate = dateTime
            showDatePicker = false
        }
    }

    Column(modifier = modifier) {
        if (totalTasks > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.tasks_progress,
                        completedTasks,
                        totalTasks
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.task_progress_width))
                        .height(dimensionResource(R.dimen.task_progress_height))
                )
            }
        } else {
            Text(
                text = stringResource(R.string.title_tasks),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
            )
        }

        if (isAdding) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_small)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_regular))
                ) {
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text(stringResource(R.string.task_description_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val displayDate = selectedDate?.let {
                        try {
                            val dateTime = LocalDateTime.parse(it)
                            dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                        } catch (e: Exception) {
                            it
                        }
                    } ?: ""

                    val interactionSource = remember { MutableInteractionSource() }

                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showDatePicker = true
                            }
                        }
                    }

                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = { },
                        label = { Text(stringResource(R.string.task_due_date_optional_label)) },
                        placeholder = { Text(stringResource(R.string.task_select_date_time)) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.padding_small)),
                        interactionSource = interactionSource,
                        trailingIcon = {
                            Row {
                                if (selectedDate != null) {
                                    IconButton(onClick = { selectedDate = null }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.clear_date)
                                        )
                                    }
                                }
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = stringResource(R.string.select_date)
                                    )
                                }
                            }
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            isAdding = false
                            newDescription = ""
                            selectedDate = null
                        }) {
                            Text(stringResource(R.string.cancel))
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
                            Text(stringResource(R.string.action_add))
                        }
                    }
                }
            }
        } else {
            Button(
                onClick = { isAdding = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_small))
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.task_add))
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                Text(stringResource(R.string.task_add))
            }
        }

        if (useLazyList) {
            if (tasks.isNotEmpty()) {
                LazyColumn {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = { taskViewModel.toggleTaskCompletion(task) },
                            onDelete = { taskViewModel.deleteTask(task) },
                            onUpdate = { updated -> taskViewModel.updateTask(updated) }
                        )
                    }
                }
            }
        } else {
            tasks.forEach { task ->
                TaskItem(
                    task = task,
                    onToggleCompletion = { taskViewModel.toggleTaskCompletion(task) },
                    onDelete = { taskViewModel.deleteTask(task) },
                    onUpdate = { updated -> taskViewModel.updateTask(updated) },
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_xxsmall))
                )
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
            .padding(vertical = dimensionResource(R.dimen.padding_xxsmall)),
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_regular))
        ) {
            // First row: Checkbox and task description/edit field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion() }
                )

                if (isEditing) {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = dimensionResource(R.dimen.padding_small)),
                        label = { Text(stringResource(R.string.task_description_label)) }
                    )
                } else {
                    Text(
                        text = task.description,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = dimensionResource(R.dimen.padding_small)),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            Color(0xFFE0E0E0)
                    )
                }
            }

            // Second row: Due date (editing or display)
            if (isEditing) {
                val displayEditDate = editDueDate.takeIf { it.isNotBlank() }?.let {
                    try {
                        val dateTime = LocalDateTime.parse(it)
                        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                    } catch (e: Exception) {
                        it
                    }
                } ?: ""

                val dateInteractionSource = remember { MutableInteractionSource() }

                LaunchedEffect(dateInteractionSource) {
                    dateInteractionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            openDateTimePicker(context, editDueDate, true) { selected ->
                                editDueDate = selected
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = displayEditDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.task_due_date_optional_label)) },
                    placeholder = { Text(stringResource(R.string.task_select_date_time)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 48.dp, // Align with text field above (checkbox width + padding)
                            top = dimensionResource(R.dimen.padding_small)
                        ),
                    interactionSource = dateInteractionSource,
                    trailingIcon = {
                        Row {
                            if (editDueDate.isNotBlank()) {
                                IconButton(onClick = { editDueDate = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear_date)
                                    )
                                }
                            }
                            IconButton(onClick = {
                                openDateTimePicker(context, editDueDate, true) { selected ->
                                    editDueDate = selected
                                }
                            }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = stringResource(R.string.select_date)
                                )
                            }
                        }
                    }
                )
            } else {
                // Display due date if it exists
                task.dueDate?.let { due ->
                    val displayDate = try {
                        val dateTime = LocalDateTime.parse(due)
                        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                    } catch (e: Exception) {
                        due
                    }
                    Text(
                        text = "Due: $displayDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            start = 48.dp, // Align with text above (checkbox width + padding)
                            top = dimensionResource(R.dimen.padding_xxsmall)
                        )
                    )
                }
            }

            // Third row: Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.End
            ) {
                if (isEditing) {
                    TextButton(
                        onClick = {
                            isEditing = false
                            editText = task.description
                            editDueDate = task.dueDate ?: ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val due = editDueDate.takeIf { it.isNotBlank() }
                            onUpdate(
                                task.copy(
                                    description = editText,
                                    dueDate = due
                                )
                            )
                            isEditing = false
                        },
                        enabled = editText.isNotBlank()
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                } else {
                    IconButton(
                        onClick = {
                            editText = task.description
                            editDueDate = task.dueDate ?: ""
                            isEditing = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_task))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_task),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
