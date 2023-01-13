package com.mits.subscription.ui.list

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Workshop
import com.mits.subscription.model.Subscription
import com.mits.subscription.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    if ((listViewModel.workshop.value?.size ?: 0) == 0) {
        Image(
            painterResource(id = R.drawable.background), contentDescription = "Фон",
            Modifier.fillMaxSize()
        )
    }
    val workshops by listViewModel.workshop.observeAsState(listOf())
    List(workshops, navController, listViewModel)
}

@Composable
fun List(
    workshops: List<WorkshopViewItem>,
    navController: NavController,
    listViewModel: ListViewModel
) {
    Log.e("TEST", "compose list")
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 88.dp
        ),
    ) {

        itemsIndexed(items = workshops, itemContent = { pos, item ->

            Column(
                modifier = Modifier
                    .background(
                        md_theme_light_secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ),
                /*.verticalScroll(scrollStateVertical),*/
                content = {
                    ListItemView(
                        item,
                        listViewModel,
                        onButtonClicked = { id: Long, expanded: Boolean ->
                            /*   listViewModel.changeActiveElement(
                                   item, expanded
                               )*/
                        }, navController = navController
                    )
                    if ((item.workshop.subscriptions?.size ?: 0) > 1) {
                        val scrollStateHorizontal = rememberScrollState()

                        Row(
                            Modifier
                                .horizontalScroll(scrollStateHorizontal)
                                .padding(vertical = 8.dp)
                        ) {
                            item.workshop.subscriptions?.forEach {
                                SubscriptionSmall(item, it, navController, listViewModel)
                            }
                        }
                    }
                })
        })

    }
}

@Composable
fun ListItemView(
    item: WorkshopViewItem,
    listViewModel: ListViewModel,
    navController: NavController,
    onButtonClicked: ((id: Long, expanded: Boolean) -> Unit)? = null
) {
    val contextMenu = remember { mutableStateOf(false) }
    fun getItemById(id: Long): Subscription? {
        return item.workshop.subscriptions?.firstOrNull { it.id == id }
    }
    Column(
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { navController.navigate("detail/${item.activeElementId}") },
                        )
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                )
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    val expanded = remember { mutableStateOf(false) }
                    val activeElement = getItemById(item.activeElementId)
                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(0.8f)
                    ) {
                        Text(
                            item.workshop.name,
                            modifier = Modifier
                                .padding(4.dp),
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            activeElement?.detail ?: "default",
                            modifier = Modifier
                                .padding(4.dp),
                            fontWeight = FontWeight.Light,
                        )
                        if ((activeElement?.lessons?.size ?: 0) > 0) {
                            var k = 0
                            var hasAddButton = false
                            for (i in 0 until (activeElement?.lessons?.size ?: 0) step 2) {
                                k += 2
                                Row() {
                                    LessonView(activeElement?.lessons?.get(i), listViewModel)
                                    if (i + 1 < (activeElement?.lessons?.size ?: 0)) {
                                        LessonView(
                                            activeElement?.lessons?.get(i + 1),
                                            listViewModel
                                        )
                                    } else {
                                        activeElement?.let {
                                            hasAddButton = true
                                            AddNewLessonView(listViewModel, activeElement)
                                        }
                                    }
                                }

                            }
                            if (k < (activeElement?.lessons?.size ?: (0 - 1))) {
                                LessonView(activeElement?.lessons?.get(k), listViewModel)
                            }
                            if (activeElement != null && !hasAddButton) {
                                AddNewLessonView(listViewModel, activeElement)
                            }
                        }
                    }
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = { expanded.value = true }
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            "menu",
                            //modifier = Modifier.padding(4.dp)
                        )
                    }
                    item.workshop.subscriptions?.get(0)
                        ?.let { ContextMenu(listViewModel, it, expanded) }
                }

            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    )
}

@Composable
fun LessonView(lesson: Lesson?, listViewModel: ListViewModel) {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    Row(
        modifier = Modifier
            .padding(start = 16.dp)
            .border(
                width = 1.dp, color =
                Color.Blue, shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(shape = RoundedCornerShape(8.dp), color = md_theme_light_background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = lesson?.date?.let { formatter.format(it) } ?: "",
                overflow = TextOverflow.Ellipsis
            )

            IconButton(onClick = {
                listViewModel.deleteVisitedLesson(lesson)
            }) {
                Icon(Icons.Rounded.Close, "")
            }
        })
}

@Composable
fun AddNewLessonView(listViewModel: ListViewModel, subscription: Subscription) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp)
            .border(
                width = 1.dp, color =
                Color.Blue, shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                listViewModel.addVisitedLesson(subscription)
            }
            .background(shape = RoundedCornerShape(8.dp), color = md_theme_light_background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = stringResource(id = R.string.add_new_lesson),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        })
}

@Composable
fun SubscriptionSmall(
    workshopViewItem: WorkshopViewItem,
    subscription: Subscription,
    navController: NavController,
    listViewModel: ListViewModel
) {
    val expanded = remember { mutableStateOf(false) }
    val offsetY by remember { mutableStateOf(0f) }
    Row(
        modifier = Modifier
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { expanded.value = true },
                    onLongPress = { navController.navigate("detail/${subscription.id}") },
                    onTap = {
                        listViewModel.changeActiveElement(
                            workshopViewItem,
                            subscription.id
                        )
                    },
                )
            }
            .padding(start = 16.dp)
            .border(
                width = if (workshopViewItem.activeElementId == subscription.id) {
                    1.dp
                } else {
                    0.dp
                }, color = if (workshopViewItem.activeElementId == subscription.id) {
                    Color.Blue
                } else {
                    Color.Transparent
                }, shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = if (workshopViewItem.activeElementId == subscription.id) {
                    10.dp
                } else {
                    1.dp
                },
                shape = RoundedCornerShape(8.dp)
            )
            .background(shape = RoundedCornerShape(8.dp), color = md_theme_light_background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = subscription.detail ?: "not_found",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            val lesNum = subscription.lessons?.size ?: 0
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = if ((subscription.lessonNumbers - lesNum) < 2) {
                    Color.Red
                } else Color.Black,
                text = "" + subscription.lessons?.size + " з " + subscription.lessonNumbers,
            )
        })
}

@Composable
fun ContextMenu(
    listViewModel: ListViewModel,
    subscription: Subscription,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(
            onClick = {
                listViewModel.addVisitedLesson(subscription)
                expanded.value = false
            },
            text = { Text(stringResource(id = R.string.description_add_lesson)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(onClick = {
            listViewModel.copy(subscription)
            expanded.value = false
        },
            text = { Text(stringResource(id = R.string.copy_subscription)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                    contentDescription = null
                )
            })

        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.delete)) },
            onClick = {
                listViewModel.delete(subscription)
                expanded.value = false
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            })
    }
}

@Composable
fun ContextMenuFolder(
    listViewModel: ListViewModel,
    workshop: Workshop,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(onClick = {
            listViewModel.deleteFolder(workshop)
            expanded.value = false
        },
            /*  colors = MenuItemColors(textColor = md_theme_light_outline),*/
            text = { Text(stringResource(id = R.string.delete)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            })
    }
}
