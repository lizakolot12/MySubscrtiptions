@file:OptIn(ExperimentalMaterial3Api::class)

package com.mits.subscription.ui.creating

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
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
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Workshop
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreatingScreenState(
    navController: NavController,
    state: MutableState<CreatingViewModel.CreatingState>,
    createViewModel: CreatingViewModel
) {

    if (state.value.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }

    if (state.value.finished) {
        navController.navigateUp()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        val name = remember { mutableStateOf(TextFieldValue()) }
        val focusRequester = remember { FocusRequester() }

        TextField(
            value = name.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
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
        if (state.value.nameError != null) {
            Text(
                text = stringResource(id = state.value.nameError!!),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                color = Red,
                fontSize = 12.sp
            )
        }

        val tag = remember { mutableStateOf(TextFieldValue()) }
        if (state.value.defaultTagStrId != null) {
            tag.value = TextFieldValue(stringResource(id = state.value.defaultTagStrId!!))
        }
        TextField(
            value = tag.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            onValueChange = {
                tag.value = it
                createViewModel.checkTag(it.text)
            },

            label = { Text(stringResource(id = R.string.label_tag)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        val number = remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val choseStartDate = remember { mutableStateOf(false) }
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

        val startDate = remember { mutableStateOf(Calendar.getInstance()) }
        Button(
            onClick = { choseStartDate.value = true },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(horizontal = 16.dp),
        ) {
            Row() {
                Text(stringResource(id = R.string.label_start_date))
                Text(
                    text = parseCalendar(startDate.value),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

        }
        val choseEndDate = remember { mutableStateOf(false) }
        if (choseStartDate.value) {
            ShowDatePicker(
                startDate.value, onChanged = {
                    choseStartDate.value = false
                    choseEndDate.value = true
                },
                onDismiss = {
                    choseStartDate.value = false
                    choseEndDate.value = false
                },
                R.string.label_start_date
            )

        }

        val endDate =
            remember { mutableStateOf(Calendar.getInstance()) }
        Button(
            onClick = { choseEndDate.value = true },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(horizontal = 16.dp),

            ) {
            Row() {
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

                },
                onDismiss = {
                    choseStartDate.value = false
                    choseEndDate.value = false
                },
                R.string.label_end_date
            )
        }

        Button(
            onClick = {
                createViewModel.create(
                    name.value.text,
                    tag.value.text,
                    Integer.valueOf(
                        number.value.ifBlank { "0" }
                    ),
                    startDate.value.time,
                    endDate.value.time,
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

@Composable
fun Folders(folders: LiveData<List<Workshop>>, init: Workshop, onChanged: (workshop: Workshop) -> Unit) {

    val selectedOptionFolder = remember { mutableStateOf(init) }
    var expanded by remember { mutableStateOf(false) }

    val options = folders.value
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier.padding(16.dp)
    ) {
        val folLabel = stringResource(id = R.string.folder)
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = init.name,
            onValueChange = { },
            readOnly = true,
            label = {
                Text(
                    text = folLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            trailingIcon = {
                TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options?.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionFolder.value = selectionOption
                        expanded = false
                        onChanged.invoke(selectionOption)
                    }, text = { selectionOption.name }
                ) /*{
                    Text(text = selectionOption.name)
                }*/
            }
        }
    }
}




