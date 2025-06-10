package com.jahi.pipelinetest.data.mappers

import com.jahi.pipelinetest.data.local.entities.TaskEntity
import com.jahi.pipelinetest.model.Task

fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        eventId = eventId,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        eventId = eventId,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate
    )
}

fun List<TaskEntity>.toDomainModels(): List<Task> {
    return map { it.toDomainModel() }
}

fun List<Task>.toEntities(): List<TaskEntity> {
    return map { it.toEntity() }
}