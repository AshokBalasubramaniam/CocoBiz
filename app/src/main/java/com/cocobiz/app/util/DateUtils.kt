package com.cocobiz.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val shortFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    fun LocalDate.toDisplayString(): String = format(displayFormatter)
    fun LocalDate.toInputString(): String = format(inputFormatter)
    fun LocalDate.toShortString(): String = format(shortFormatter)

    fun LocalDate.toEpochMilli(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    fun Long.toDisplayString(): String = toLocalDate().toDisplayString()

    fun calculateNextSalesDate(salesDate: LocalDate, cycleDays: Int): LocalDate =
        salesDate.plusDays(cycleDays.toLong())

    fun calculateRemainingDays(nextSalesDate: LocalDate): Long =
        ChronoUnit.DAYS.between(LocalDate.now(), nextSalesDate).coerceAtLeast(0)

    fun isOverdue(nextSalesDate: LocalDate): Boolean =
        LocalDate.now().isAfter(nextSalesDate)

    fun parseInputDate(input: String): LocalDate? {
        return try {
            LocalDate.parse(input, inputFormatter)
        } catch (e: Exception) {
            try {
                LocalDate.parse(input, displayFormatter)
            } catch (e2: Exception) {
                null
            }
        }
    }

    fun getStartOfDay(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun getEndOfDay(date: LocalDate): Long =
        date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun getStartOfMonth(date: LocalDate): Long =
        date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun getEndOfMonth(date: LocalDate): Long {
        val lastDay = date.withDayOfMonth(date.lengthOfMonth())
        return lastDay.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getStartOfYear(year: Int): Long =
        LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun getEndOfYear(year: Int): Long =
        LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun formatRemainingDays(days: Long): String = when {
        days == 0L -> "Due Today"
        days == 1L -> "1 day left"
        days < 0L -> "Overdue"
        else -> "$days days left"
    }
}
