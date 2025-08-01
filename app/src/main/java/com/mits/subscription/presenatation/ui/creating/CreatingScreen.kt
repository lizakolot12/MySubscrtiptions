@file:OptIn(ExperimentalMaterial3Api::class)

package com.mits.subscription.presenatation.ui.creating

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mits.subscription.R
import com.mits.subscription.presenatation.ui.components.parseMillis
import com.mits.subscription.presenatation.ui.components.PaymentFileView
import com.mits.subscription.presenatation.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatingScreen(
    onBack: () -> Unit,
    createViewModel: CreatingViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.title),
                        textAlign = TextAlign.Center,
                        color = md_theme_light_primary,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            "",
                            tint = md_theme_light_primary
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            CreatingScreenState(onBack, createViewModel)
        }
    }
}

@Composable
fun CreatingScreenState(
    onFinish: () -> Unit,
    createViewModel: CreatingViewModel
) {
    val state by createViewModel.uiState.collectAsStateWithLifecycle()
    LoadingIndicator(state.isLoading)
    val choseStartDate = remember { mutableStateOf(false) }

    val choseEndDate = remember { mutableStateOf(false) }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Name(state.name, state.nameError) { newValue -> createViewModel.checkName(newValue) }
        }

        item {
            Detail(state.detail, state.defaultDetailStrId) { newValue ->
                createViewModel.checkDetail(
                    newValue
                )
            }
        }

        item {
            PlannedNumber(state.number.toString()) { newValue ->
                createViewModel.acceptNumber(
                    try {
                        newValue.toInt()
                    } catch (ex: Exception) {
                        0
                    }
                )
            }
        }

        item {


            StartDate(choseStartDate, state.startDate, choseEndDate) { newValue ->
                createViewModel.acceptStartDate(
                    newValue
                )
            }
        }

        item {

            EndDate(choseEndDate, state.endDate, choseStartDate) { newValue ->
                createViewModel.acceptEndDate(
                    newValue
                )
            }
        }

        item {
            PaymentFileView(state.fileUri) { photoUri ->
                createViewModel.acceptUri(photoUri.toString())
            }
        }

        item {
            SaveButton(state.savingAvailable) {
                createViewModel.create()
            }
        }

        if (state.finished) {
            onFinish()
        }
    }
}

@Composable
private fun SaveButton(
    savingAvailable: Boolean,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Button(
            onClick = onButtonClick,
            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = savingAvailable
        ) {
            Row {
                Text(stringResource(id = R.string.btn_save))
            }
        }
    }
}

@Composable
private fun PlannedNumber(
    number: String,
    onChange: (newValue: String) -> Unit?
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        value = number,

        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            .copy(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        onValueChange = {
            val digitValue = it.digits()
            onChange(digitValue)

        },
        label = { Text(stringResource(id = R.string.label_lesson_number)) },
    )
}

@Composable
private fun Detail(
    tag: String?,
    defaultDetailStrId: Int,
    onTagChange: (newValue: String) -> Unit?
) {
    TextField(
        value = if (tag?.isNotBlank() == true) tag else stringResource(id = defaultDetailStrId),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        onValueChange = {
            onTagChange.invoke(it)
        },

        label = { Text(stringResource(id = R.string.label_tag)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
    )
}

@Composable
private fun LoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun Name(
    name: String,
    nameError: Int?,
    onNameChange: (newValue: String) -> Unit?
) {
    TextField(
        value = name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onValueChange = {
            onNameChange(it)
        },
        isError = nameError != null,
        label = { Text(stringResource(id = R.string.label_name)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
    )

    nameError?.let {
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
private fun StartDate(
    choseStartDate: MutableState<Boolean>,
    startDate: Long,
    choseEndDate: MutableState<Boolean>,
    onChange: (newValue: Long) -> Unit?
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
                text = parseMillis(startDate),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    if (choseStartDate.value) {
        ShowDatePicker(
            startDate, onChange = {
                choseStartDate.value = false
                choseEndDate.value = true
                onChange.invoke(it)

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
private fun EndDate(
    choseEndDate: MutableState<Boolean>,
    endDate: Long,
    choseStartDate: MutableState<Boolean>,
    onChange: (newValue: Long) -> Unit?
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
                text = parseMillis(endDate),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

    }

    if (choseEndDate.value) {
        ShowDatePicker(
            endDate, onChange = { newCalendar ->
                choseEndDate.value = false
                onChange(newCalendar)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePicker(
    initialMillis: Long,
    onChange: (m: Long) -> Unit,
    onDismiss: (() -> Unit)? = null,
    titleId: Int? = null
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = { onDismiss?.invoke() },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onChange(it) }
                onDismiss?.invoke()
            }) {
                Text(stringResource(id = R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss?.invoke() }) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        }
    ) {
        Column {
            if (titleId != null) {
                Text(
                    text = titleId.let { stringResource(id = it) },
                    fontWeight = FontWeight.W600,
                    color = md_theme_light_primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp),
                )
            }
            DatePicker(state = datePickerState)
        }
    }
}


