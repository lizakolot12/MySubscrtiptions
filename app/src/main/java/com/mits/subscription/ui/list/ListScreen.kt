package com.mits.subscription.ui.list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ListScreen(navController: NavController, listViewModel: ListViewModel) {
    val subscriptions by listViewModel.subscriptions.observeAsState(listOf())
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(items = subscriptions, itemContent = { pos, item ->
            Log.e("TEST", "item $item")
            Row(content = {
                Text(text = item.name)
                Text(text = "" + item.lessons?.size + " з " + item.lessonNumbers)
            })
        })

    }
}