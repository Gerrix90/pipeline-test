package com.jahi.pipelinetest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.jahi.pipelinetest.model.CustomEvent
import com.jahi.pipelinetest.ui.components.TaskList
import com.jahi.pipelinetest.util.openDateTimePicker
import com.jahi.pipelinetest.viewmodel.PlannerViewModel
import com.jahi.pipelinetest.parseEventDateTimeOrNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.jahi.pipelinetest.viewmodel.TaskViewModel

@Composable
fun PlannerScreen(
    plannerViewModel: PlannerViewModel,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val events by plannerViewModel.events.collectAsState()
    val tasks by taskViewModel.allTasks.collectAsState()
    var expandedEventId by remember { mutableStateOf<Int?>(null) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<CustomEvent?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_default))
    ) {
        // Header with Add Event button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_default)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.nav_planner),
                style = MaterialTheme.typography.headlineMedium,
                color = colorResource(R.color.white)
            )
            
            Button(
                onClick = { showAddEventDialog = true },
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(dimensionResource(R.dimen.padding_tiny))
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_event)
                )
            }
        }

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events yet. Tap + to create your first event!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_default))
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        isExpanded = expandedEventId == event.id,
                        onToggleExpanded = { 
                            expandedEventId = if (expandedEventId == event.id) null else event.id
                        },
                        onEditEvent = { editingEvent = event },
                        onDeleteEvent = { plannerViewModel.deleteEvent(event.id) },
                        tasks = tasks.filter { it.eventId == event.id },
                        taskViewModel = taskViewModel
                    )
                }
            }
        }
    }

    // Add Event Dialog
    if (showAddEventDialog) {
        EventDialog(
            event = null,
            onSave = { name, date, showTime, showInWidget ->
                plannerViewModel.createEvent(name, date, showTime, showInWidget)
                showAddEventDialog = false
            },
            onDismiss = { showAddEventDialog = false }
        )
    }

    // Edit Event Dialog
    editingEvent?.let { event ->
        EventDialog(
            event = event,
            onSave = { name, date, showTime, showInWidget ->
                plannerViewModel.updateEvent(
                    event.copy(
                        name = name,
                        date = date,
                        showTime = showTime,
                        showInWidget = showInWidget
                    )
                )
                editingEvent = null
            },
            onDismiss = { editingEvent = null }
        )
    }
}

@Composable
private fun EventCard(
    event: CustomEvent,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onEditEvent: () -> Unit,
    onDeleteEvent: () -> Unit,
    tasks: List<com.jahi.pipelinetest.model.Task>,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.chat_message_corner_radius))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_default))
        ) {
            // Event Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorResource(R.color.white)
                    )
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.jahi.pipelinetest.ui.theme.Slate400
                    )
                    if (tasks.isNotEmpty()) {
                        val completedTasks = tasks.count { it.isCompleted }
                        Text(
                            text = stringResource(R.string.tasks_progress, completedTasks, tasks.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = com.jahi.pipelinetest.ui.theme.Slate400
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onEditEvent) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_task),
                            tint = colorResource(R.color.white)
                        )
                    }
                    IconButton(onClick = onDeleteEvent) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_task),
                            tint = colorResource(R.color.white)
                        )
                    }
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = colorResource(R.color.white)
                    )
                }
            }

            // Expanded Task List
            if (isExpanded) {
                Divider(
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)),
                    color = com.jahi.pipelinetest.ui.theme.Slate700
                )
                TaskList(
                    eventId = event.id,
                    tasks = tasks,
                    taskViewModel = taskViewModel,
                    useLazyList = false
                )
            }
        }
    }
}

@Composable
private fun EventDialog(
    event: CustomEvent?,
    onSave: (String, String, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(event?.name ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var showTime by remember { mutableStateOf(event?.showTime ?: false) }
    var showInWidget by remember { mutableStateOf(event?.showInWidget ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    if (showDatePicker) {
        openDateTimePicker(
            context = context,
            value = date.ifBlank { LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) },
            showTime = showTime
        ) { selectedDateTime ->
            date = selectedDateTime
            showDatePicker = false
        }
    }
    
    // Helper function to format date for display
    val displayDate = remember(date, showTime) {
        if (date.isBlank()) {
            ""
        } else {
            val dateTime = parseEventDateTimeOrNull(date)
            dateTime?.let {
                if (showTime) {
                    it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                } else {
                    it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                }
            } ?: date
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (event == null) stringResource(R.string.action_add_event) else "Edit Event",
                color = colorResource(R.color.white)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_event_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.label_event_date)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select Date"
                            )
                        }
                    }
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showTime,
                        onCheckedChange = { showTime = it }
                    )
                    Text(
                        text = stringResource(R.string.show_time),
                        color = colorResource(R.color.white)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showInWidget,
                        onCheckedChange = { showInWidget = it }
                    )
                    Text(
                        text = stringResource(R.string.show_in_widget),
                        color = colorResource(R.color.white)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && date.isNotBlank()) {
                        onSave(name, date, showTime, showInWidget)
                    }
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}