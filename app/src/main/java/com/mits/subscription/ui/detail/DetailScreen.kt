package com.mits.subscription.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mits.subscription.CalendarView
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.parseDate
import com.mits.subscription.parseMillis
import com.mits.subscription.ui.components.PaymentFileView
import com.mits.subscription.ui.creating.ShowDatePicker
import com.mits.subscription.ui.theme.md_theme_light_primary
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val uiState = detailViewModel.uiState.collectAsState().value
    Detail(
        uiState,
        onBack = onBack,
        onNameChange = detailViewModel::acceptNameWorkshop,
        onDetailChange = detailViewModel::acceptDetail,
        onNumberChange = detailViewModel::updateNumber,
        onStartCalendarChange = detailViewModel::updateStartCalendar,
        onEndCalendarChange = detailViewModel::updateEndCalendar,
        onDeleteLesson = detailViewModel::deleteLesson,
        onChangeLessonDate = detailViewModel::changeLessonDate,
        addVisitedLesson = detailViewModel::addVisitedLesson,
        acceptPhotoUri = detailViewModel::acceptPhotoUri
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(
    uiState: DetailViewModel.DetailState,
    onBack: () -> Unit,
    onNameChange: (newName: String) -> Unit,
    onDetailChange: (newName: String) -> Unit,
    onNumberChange: (newNumber: String) -> Unit,
    onStartCalendarChange: (newValue: Long) -> Unit,
    onEndCalendarChange: (newValue: Long) -> Unit,
    onDeleteLesson: (item: Long) -> Unit,
    onChangeLessonDate: (item: Lesson, date: Long) -> Unit,
    addVisitedLesson: () -> Unit,
    acceptPhotoUri: (photoUri: String?) -> Unit
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
                        onClick = {
                            onBack.invoke()
                        }) {
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
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(1f)
                .fillMaxWidth()
        ) {
            when (uiState) {
                DetailViewModel.DetailState.Loading -> ProgressIndicator()
                is DetailViewModel.DetailState.Success -> {
                    Name(uiState.subscription.workshop?.name ?: "", onNameChange)
                    Detail(uiState.subscription.detail ?: "", onDetailChange)
                    LessonNumber(uiState.subscription.lessonNumbers, onNumberChange)
                    StartDate(uiState.subscription.startDate?: 0, onStartCalendarChange)
                    EndDate(uiState.subscription.endDate ?: 0, onEndCalendarChange)
                    Lessons(
                        uiState.subscription.lessons ?: emptyList(),
                        onDeleteLesson,
                        onChangeLessonDate,
                        addVisitedLesson
                    )
                    CalendarView(getVisited(uiState.subscription.lessons))
                    PaymentFileView(uiState.paymentFile) { photoUri ->
                        acceptPhotoUri(photoUri)
                    }
                }
            }
        }
    }
}

fun getVisited(lessons: List<Lesson>?): List<LocalDate>? {
    if (lessons.isNullOrEmpty()) return null
    return lessons.map { it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
}

@Composable
fun LessonRow(
    item: Lesson, onDeleteLesson: () -> Unit,
    onChangeLessonDate: (date: Long) -> Unit
) {
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
        val start = Calendar.getInstance().time.time

        ShowDatePicker(start, onChange = { newCalendar ->
            onChangeLessonDate(start)
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
    onNameChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(name)) }

    LaunchedEffect(name) {
        if (name != text.text) {
            text = TextFieldValue(name, text.selection)
        }
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
            onNameChange(it.text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        label = { Text(stringResource(id = R.string.label_name)) },
    )
}

@Composable
private fun Detail(
    name: String,
    onNameChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(name)) }

    LaunchedEffect(name) {
        if (name != text.text) {
            text = TextFieldValue(name, text.selection)
        }
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
            onNameChange(it.text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text(stringResource(id = R.string.label_tag)) }
    )
}

@Composable
private fun LessonNumber(
    lessonsNumber: Int,
    onNumberChange: (newNumber: String) -> Unit
) {
    val lessonsNumberStr = lessonsNumber.toString()
    var text by remember { mutableStateOf(TextFieldValue(lessonsNumberStr)) }

    LaunchedEffect(lessonsNumberStr) {
        if (lessonsNumberStr != text.text) {
            text = TextFieldValue(lessonsNumberStr, text.selection)
        }
    }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onNumberChange(it.text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(stringResource(id = R.string.label_lesson_number)) }
    )
}

@Composable
private fun StartDate(
    startDate: Long,
    onCalendarChange: (newValue: Long) -> Unit
) {
    val choseStartDate = remember { mutableStateOf(false) }

    FilledTonalButton(
        onClick = { choseStartDate.value = true },
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
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
            startDate, onChange = { newCalendar ->
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
    onCalendarChange: (newValue: Long) -> Unit?
) {
    val choseEndDate = remember { mutableStateOf(false) }

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
        ShowDatePicker(endDate, onChange = { newCalendar ->
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
    lessons: List<Lesson>,
    onDeleteLesson: (lessonId: Long) -> Unit,
    onChangeLessonDate: (lesson: Lesson, calendar: Long) -> Unit,
    addVisitedLesson: () -> Unit,
) {
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

            if (lessons.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {

                    lessons.forEach {
                        LessonRow(
                            item = it,
                            onDeleteLesson = { onDeleteLesson(it.lId) },
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
private fun ProgressIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}