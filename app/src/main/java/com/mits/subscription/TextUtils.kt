package com.mits.subscription

import java.text.SimpleDateFormat
import java.util.*

const val GENERAL_DATE_FORMAT = "dd.MM.yyyy"

fun parseCalendar(calendar: Calendar): String {
    val formatter = SimpleDateFormat(GENERAL_DATE_FORMAT, Locale.US)
    return formatter.format(calendar.time)
}

fun parseDate(date: Date): String {
    val formatter = SimpleDateFormat(GENERAL_DATE_FORMAT, Locale.US)
    return formatter.format(date)
}