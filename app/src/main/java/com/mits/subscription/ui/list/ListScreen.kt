package com.mits.subscription.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import com.mits.subscription.ui.creating.ShowDatePicker
import com.mits.subscription.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

val DATE_FORMATTER = SimpleDateFormat("dd.MM.yyyy", Locale.US)

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel = hiltViewModel()) {
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

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 88.dp
        ),
    ) {

        itemsIndexed(items = workshops, itemContent = { _, item ->

            Column(
                modifier = Modifier
                    .background(
                        md_theme_light_surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp),
                content = {
                    val openDialog = remember { mutableStateOf(false) }

                    val listener = { openDialog.value = true }

                    if (openDialog.value) {
                        InputMessage(openDialog = openDialog, addMessageWorkshopListener = {
                            item.getActiveElement()
                                ?.let { it1 -> listViewModel.addMessage(it, it1) }
                        })
                    }
                    Workshop(
                        {
                            item.getActiveElement()?.let { listViewModel.addVisitedLesson(it) }
                        },
                        {
                            item.getActiveElement()?.let { listViewModel.copy(it) }
                        },
                        {
                            item.getActiveElement()?.let { listViewModel.deleteWorkshop(it) }
                        },
                        {
                            listener.invoke()
                        },
                        item,
                        listViewModel
                    ) {
                        navController.navigate("detail/${item.activeElementId}")
                    }

                    val scrollStateHorizontal = rememberScrollState()
                    AnimatedVisibility(((item.workshop.subscriptions?.size ?: 0) > 1)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputMessage(
    openDialog: MutableState<Boolean>,
    addMessageWorkshopListener: (message: String) -> Unit?
) {
    val initValue = stringResource(id = R.string.warning_message)
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = initValue, TextRange(0, 7)
            )
        )
    }

    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.add_message))
        },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                    addMessageWorkshopListener.invoke(text.text)
                }
            ) {
                Text(stringResource(id = R.string.btn_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.btn_cancel))
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Workshop(
    addVisitedLessonListener: () -> Unit?,
    copySubscriptionListener: () -> Unit?,
    deleteWorkshopListener: () -> Unit?,
    addMessageWorkshopListener: () -> Unit?,
    item: WorkshopViewItem,
    listViewModel: ListViewModel,
    onButtonClicked: (() -> Unit)? = null
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onButtonClicked?.invoke() }
            .animateContentSize()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            val activeElement = item.getActiveElement()
            Column(
                modifier = Modifier
                    .padding(8.dp)
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            stringResource(id = R.string.label_start_period),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        Text(
                            stringResource(id = R.string.label_end_period),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    }
                    Column {
                        Text(
                            DATE_FORMATTER.format(activeElement?.startDate ?: Date()),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        Text(
                            DATE_FORMATTER.format(activeElement?.endDate ?: Date()),
                            modifier = Modifier
                                .padding(4.dp)
                        )
                    }
                    val lesNum = activeElement?.lessons?.size ?: 0
                    Text(
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = if (((activeElement?.lessonNumbers ?: 0) - lesNum) < 2) {
                            md_theme_light_error
                        } else Color.Black,
                        text = "" + activeElement?.lessons?.size + " з " + activeElement?.lessonNumbers,
                    )
                }

                if ((activeElement?.lessons?.size ?: 0) > 0) {
                    activeElement?.let { VisitedLesson(listViewModel, activeElement) }
                } else {
                    activeElement?.let { AddNewLesson(listViewModel, activeElement) }

                }
                if (item.getActiveElement()?.message != null) {
                    val textValue = item.getActiveElement()?.message ?: ""
                    val expanded = remember { mutableStateOf(false) }
                    Text(
                        textValue,
                        maxLines = 1,
                        color = md_theme_dark_onError,
                        modifier = Modifier
                            .basicMarquee()
                            .clickable {
                                item
                                    .getActiveElement()
                                    ?.let {
                                        expanded.value = true

                                    }
                            })
                    item.getActiveElement()?.let {
                        ContextMenuMessage(
                            listViewModel = listViewModel,
                            subscription = it,
                            expanded = expanded
                        )
                    }
                }

            }
            ContextMenu(
                addVisitedLessonListener,
                copySubscriptionListener,
                deleteWorkshopListener,
                addMessageWorkshopListener,
            )
        }

    }
}

