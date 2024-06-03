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

fun getDefaultDetail(): Int {
    val currentDate = Calendar.getInstance()
    return when (currentDate.get(Calendar.MONTH)) {
        0 -> R.string.january
        1 -> R.string.february
        2 -> R.string.march
        3 -> R.string.april
        4 -> R.string.may
        5 -> R.string.june
        6 -> R.string.july
        7 -> R.string.august
        8 -> R.string.september
        9 -> R.string.october
        10 -> R.string.november
        11 -> R.string.december
        else -> {
            R.string.january
        }
    }
}