package com.mits.subscription.ui.list

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.data.db.SubscriptionDb
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Subscription
import com.mits.subscription.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    if ((listViewModel.subsFolders.value?.size ?: 0) == 0) {
        Image(
            painterResource(id = R.drawable.background), contentDescription = "Фон",
            Modifier.fillMaxSize()
        )
    }
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
                            SubscriptionRow(it, navController, listViewModel)
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
                item.folder.name,
                modifier = Modifier
                    .weight(1F)
                    .padding(4.dp),
                fontWeight = FontWeight.SemiBold,
            )
            Text("" + item.folder.subscriptions?.size, Modifier.padding(6.dp))
            Icon(
                imageVector = if (item.expanded) ImageVector.vectorResource(id = R.drawable.arrow_drop_up)
                else Icons.Default.ArrowDropDown,
                contentDescription = "image",
                modifier = Modifier
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
            .background(md_theme_light_secondaryContainer, shape = RoundedCornerShape(6.dp))
            .combinedClickable(
                onClick = { onButtonClicked?.invoke(item.folder.id, !item.expanded) },
                onLongClick = {
                    contextMenu.value = true
                }
            )
            .padding(8.dp)

    )
}

@Composable
fun SubscriptionRow(
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
                    onLongPress = { expanded.value = true },
                    onTap = { navController.navigate("detail/${subscription.id}") },
                )
            }
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(2.dp)
            )
            .background(md_theme_light_background)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = subscription.name ?: "not_found",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .align(CenterVertically)
            )
            {
                val lesNum = subscription.lessons?.size ?: 0
                Text(
                    color = if ((subscription.lessonNumbers - lesNum) < 2) {
                        Color.Red
                    } else Color.Black,
                    text = "" + subscription.lessons?.size + " з " + subscription.lessonNumbers,
                )
            }
            ContextMenu(listViewModel, subscription, expanded)
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
