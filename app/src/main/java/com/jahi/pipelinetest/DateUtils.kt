package com.jahi.pipelinetest

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Parses an ISO date string that may contain a time component.
 * Returns null if parsing fails.
 */
internal fun parseEventDateTimeOrNull(dateString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: DateTimeParseException) {
        try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
        } catch (e2: DateTimeParseException) {
            null
        }
    }
}

internal fun isValidDateTimeFormat(dateString: String): Boolean {
    return parseEventDateTimeOrNull(dateString) != null
}
