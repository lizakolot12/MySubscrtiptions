@file:OptIn(ExperimentalMaterial3Api::class)

package com.mits.subscription.ui.detail

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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.parseCalendar
import com.mits.subscription.parseDate
import com.mits.subscription.ui.creating.ShowDatePicker
import java.util.*

@Composable
fun DetailScreen(
    navController: NavController,
    detailViewModel: DetailViewModel
) {
    val uiState = detailViewModel.uiState.collectAsState()
    Detail(uiState, navController, detailViewModel)
}

@Composable
fun Detail(
    uiState: State<DetailViewModel.DetailState>,
    navController: NavController,
    detailViewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxHeight(1f)
    )
    {
        if (uiState.value.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        if (uiState.value.finished) {
            navController.navigateUp()
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            val name = uiState.value.workshopName ?: ""
            TextField(
                value = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onValueChange = {
                    detailViewModel.checkNameWorkshop(it)
                },
                isError = uiState.value.nameError != null,
                label = { Text(stringResource(id = R.string.label_name)) }
            )
            if (uiState.value.nameError != null) {
                Text(
                    text = stringResource(id = uiState.value.nameError!!),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Red
                )
            }

            val detail = uiState.value.subscription?.detail ?: ""
            TextField(
                value = detail,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onValueChange = {
                    detailViewModel.acceptDetail(it)
                },
                label = { Text(stringResource(id = R.string.label_tag)) }
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = "" + uiState.value.subscription?.lessonNumbers,
                onValueChange = { detailViewModel.acceptNumber(it) },
                label = { Text(stringResource(id = R.string.label_lesson_number)) }
            )
            val choseStartDate = remember { mutableStateOf(false) }
            val startCalendar = Calendar.getInstance()
            startCalendar.time = uiState.value.subscription?.startDate ?: Date()

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
                    startCalendar, onChanged = { newCalendar ->
                        detailViewModel.acceptStartCalendar(newCalendar)
                        choseStartDate.value = false
                    },
                    onDismiss = {
                        choseStartDate.value = false
                    },
                    R.string.label_start_date
                )
            }
            val choseEndDate = remember { mutableStateOf(false) }
            val endCalendar = Calendar.getInstance()
            endCalendar.time = uiState.value.subscription?.endDate ?: Date()

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
                ShowDatePicker(endCalendar, onChanged = { newCalendar ->
                    run {
                        choseEndDate.value = false
                        detailViewModel.acceptEndCalendar(newCalendar)
                    }

                }, onDismiss = {
                    choseStartDate.value = false
                    choseEndDate.value = false
                },
                    R.string.label_end_date
                )
            }

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
                                .clickable(true, onClick = { detailViewModel.addVisitedLesson() })
                                .weight(0.2f)
                                .padding(end = 16.dp)
                        )
                    }

                    if ((uiState.value.subscription?.lessons?.size ?: 0) > 0) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {

                            uiState.value.subscription?.lessons?.forEach {
                                LessonRow(it, detailViewModel)
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

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(
                onClick = {
                    detailViewModel.save()
                },

                modifier = Modifier
                    .padding(horizontal = 16.dp),
                enabled = uiState.value.savingAvailable
            ) {
                Row {
                    Text(stringResource(id = R.string.btn_save))
                }
            }
        }
    }
}

@Composable
fun LessonRow(item: Lesson, detailViewModel: DetailViewModel) {
    val expanded = remember { mutableStateOf(false) }
    Row(

        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
            .shadow(
                elevation = 1.dp,
               // shape = RoundedCornerShape(4.dp)
            )

            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { expanded.value = true },
                    onTap = { /* Called on Tap */ }
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
                    .clickable(true, onClick = { detailViewModel.deleteLesson(item) })
                    .weight(1f)
            )
        })
    if (expanded.value) {
        val start = Calendar.getInstance()
        start.time = item.date
        ShowDatePicker(start, onChanged = { newCalendar ->
            detailViewModel.changeLessonDate(item, newCalendar)
            expanded.value = false
        },
            onDismiss = {
                expanded.value = false
            })
    }
}


