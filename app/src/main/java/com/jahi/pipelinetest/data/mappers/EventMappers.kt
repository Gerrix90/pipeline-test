package com.jahi.pipelinetest.data.mappers

import com.jahi.pipelinetest.data.local.entities.CustomEventEntity
import com.jahi.pipelinetest.model.CustomEvent

fun CustomEventEntity.toDomainModel(): CustomEvent {
    return CustomEvent(
        id = id,
        name = name,
        date = date,
        showTime = showTime,
        showInWidget = showInWidget
    )
}

fun CustomEvent.toEntity(): CustomEventEntity {
    return CustomEventEntity(
        id = id,
        name = name,
        date = date,
        showTime = showTime,
        showInWidget = showInWidget
    )
}

fun List<CustomEventEntity>.toDomainModels(): List<CustomEvent> {
    return map { it.toDomainModel() }
}

fun List<CustomEvent>.toEntities(): List<CustomEventEntity> {
    return map { it.toEntity() }
}