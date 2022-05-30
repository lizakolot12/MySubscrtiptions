package com.mits.subscription.ui.creating

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Subscription
import com.mits.subscription.parseCalendar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreatingScreen(
    navController: NavController,
    createViewModel: CreatingViewModel
) {
   // val uiState by createViewModel.uiState.collectAsState()
    val uiState = remember {
        createViewModel.uiState
    }

    CreatingScreenState(navController, uiState, createViewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreatingScreenState(
    navController: NavController,
    state: MutableState<CreatingViewModel.CreatingState>,
    createViewModel: CreatingViewModel
) {

    if (state.value.isLoading) {
        Log.e("TEST", "must be loading")
        CircularProgressIndicator(
            modifier = Modifier
        )
    }
    if (state.value.finished) {
        Log.e("TEST", "must be pop " + state.value.finished)
        navController.navigateUp()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        val name = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = name.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onValueChange = {
                name.value = it
                createViewModel.checkName(it.text)
            },
            isError = state.value.nameError != null,
            label = { Text(stringResource(id = R.string.label_name)) }
        )
        if (state.value.nameError != null) {
            Text(
                text = stringResource(id = state.value.nameError!!),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = Red
            )
        }
        val number = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = number.value,
            onValueChange = { number.value = it },
            label = { Text(stringResource(id = R.string.label_lesson_number)) }
        )
        val choseStartDate = remember { mutableStateOf(false) }
        val startDate = remember { mutableStateOf(Calendar.getInstance()) }
        Button(
            onClick = { choseStartDate.value = true },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(horizontal = 16.dp),
        ) {
            Row() {
                Text(stringResource(id = R.string.label_start_date))
                Text(text = parseCalendar(startDate.value), modifier = Modifier.padding(horizontal = 8.dp))
            }

        }
        if (choseStartDate.value) {
            ShowDatePicker(startDate, choseStartDate)
        }

        ///

        val choseEndDate = remember { mutableStateOf(false) }
        val endDate =
            remember { mutableStateOf(Calendar.getInstance()) }
        Button(
            onClick = { choseEndDate.value = true },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(horizontal = 16.dp),

            ) {
            Row() {
                Text(stringResource(id = R.string.label_end_date))
                Text(text = parseCalendar(endDate.value), modifier = Modifier.padding(horizontal = 8.dp))
            }

        }
        if (choseEndDate.value) {
            ShowDatePicker(endDate, choseEndDate)
        }

        Button(
            onClick = {
                createViewModel.create(
                    Subscription(
                        0, name.value.text,
                        startDate.value.time,
                        endDate.value.time,
                        Integer.valueOf(
                            number.value.text.ifBlank { "0" }
                        ), ""
                    )
                )
            },

            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = state.value.savingAvailable
        ) {
            Row() {
                Text(stringResource(id = R.string.btn_save))
            }

        }
    }
}

@Composable
fun ShowDatePicker(date: MutableState<Calendar>, opening:MutableState<Boolean>) {
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
            opening.value = false
        }, yearInit, monthInit, dayInit
    )
    datePickerDialog.show()

}

@Composable
fun ShowDatePicker(initial:Calendar, onChanged:(m: Calendar) -> Unit) {
    val context = LocalContext.current

    val yearInit = initial.get(Calendar.YEAR)
    val monthInit = initial.get(Calendar.MONTH)
    val dayInit = initial.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            onChanged.invoke(calendar)
        }, yearInit, monthInit, dayInit
    )
    datePickerDialog.show()

}

