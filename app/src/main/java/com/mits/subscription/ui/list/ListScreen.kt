package com.mits.subscription.ui.list

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Subscription
import kotlin.math.roundToInt

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    Image(
        painterResource(id = R.drawable.background), contentDescription = "Фон",
        Modifier.fillMaxSize(), contentScale = ContentScale.FillHeight,
        alpha = 0.9f
    )

    val subsFolder by listViewModel.subsFolders.observeAsState(listOf())

    val mapPosition: MutableMap<Long, Offset> = HashMap()
    subsFolder.forEach {
        val currentPosition by remember { mutableStateOf(Offset.Zero) }
        mapPosition[it.folder.id] = currentPosition
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {

        itemsIndexed(items = subsFolder, itemContent = { pos, item ->
            Column(
                modifier = Modifier.onGloballyPositioned {
                    val currentPosition = it.localToWindow(Offset.Zero)
                    mapPosition[item.folder.id] = currentPosition
                },
                content = {
                    ExpandedListItemView(
                        item,
                        listViewModel,
                        onButtonClicked = { id: Long, expanded: Boolean ->
                            listViewModel.changeExpand(
                                item, expanded
                            )
                        })
                    if (item.expanded) {
                        item.folder.subscriptions?.forEach {
                            SubscriptionRow(it, navController, listViewModel, mapPosition)
                        }
                    }
                })
        })

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandedListItemView(
    item: ExpandableListItem,
    listViewModel: ListViewModel,
    onButtonClicked: ((id: Long, expanded: Boolean) -> Unit)? = null
) {
    val contextMenu = remember { mutableStateOf(false) }

    Row(
        content = {
            Text(
                item.folder.name, modifier = Modifier
                    .weight(1F)
                    .padding(4.dp), fontWeight = FontWeight.Bold
            )
            Text("" + item.folder.subscriptions?.size, Modifier.padding(6.dp))
            Icon(
                imageVector = if (item.expanded) ImageVector.vectorResource(id = R.drawable.arrow_drop_up)
                else Icons.Default.ArrowDropDown,
                contentDescription = "image",
                tint = Color.White, modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        onButtonClicked?.invoke(item.folder.id, !item.expanded)
                    })
            )
            ContextMenuFolder(listViewModel, item.folder, contextMenu)
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(1.dp))
            .background(Color.LightGray, shape = RoundedCornerShape(6.dp))
            .combinedClickable(
                onClick = { onButtonClicked?.invoke(item.folder.id, !item.expanded) },
                onLongClick = {
                    if (item.folder.id != SubscriptionDb.DEFAULT_FOLDER_ID) {
                        contextMenu.value = true
                    }
                }
            )
            .padding(8.dp)

    )
}

@Composable
fun SubscriptionRow(
    subscription: Subscription,
    navController: NavController,
    listViewModel: ListViewModel,
    mapPosition: MutableMap<Long, Offset>
) {
    val expanded = remember { mutableStateOf(false) }
    var offsetY by remember { mutableStateOf(0f) }
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    Row(
        modifier = Modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
                // Log.e("TST", "current " + currentPosition)
            }
            .offset { IntOffset(0, offsetY.roundToInt()) }
/*            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    // Log.e("TEST", "Offset = " + offsetY)
                    offsetY += delta
                },
                onDragStopped = {
                    val folder: Long? = checkIntersection(mapPosition, currentPosition, offsetY)
                    Log.e("TEST", "folder = " + folder)
                    if (folder != null && folder > 0 && folder != subscription.folderId) {
                        listViewModel.moveToFolder(folder, subscription)
                        Log.e("TEST", "must move to folder " + folder)
                    } else {
                        Log.e("TEST", "must go back")
                        offsetY = 0f
                    }
                }
            )*/
            .pointerInput(Unit) {
                detectTapGestures(
                    // onPress = { navController.navigate("detail/${item.id}") },
                    onDoubleTap = { expanded.value = true },
                    onLongPress = { expanded.value = true },
                    onTap = { navController.navigate("detail/${subscription.id}") },
                )
            }
            .padding(4.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(2.dp)
            )
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = subscription.name,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box {
                val lesNum = subscription.lessons?.size ?: 0
                Text(

                    color = if ((subscription.lessonNumbers - lesNum) < 2) {
                        Color.Red
                    } else Color.Green,
                    text = "" + subscription.lessons?.size + " з " + subscription.lessonNumbers,
                )
            }
            ContextMenu(listViewModel, subscription, expanded)
        })
}

fun checkIntersection(
    mapPosition: MutableMap<Long, Offset>,
    currentPosition: Offset,
    offsetY: Float
): Long? {
    val curPos = currentPosition.y + offsetY
    var init = 0

    val result = mapPosition.filter { entry ->
        if (curPos > init && curPos <= entry.value.y) true
        else {
            init = entry.value.y.roundToInt()
            false

        }
    }.keys.firstOrNull()
    return result
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
        DropdownMenuItem(onClick = {
            listViewModel.delete(subscription)
            expanded.value = false
        }) {

            Text(
                stringResource(id = R.string.delete),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(id = R.string.description_add_lesson),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        DropdownMenuItem(
            onClick = {
                listViewModel.addVisitedLesson(subscription)
                expanded.value = false
            },
        ) {
            Text(
                stringResource(id = R.string.description_add_lesson),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check, contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )


        }
        DropdownMenuItem(onClick = {
            listViewModel.copy(subscription)
            expanded.value = false
        }) {
            Text(stringResource(id = R.string.copy_subscription))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ContextMenuFolder(
    listViewModel: ListViewModel,
    folder: Folder,
    expanded: MutableState<Boolean>
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {
        DropdownMenuItem(onClick = {
            listViewModel.deleteFolder(folder)
            expanded.value = false
        }) {

            Text(
                stringResource(id = R.string.delete),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(id = R.string.description_add_lesson),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
