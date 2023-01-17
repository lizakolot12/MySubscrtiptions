package com.mits.subscription.ui.list

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import com.mits.subscription.ui.creating.ShowDatePicker
import com.mits.subscription.ui.theme.md_theme_light_background
import com.mits.subscription.ui.theme.md_theme_light_secondaryContainer
import kotlinx.coroutines.coroutineScope
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

val DATE_FORMATTER = SimpleDateFormat("dd.MM.yyyy")

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
                content = {
                    ListItemView(
                        item,
                        listViewModel,
                        onButtonClicked = {
                            navController.navigate("detail/${item.activeElementId}")
                        }
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
    onButtonClicked: (() -> Unit)? = null
) {
    fun getItemById(id: Long): Subscription? {
        return item.workshop.subscriptions?.firstOrNull { it.id == id }
    }

    Column(
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { onButtonClicked?.invoke() },
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
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column() {
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
                            Column() {
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
                                    Color.Red
                                } else Color.Black,
                                text = "" + activeElement?.lessons?.size + " з " + activeElement?.lessonNumbers,
                            )
                        }
                        val scrollState = rememberScrollState()
                        var maxHeight = 0.dp
                        var sizeInDp by remember { mutableStateOf(DpSize.Zero) }
                        val density = LocalDensity.current
                        if ((activeElement?.lessons?.size ?: 0) > 0) {
                            Column(
                                modifier = Modifier.onSizeChanged {
                                    sizeInDp = density.run {
                                        DpSize(
                                            it.width.toDp(),
                                            it.height.toDp()
                                        )
                                    }
                                }
                                    .heightIn(min = 0.dp, max = 200.dp)
                                    .verticalScroll(scrollState)
                                    //.heightIn(max = 148.dp)

                            ) {
                                var k = 0
                                var hasAddButton = false
                                for (i in 0 until (activeElement?.lessons?.size ?: 0) step 2) {
                                    k += 2
                                    Log.e("TEST", "maxHeight " + maxHeight + "   k = " + k)
                                    if(k > 1 && maxHeight < 1.dp) {
                                        maxHeight = sizeInDp.height
                                    }

                                    Row {
                                        LessonView(
                                            activeElement?.lessons?.get(i),
                                            activeElement?.id ?: -1,
                                            listViewModel
                                        )
                                        if (i + 1 < (activeElement?.lessons?.size ?: 0)) {
                                            LessonView(
                                                activeElement?.lessons?.get(i + 1),
                                                activeElement?.id ?: -1,
                                                listViewModel,
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
                                    LessonView(
                                        activeElement?.lessons?.get(k),
                                        activeElement?.id ?: -1,
                                        listViewModel
                                    )
                                }
                                if (activeElement != null && !hasAddButton) {
                                    AddNewLessonView(listViewModel, activeElement)
                                }
                            }
                        } else {
                            if (activeElement != null) {
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
                    if ((item.workshop.subscriptions?.size
                            ?: 0) > 0 && item.workshop.subscriptions?.get(0) != null
                    ) {
                        ContextMenu(listViewModel, item.workshop.subscriptions?.get(0)!!, expanded)
                    }
                }

            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    )
}

private fun drawRow() {

}

@Composable
fun LessonView(lesson: Lesson?, subscriptionId: Long, listViewModel: ListViewModel) {

    val expanded = remember { mutableStateOf(false) }
    if (expanded.value) {
        val start = Calendar.getInstance()
        start.time = lesson?.date ?: Date()
        ShowDatePicker(start, onChanged = { newCalendar ->
            lesson?.let { listViewModel.changeLessonDate(it, newCalendar, subscriptionId) }
            expanded.value = false
        },
            onDismiss = {
                expanded.value = false
            })
    }
    Button(
        onClick = { expanded.value = true },
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(lesson?.date?.let { DATE_FORMATTER.format(it) } ?: "")
        /*  Spacer(Modifier.size(ButtonDefaults.IconSpacing))
          Icon(
              Icons.Rounded.Close,
              contentDescription = "Відмінити заняття",
              modifier = Modifier
                  .size(ButtonDefaults.IconSize)
                  .clickable {
                      lesson?.let {
                          listViewModel.deleteVisitedLesson(
                              it
                          )
                      }
                  },
          )*/

    }
    /*      Text(
              text = lesson?.date?.let { formatter.format(it) } ?: "",
              overflow = TextOverflow.Ellipsis
          )

          IconButton(onClick = {
              listViewModel.deleteVisitedLesson(lesson)
          }) {
              Icon(Icons.Rounded.Close, "")
          }*/
    /* })*/
}

@Composable
fun AddNewLessonView(listViewModel: ListViewModel, subscription: Subscription) {
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
    ContextMenuSubscription(listViewModel, subscription, expanded)
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
                listViewModel.deleteWorkshop(subscription)
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
