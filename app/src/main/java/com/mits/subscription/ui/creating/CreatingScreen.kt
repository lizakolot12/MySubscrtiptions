@file:OptIn(ExperimentalMaterial3Api::class)

package com.mits.subscription.ui.creating

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.parseCalendar
import java.util.*

@Composable
fun CreatingScreen(
    navController: NavController,
    createViewModel: CreatingViewModel
) {
    val uiState = remember {
        createViewModel.uiState
    }
    CreatingScreenState(navController, uiState, createViewModel)
}

@Composable
fun CreatingScreenState(
    navController: NavController,
    state: MutableState<CreatingViewModel.CreatingState>,
    createViewModel: CreatingViewModel
) {
    LoadingView(state)
    Column(modifier = Modifier.fillMaxWidth()) {
        val name = remember { mutableStateOf(TextFieldValue()) }
        NameView(name, state, createViewModel)

        val tag = remember { mutableStateOf(TextFieldValue()) }
        TagView(tag, state, createViewModel)

        val number = remember { mutableStateOf("") }
        PlannedNumberView(number)

        val choseStartDate = remember { mutableStateOf(false) }
        val startDate = remember { mutableStateOf(Calendar.getInstance()) }
        val choseEndDate = remember { mutableStateOf(false) }
        StartDateView(choseStartDate, startDate, choseEndDate)

        val endDate = remember { mutableStateOf(Calendar.getInstance()) }
        EndDateView(choseEndDate, endDate, choseStartDate)
        SaveView(state) {
            createViewModel.create(
                name.value.text,
                tag.value.text,
                Integer.valueOf(
                    number.value.ifBlank { "0" }
                ),
                startDate.value.time,
                endDate.value.time,
            )
        }

        if (state.value.finished) {
            navController.navigateUp()
        }
    }
}

@Composable
private fun SaveView(
    state: MutableState<CreatingViewModel.CreatingState>,
    onButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Button(
            onClick = onButtonClicked,
            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = state.value.savingAvailable
        ) {
            Row {
                Text(stringResource(id = R.string.btn_save))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PlannedNumberView(
    number: MutableState<String>,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        value = number.value,

        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            .copy(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        onValueChange = {
            number.value = it.digits()
        },
        label = { Text(stringResource(id = R.string.label_lesson_number)) },
    )
}

@Composable
private fun TagView(
    tag: MutableState<TextFieldValue>,
    state: MutableState<CreatingViewModel.CreatingState>,
    createViewModel: CreatingViewModel
) {
    state.value.defaultTagStrId?.let {
        tag.value = TextFieldValue(stringResource(it))
    }
    TextField(
        value = tag.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        onValueChange = {
            tag.value = it
            createViewModel.checkTag(it.text)
        },

        label = { Text(stringResource(id = R.string.label_tag)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
    )
}

@Composable
private fun LoadingView(state: MutableState<CreatingViewModel.CreatingState>) {
    if (state.value.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun NameView(
    name: MutableState<TextFieldValue>,
    state: MutableState<CreatingViewModel.CreatingState>,
    createViewModel: CreatingViewModel
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = name.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
            .focusRequester(focusRequester),
        onValueChange = {
            name.value = it
            createViewModel.checkName(it.text)
        },
        isError = state.value.nameError != null,
        label = { Text(stringResource(id = R.string.label_name)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    state.value.nameError?.let {
        Text(
            text = stringResource(id = it),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp),
            color = Red,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun StartDateView(
    choseStartDate: MutableState<Boolean>,
    startDate: MutableState<Calendar>,
    choseEndDate: MutableState<Boolean>
) {
    FilledTonalButton(
        onClick = { choseStartDate.value = true },
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 16.dp),
    ) {
        Row {
            Text(stringResource(id = R.string.label_start_date))
            Text(
                text = parseCalendar(startDate.value),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    if (choseStartDate.value) {
        ShowDatePicker(
            startDate.value, onChanged = {
                choseStartDate.value = false
                choseEndDate.value = true
                startDate.value = it
            },
            onDismiss = {
                choseStartDate.value = false
                choseEndDate.value = false
            },
            R.string.label_start_date
        )
    }
}

@Composable
private fun EndDateView(
    choseEndDate: MutableState<Boolean>,
    endDate: MutableState<Calendar>,
    choseStartDate: MutableState<Boolean>
) {
    FilledTonalButton(
        onClick = { choseEndDate.value = true },
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 16.dp),

        ) {
        Row {
            Text(stringResource(id = R.string.label_end_date))
            Text(
                text = parseCalendar(endDate.value),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

    }

    if (choseEndDate.value) {
        ShowDatePicker(
            endDate.value, onChanged = { newCalendar ->
                choseEndDate.value = false
                endDate.value = newCalendar

            },
            onDismiss = {
                choseStartDate.value = false
                choseEndDate.value = false
            },
            R.string.label_end_date
        )
    }
}

private fun String.digits() = filter { it.isDigit() }

@Composable
fun ShowDatePicker(
    initial: Calendar,
    onChanged: (m: Calendar) -> Unit,
    onDismiss: (() -> Unit)? = null,
    titleId: Int? = null
) {
    val context = LocalContext.current
    val yearInit = initial.get(Calendar.YEAR)
    val monthInit = initial.get(Calendar.MONTH)
    val dayInit = initial.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, R.style.DatePickerDialogTheme,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            onChanged.invoke(calendar)
        }, yearInit, monthInit, dayInit
    )
    datePickerDialog.setOnDismissListener { onDismiss?.invoke() }
    titleId?.let {
        datePickerDialog.setMessage(stringResource(id = titleId))
    }

    datePickerDialog.show()
}




