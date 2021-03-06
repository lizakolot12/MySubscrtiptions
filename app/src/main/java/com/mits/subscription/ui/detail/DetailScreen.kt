package com.mits.subscription.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Lesson
import com.mits.subscription.parseCalendar
import com.mits.subscription.parseDate
import com.mits.subscription.ui.creating.Folders
import com.mits.subscription.ui.creating.ShowDatePicker
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(
    navController: NavController,
    detailViewModel: DetailViewModel
) {
    val state = detailViewModel.uiState.observeAsState()
    Detail(state, navController, detailViewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Detail(
    uiState: State<DetailViewModel.DetailState?>,
    navController: NavController,
    detailViewModel: DetailViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
    )
    {
        if (uiState.value?.isLoading == true) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        if (uiState.value?.finished == true) {
            navController.navigateUp()
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            val name = uiState.value?.subscription?.name ?: ""
            TextField(
                value = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onValueChange = {
                    detailViewModel.checkName(it)
                },
                isError = uiState.value?.nameError != null,
                label = { Text(stringResource(id = R.string.label_name)) }
            )
            if (uiState.value?.nameError != null) {
                Text(
                    text = stringResource(id = uiState.value?.nameError!!),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Red
                )
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = "" + uiState.value?.subscription?.lessonNumbers,
                onValueChange = { detailViewModel.acceptNumber(it) },
                label = { Text(stringResource(id = R.string.label_lesson_number)) }
            )
            val choseStartDate = remember { mutableStateOf(false) }
            val startCalendar = Calendar.getInstance()
            startCalendar.time = uiState.value?.subscription?.startDate ?: Date()

            Button(
                onClick = { choseStartDate.value = true },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = 16.dp),
            ) {
                Row() {
                    Text(stringResource(id = R.string.label_start_date))
                    Text(
                        text = parseCalendar(startCalendar),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

            }
            if (choseStartDate.value) {
                ShowDatePicker(startCalendar, onChanged = { newCalendar ->
                    detailViewModel.acceptStartCalendar(newCalendar)
                    choseStartDate.value = false
                })
            }
            val choseEndDate = remember { mutableStateOf(false) }
            val endCalendar = Calendar.getInstance()
            endCalendar.time = uiState.value?.subscription?.endDate ?: Date()

            Button(
                onClick = { choseEndDate.value = true },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = 16.dp),

                ) {
                Row() {
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
                })
            }


            val folder = detailViewModel.folders.value?.find {
                it.id == (uiState.value?.subscription?.folderId)
            } ?: Folder(
                SubscriptionDb.DEFAULT_FOLDER_ID,
                stringResource(id = R.string.default_folder_name), emptyList()
            )

            Folders(
                detailViewModel.folders,
                folder,
                onChanged = { detailViewModel.acceptNewFolder(it) })

            Card(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp,  )
            ) {
                Column() {
                    Row(
                        modifier = Modifier.padding(top = 16.dp)
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
                                .padding(end =  16.dp)
                        )
                    }

                    if ((uiState.value?.subscription?.lessons?.size ?: 0) > 0) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        ) {

                            itemsIndexed(items = uiState.value?.subscription?.lessons
                                ?: emptyList(),
                                itemContent = { pos, item ->
                                    LessonRow(item, detailViewModel)
                                })
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
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                detailViewModel.save()
            },

            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
            enabled = uiState.value?.savingAvailable ?: false
        ) {
            Row() {
                Text(stringResource(id = R.string.btn_save))
            }

        }
    }
}

@Composable
fun LessonRow(item: Lesson, detailViewModel: DetailViewModel) {
    val expanded = remember { mutableStateOf(false) }
    Row(

        modifier = Modifier
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(2.dp)
            )

            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { expanded.value = true },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { },
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
        })
    }
}