@Composable
private fun ContextMenu(
    addVisitedLessonListener: () -> Unit?,
    copySubscriptionListener: () -> Unit?,
    deleteWorkshopListener: () -> Unit?,
    addMessageWorkshopListener: () -> Unit?,
) {
    val expanded = remember { mutableStateOf(false) }
    IconButton(
        modifier = Modifier.size(24.dp),
        onClick = { expanded.value = true }
    ) {
        Icon(
            Icons.Filled.MoreVert,
            "menu",
        )
    }
    ContextMenu(
        addVisitedLessonListener,
        copySubscriptionListener,
        deleteWorkshopListener,
        addMessageWorkshopListener,
        expanded
    )
}

@Composable
private fun VisitedLesson(listViewModel: ListViewModel, activeElement: Subscription) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .heightIn(
                min = 0.dp,
                max = 200.dp
            )
            .verticalScroll(scrollState)
    ) {

        val columnCount = 2
        for (i in 0..(activeElement.lessons?.size ?: 0) step columnCount) {
            Row {
                for (j in i until i + columnCount) {
                    if (j == (activeElement.lessons?.size ?: 0)) {
                        AddNewLesson(
                            listViewModel,
                            activeElement
                        )
                        break
                    }
                    Lesson(
                        activeElement.lessons?.get(j),
                        activeElement.id,
                        listViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun Lesson(lesson: Lesson?, subscriptionId: Long, listViewModel: ListViewModel) {
    val expanded = remember { mutableStateOf(false) }
    if (expanded.value) {
        val start = Calendar.getInstance()
        start.time = lesson?.date ?: Date()
        ShowDatePicker(start, onChange = { newCalendar ->
            lesson?.let { listViewModel.changeLessonDate(it, newCalendar, subscriptionId) }
            expanded.value = false
        },
            onDismiss = {
                expanded.value = false
            })
    }
    OutlinedButton(
        onClick = { expanded.value = true },
        modifier = Modifier.padding(4.dp)
    ) {
        Text(lesson?.date?.let { DATE_FORMATTER.format(it) } ?: "")

    }
}

@Composable
fun AddNewLesson(listViewModel: ListViewModel, subscription: Subscription) {
    ElevatedButton(
        onClick = { listViewModel.addVisitedLesson(subscription) },
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            Icons.Rounded.Add,
            contentDescription = "Додати заняття",
            modifier = Modifier.size(ButtonDefaults.IconSize),
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(stringResource(id = R.string.add_new_lesson))

    }
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
                    onDoubleTap = { navController.navigate("detail/${subscription.id}") },
                    onLongPress = { expanded.value = true },
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
                    2.dp
                } else {
                    0.dp
                }, color = if (workshopViewItem.activeElementId == subscription.id) {
                    md_theme_light_primaryContainer
                } else {
                    Color.Transparent
                }, shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = if (workshopViewItem.activeElementId == subscription.id) {
                    20.dp
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
                    md_theme_light_error
                } else Color.Black,
                text = "" + subscription.lessons?.size + " з " + subscription.lessonNumbers,
            )
        })
    ContextMenuSubscription(listViewModel, subscription, expanded)
}

@Composable
fun ContextMenu(
    addVisitedLessonListener: () -> Unit?,
    copySubscriptionListener: () -> Unit?,
    deleteWorkshopListener: () -> Unit?,
    addMessageWorkshopListener: () -> Unit?,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(
            onClick = {
                addVisitedLessonListener()
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
            copySubscriptionListener()
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
                deleteWorkshopListener()
                expanded.value = false
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            })

        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.add_message)) },
            onClick = {
                addMessageWorkshopListener()
                expanded.value = false
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = null
                )
            })
    }
}

@Composable
fun ContextMenuSubscription(
    listViewModel: ListViewModel,
    subscription: Subscription,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(onClick = {
            listViewModel.deleteSubscription(subscription)
            expanded.value = false
        },
            text = { Text(stringResource(id = R.string.delete)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            })
    }
}

@Composable
fun ContextMenuMessage(
    listViewModel: ListViewModel,
    subscription: Subscription,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(onClick = {
            listViewModel.removeMessage(subscription)
            expanded.value = false
        },
            text = { Text(stringResource(id = R.string.delete)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            })
    }
}
