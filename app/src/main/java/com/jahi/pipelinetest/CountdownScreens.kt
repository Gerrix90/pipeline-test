package com.jahi.pipelinetest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.geometry.Offset
import com.jahi.pipelinetest.ui.theme.Slate100
import com.jahi.pipelinetest.ui.theme.Slate400
import com.jahi.pipelinetest.ui.theme.SurfaceDark
import com.jahi.pipelinetest.ui.theme.Turquoise400
import com.jahi.pipelinetest.ui.theme.Indigo200
import com.jahi.pipelinetest.ui.theme.Yellow300
import com.jahi.pipelinetest.ui.components.TaskList
import com.jahi.pipelinetest.viewmodel.TaskViewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource

@Composable
private fun CountdownCard(
    title: String,
    value: String,
    eventId: Int? = null,
    taskViewModel: TaskViewModel? = null,
    modifier: Modifier = Modifier
) {
    var showTitle by remember { mutableStateOf(true) }
    var showTasks by remember { mutableStateOf(false) }
    val tasks by (taskViewModel?.tasks?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val eventTasks = eventId?.let { id -> tasks.filter { it.eventId == id } } ?: emptyList()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_default))
            .clickable { 
                if (eventId != null) {
                    showTasks = !showTasks
                } else {
                    showTitle = !showTitle
                }
            },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showTitle || eventId != null) {
                Text(
                    text = title,
                    color = Slate400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = value,
                color = Slate100,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            
            // Show task progress for events
            if (eventId != null && eventTasks.isNotEmpty()) {
                val completedTasks = eventTasks.count { it.isCompleted }
                val totalTasks = eventTasks.size
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.tasks_progress, completedTasks, totalTasks),
                        color = Slate400,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                    LinearProgressIndicator(
                        progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.progress_bar_width))
                            .height(dimensionResource(id = R.dimen.progress_bar_height))
                    )
                }
            }
            
            // Show tasks when expanded
            if (showTasks && eventId != null && taskViewModel != null) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_default)))
                TaskList(
                    eventId = eventId,
                    tasks = eventTasks,
                    taskViewModel = taskViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (eventId != null) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                Text(
                    text = stringResource(R.string.tap_to_manage_tasks),
                    color = Slate400,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CountdownsScreen(
    viewModel: MainViewModel,
    taskViewModel: TaskViewModel? = null,
    modifier: Modifier = Modifier
) {
    val now by viewModel.now.collectAsState()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CountdownCard(
            title = stringResource(R.string.daily_countdown),
            value = viewModel.formatTime(viewModel.durationToEndOfDay(now)),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (viewModel.showYearCountdown) {
            CountdownCard(
                title = stringResource(R.string.year_countdown),
                value = viewModel.daysUntilEndOfYear(now).toString() + " days",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        viewModel.events.forEach { event ->
            if (event.date.isNotBlank()) {
                val diff = viewModel.durationToEvent(event.date, now)
                if (diff != null && !diff.isNegative && !diff.isZero) {
                    CountdownCard(
                        title = event.name,
                        value = if (event.showTime) {
                            viewModel.formatDuration(diff)
                        } else {
                            "${diff.toDays()} days"
                        },
                        eventId = event.id,
                        taskViewModel = taskViewModel,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun LifeHourglassScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val current = viewModel.currentAge
        val target = viewModel.targetAge

        Text(text = stringResource(R.string.life_hourglass), fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(R.string.life_hourglass_quote),
            color = Slate400,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
        )

        val valid = current >= 0 && target > 0
        if (!valid) {
            Text(
                text = stringResource(R.string.set_age_in_settings),
                color = Slate400,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_default))
            )
        }

        val gridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dimensionResource(id = R.dimen.hourglass_grid_cell)),
            state = gridState,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(target + 1) { year ->
                val state = when {
                    !valid -> HourglassState.NEUTRAL
                    year < current -> HourglassState.PAST
                    year == current -> HourglassState.CURRENT
                    else -> HourglassState.FUTURE
                }
                HourglassItem(year, state)
            }
        }

        if (valid) {
            val remaining = target - current
            Text(
                text = if (remaining >= 0) {
                    stringResource(R.string.years_remaining_until, remaining, target)
                } else {
                    stringResource(R.string.congrats_surpassed, target)
                },
                color = Slate400,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

private enum class HourglassState { PAST, CURRENT, FUTURE, NEUTRAL }

@Composable
private fun HourglassItem(year: Int, state: HourglassState) {
    var hovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (hovered) 1.1f else 1f)

    val infinite = rememberInfiniteTransition()
    val grainOffset by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )

    val color = when (state) {
        HourglassState.PAST -> Slate400
        HourglassState.CURRENT -> Turquoise400
        HourglassState.FUTURE -> Indigo200
        HourglassState.NEUTRAL -> Slate400
    }

    val disabledAlpha = if (state == HourglassState.PAST) 0.4f else 1f

    Column(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.hourglass_item_padding))
            .size(dimensionResource(id = R.dimen.hourglass_item_size))
            .alpha(disabledAlpha)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        hovered = true
                        tryAwaitRelease()
                        hovered = false
                    }
                )
            }
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Text(
                text = when (state) {
                    HourglassState.PAST -> "\u231B" // hourglass done
                    HourglassState.CURRENT -> "\u23F3" // hourglass running
                    HourglassState.FUTURE -> "\u231B" // hourglass full
                    HourglassState.NEUTRAL -> "\u231B"
                },
                fontSize = 24.sp,
                color = color
            )
            if (state == HourglassState.CURRENT) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val y = size.height * grainOffset * 0.5f
                    val radius = 2.dp.toPx()
                    drawCircle(
                        color = Yellow300,
                        radius = radius,
                        center = Offset(size.width / 2, y)
                    )
                }
            }
        }
        Text(text = year.toString(), fontSize = 12.sp, color = color)
    }
}
