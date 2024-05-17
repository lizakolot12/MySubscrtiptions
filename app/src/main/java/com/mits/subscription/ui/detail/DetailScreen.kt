package com.mits.subscription.ui.detail

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.parseCalendar
import com.mits.subscription.parseDate
import com.mits.subscription.ui.creating.ShowDatePicker
import java.util.*

@Composable
fun DetailScreen(
    onSave: () -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val uiState = detailViewModel.uiState.collectAsState().value
    Detail(
        uiState, onSave,
        onNameChange = detailViewModel::acceptNameWorkshop,
        onDetailChange = detailViewModel::acceptDetail,
        onNumberChange = detailViewModel::acceptNumber,
        onStartCalendarChange = detailViewModel::acceptStartCalendar,
        onEndCalendarChange = detailViewModel::acceptEndCalendar,
        onDeleteLesson = detailViewModel::deleteLesson,
        onChangeLessonDate = detailViewModel::changeLessonDate,
        addVisitedLesson = detailViewModel::addVisitedLesson
    )
}

@Composable
fun Detail(
    uiState: DetailViewModel.DetailState,
    onSave: () -> Unit,
    onNameChange: (newName: String) -> Unit,
    onDetailChange: (newName: String) -> Unit,
    onNumberChange: (newNumber: String) -> Unit,
    onStartCalendarChange: (newValue: Calendar) -> Unit,
    onEndCalendarChange: (newValue: Calendar) -> Unit,
    onDeleteLesson: (item: Lesson) -> Unit,
    onChangeLessonDate: (item: Lesson, calendar: Calendar) -> Unit,
    addVisitedLesson: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxHeight(1f)
            .fillMaxWidth()
    ) {
        Log.e("TEST", "Main column ")
        ProgressIndicator(uiState.isLoading)
        Name(uiState.subscription?.workshop?.name?:"", uiState.nameError, onNameChange)
        Detail(uiState.subscription?.detail, onDetailChange)
        LessonNumber(uiState.subscription?.lessonNumbers, onNumberChange)
        StartDate(uiState.subscription?.startDate?.time ?: 0, onStartCalendarChange)
        EndDate(uiState.subscription?.endDate?.time ?: 0, onEndCalendarChange)
        Lessons(uiState.subscription?.lessons, onDeleteLesson, onChangeLessonDate, addVisitedLesson)

        if (uiState.finished) {
            onSave.invoke()
        }
    }
}

@Composable
fun LessonRow(
    item: Lesson, onDeleteLesson: () -> Unit,
    onChangeLessonDate: (calwendar: Calendar) -> Unit
) {
    Log.e("TEST", "Lesson row ")
    val expanded = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
            .shadow(
                elevation = 1.dp,
            )
            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { expanded.value = true },
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(text = parseDate(item.date), Modifier.weight(3f))
            Text(text = item.description, Modifier.weight(3f))
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(id = R.string.delete),
                Modifier
                    .clickable(true, onClick = onDeleteLesson)
                    .weight(1f)
            )
        })

    if (expanded.value) {
        val start = Calendar.getInstance()
        start.time = item.date
        ShowDatePicker(start, onChange = { newCalendar ->
            onChangeLessonDate(newCalendar)
            expanded.value = false
        },
            onDismiss = {
                expanded.value = false
            })
    }
}

@Composable
private fun Name(
    name: String,
    nameError: Int?,
    onNameChange:((String) -> Unit)
) {
    Log.e("TEST", "Name ")
    TextField(
        value = name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onValueChange = {
            onNameChange(it)
        },
        isError = nameError != null,
        label = { Text(stringResource(id = R.string.label_name)) }
    )

    if (nameError != null) {
        Text(
            text = stringResource(id = nameError),
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun Detail(
    detail: String?,
    onNameChange: (String) -> Unit
) {
    Log.e("TEST", "Detail ")
    TextField(
        value = detail ?: "",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onValueChange = onNameChange,
        label = { Text(stringResource(id = R.string.label_tag)) }
    )
}

@Composable
private fun LessonNumber(
    lessonsNumber: Int?,
    onNumberChange: (newNumber: String) -> Unit
) {
    Log.e("TEST", "Lesson number ")
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = (lessonsNumber ?: 0).toString(),
        onValueChange = onNumberChange,
        label = { Text(stringResource(id = R.string.label_lesson_number)) }
    )
}

@Composable
private fun StartDate(
    startDate: Long,
    onCalendarChange: (newValue: Calendar) -> Unit
) {
    Log.e("TEST", "Start Date ")
    val choseStartDate = remember { mutableStateOf(false) }
    val startCalendar = Calendar.getInstance()
    startCalendar.time = Date(startDate)

    FilledTonalButton(
        onClick = { choseStartDate.value = true },
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
    ) {
        Row {
            Text(stringResource(id = R.string.label_start_date))
            Text(
                text = parseCalendar(startCalendar),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
    if (choseStartDate.value) {
        ShowDatePicker(
            startCalendar, onChange = { newCalendar ->
                onCalendarChange(newCalendar)
                choseStartDate.value = false
            },
            onDismiss = {
                choseStartDate.value = false
            },
            R.string.label_start_date
        )
    }
}

@Composable
private fun EndDate(
    endDate: Long,
    onCalendarChange: (newValue: Calendar) -> Unit?
) {
    Log.e("TEST", "EndDate ")
    val choseEndDate = remember { mutableStateOf(false) }
    val endCalendar = Calendar.getInstance()
    endCalendar.time = Date(endDate)

    FilledTonalButton(
        onClick = { choseEndDate.value = true },
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 16.dp),

        ) {
        Row {
            Text(stringResource(id = R.string.label_end_date))
            Text(
                text = parseCalendar(endCalendar),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
    if (choseEndDate.value) {
        ShowDatePicker(endCalendar, onChange = { newCalendar ->
            run {
                choseEndDate.value = false
                onCalendarChange(newCalendar)
            }

        }, onDismiss = {
            choseEndDate.value = false
        },
            R.string.label_end_date
        )
    }
}

@Composable
private fun Lessons(
    lessons: List<Lesson>?,
    onDeleteLesson: (item: Lesson) -> Unit,
    onChangeLessonDate: (item: Lesson, calendar: Calendar) -> Unit,
    addVisitedLesson: () -> Unit,
) {
    Log.e("TEST", "Lessons ")
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.visited_lessons),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(0.9f)
                )

                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.description_add_lesson),
                    Modifier
                        .clickable(true, onClick = addVisitedLesson)
                        .weight(0.2f)
                        .padding(end = 16.dp)
                )
            }

            if ((lessons?.size ?: 0) > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {

                    lessons?.forEach {
                        LessonRow(
                            item = it,
                            onDeleteLesson = { onDeleteLesson(it) },
                            onChangeLessonDate = { calendar -> onChangeLessonDate(it, calendar) }
                        )

                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.empty_visited_lessons),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ProgressIndicator(isLoading: Boolean) {
    Log.e("TEST", "ProgressIndicator ")
    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}