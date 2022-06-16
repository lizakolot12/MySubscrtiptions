package com.mits.subscription.ui.list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.compose.material.BackdropScaffold
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mits.subscription.R
import com.mits.subscription.model.Subscription

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    Image(
        painterResource(id = R.drawable.background), contentDescription = "Фон",
        Modifier.fillMaxSize(), contentScale = ContentScale.FillHeight,
        alpha = 0.9f
    )

    val subscriptions by listViewModel.subscriptions.observeAsState(listOf())
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {

        itemsIndexed(items = subscriptions, itemContent = { pos, item ->
            val expanded = remember { mutableStateOf(false) }
            Row(

                modifier = Modifier
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
                            onTap = { navController.navigate("detail/${item.id}") }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                content = {
                    Text(
                        text = item.name,
                        //Modifier.weight(4f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        //   Modifier.weight(1f)
                    ) {
                        val lesNum = item.lessons?.size ?: 0
                        Text(

                            color = if ((item.lessonNumbers - lesNum) < 2) {
                                Color.Red
                            } else Color.Green,
                            text = "" + item.lessons?.size + " з " + item.lessonNumbers,
                        )
                    }
                    ContextMenu(listViewModel, item, expanded)
                })
        })

    }
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
