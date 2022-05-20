package com.mits.subscription.ui.creating

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Subscription
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreatingScreen(
    navController: NavController,
    createViewModel: CreatingViewModel,
    activity: ComponentActivity
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val name = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = name.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { name.value = it },
            label = { Text(stringResource(id = R.string.label_name)) }
        )
        val number = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = number.value,
            onValueChange = { number.value = it },
            label = { Text(stringResource(id = R.string.label_lesson_number)) }
        )
        val choseStartDate = remember { mutableStateOf(false) }
        val startDate = remember { mutableStateOf(Calendar.getInstance()) }
        Button(onClick = { choseStartDate.value = true }) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.label_start_date))
                Text(text = parse(startDate.value))
            }

        }
        if (choseStartDate.value) {
            ShowDatePicker(startDate)
        }

        ///
        val choseEndDate = remember { mutableStateOf(false) }
        val endDate = remember { mutableStateOf(Calendar.getInstance()) }
        Button(onClick = { choseEndDate.value = true }) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.label_end_date))
                Text(text = parse(endDate.value))
            }

        }
        if (choseEndDate.value) {
            ShowDatePicker(endDate)
        }

        Button(onClick = {
            createViewModel.create(
                Subscription(
                    0, name.value.text,
                    startDate.value.time,
                    endDate.value.time,
                    Integer.valueOf(number.value.text ?: "0"), ""
                )
            )
        }) {
            Row() {
                Text(stringResource(id = R.string.btn_save))
            }

        }
    }
}

@Composable
fun ShowDatePicker(date: MutableState<Calendar>) {
    val context = LocalContext.current
    val calendarInit = Calendar.getInstance()

    val yearInit = calendarInit.get(Calendar.YEAR)
    val monthInit = calendarInit.get(Calendar.MONTH)
    val dayInit = calendarInit.get(Calendar.DAY_OF_MONTH)

    calendarInit.time = Date()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            date.value = calendar
        }, yearInit, monthInit, dayInit
    )
    datePickerDialog.show()

}

private fun parse(calendar: Calendar): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    return formatter.format(calendar.time)
}
