package com.mits.subscription.ui.list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Subscription

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    Image(
        painterResource(id = R.drawable.background), contentDescription = "Фон",
        Modifier.fillMaxSize(), contentScale = ContentScale.FillHeight,
        alpha = 0.9f
    )

    val subsFolder by listViewModel.subsFolders.observeAsState(listOf())
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {

        itemsIndexed(items = subsFolder, itemContent = { pos, item ->
            Column(content = {
                ExpandedListItemView(
                    item,
                    onButtonClicked = { id: Long, expanded: Boolean ->
                        listViewModel.changeExpand(
                            item, expanded
                        )
                    })
                //Spacer(modifier = Modifier.size(8.dp))
                if (item.expanded) {
                    item.folder.subscriptions?.forEach {
                        SubscriptionRow(it, navController, listViewModel)
                    }
                }
            })
        })

    }
}

@Composable
fun ExpandedListItemView(
    item: ExpandableListItem,
    onButtonClicked: ((id: Long, expanded: Boolean) -> Unit)? = null
) {
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
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(1.dp))
            .background(Color.LightGray, shape = RoundedCornerShape(6.dp))
            .clickable(onClick = {
                onButtonClicked?.invoke(item.folder.id, !item.expanded)
            })
            .padding(8.dp),

        )
}


@Composable
fun SubscriptionRow(
    subscription: Subscription,
    navController: NavController,
    listViewModel: ListViewModel
) {
    val expanded = remember { mutableStateOf(false) }
    Row(

        modifier = Modifier
            .padding(4.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(2.dp)
            )
            .background(Color.White)
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    // onPress = { navController.navigate("detail/${item.id}") },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { expanded.value = true },
                    onTap = { navController.navigate("detail/${subscription.id}") }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        content = {
            Text(
                text = subscription.name,
                //Modifier.weight(4f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                //   Modifier.weight(1f)
            ) {
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

