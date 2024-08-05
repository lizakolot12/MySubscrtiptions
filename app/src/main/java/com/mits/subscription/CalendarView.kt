package com.mits.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mits.subscription.ui.theme.md_theme_light_inversePrimary
import java.time.LocalDate

@Composable
fun CalendarView(checked: List<LocalDate>?) {
    var now by remember { mutableStateOf(LocalDate.now()) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { now = now.minusMonths(1) }) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = "Back"
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = getNameOfMonth(now.month.value))
            )
            IconButton(onClick = { now = now.plusMonths(1) }) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Forward"
                )
            }
        }

        MonthView(now = now, checked)
    }
}

@Composable
private fun MonthView(now: LocalDate, checked: List<LocalDate>?) {
    Column {
        CalendarHeader()
        BaseMonthView(now, checked)
    }
}


@Composable
private fun CalendarHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        val daysOfWeek = context.resources.getStringArray(R.array.days_of_week)

        daysOfWeek.forEach { day ->
            Text(
                day,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun BaseMonthView(now: LocalDate, checked: List<LocalDate>?) {

    val length = now.lengthOfMonth()
    var currentDay = now.withDayOfMonth(1)

    var day = 1
    val row = 6
    val column = 7
    for (i in 0 until row) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (dayOfWeek in 1..column) {
                if (isCell(dayOfWeek, currentDay) && day <= length) {
                    if (checked?.contains(currentDay) == true) {
                        CheckedCell(
                            day = day.toString(), Modifier
                                .weight(1f)
                        )
                    } else {
                        UnCheckedCell(
                            day.toString(),
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                    day++
                    currentDay = currentDay.plusDays(1)
                } else {
                    EmptyCell(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UnCheckedCell(day: String, modifier: Modifier = Modifier) {
    Text(
        day,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(8.dp)
    )
}

@Composable
private fun CheckedCell(day: String, modifier: Modifier = Modifier) {
    Text(
        day,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(2.dp)
            .background(
                md_theme_light_inversePrimary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    )
}

@Composable
private fun EmptyCell(modifier: Modifier) {
    Box(
        modifier = modifier
    )
}


private fun isCell(dayOfWeek: Int, currentDay: LocalDate): Boolean {
    return currentDay.dayOfWeek.value == dayOfWeek
}