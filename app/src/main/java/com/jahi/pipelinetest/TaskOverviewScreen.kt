package com.jahi.pipelinetest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jahi.pipelinetest.ui.components.TaskList
import com.jahi.pipelinetest.viewmodel.TaskViewModel

@Composable
fun TaskOverviewScreen(
    mainViewModel: MainViewModel,
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks = taskViewModel.allTasks.collectAsState().value
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        mainViewModel.events.forEach { event ->
            item {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                val eventTasks = tasks.filter { it.eventId == event.id }
                TaskList(
                    eventId = event.id,
                    tasks = eventTasks,
                    taskViewModel = taskViewModel,
                    modifier = Modifier.padding(bottom = 16.dp),
                    useLazyList = false
                )
            }
        }
    }
}