package com.mits.subscription.ui.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.mits.subscription.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FolderCreatingDialog(
    createFolder: MutableState<Boolean>,
    createFolderViewModel: CreateFolderViewModel
) {
    val folderName = remember { mutableStateOf("") }
    AlertDialog(
        text = {
            Column() {
                Text(
                    stringResource(id = R.string.title_create_folder),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                TextField(
                    modifier = Modifier.padding(top = 16.dp),
                    value = folderName.value,
                    onValueChange = { folderName.value = it },
                )
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = true),
        onDismissRequest = {
            createFolder.value = false
        },
        confirmButton = {
            Button(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                onClick = {
                    createFolder.value = false
                    createFolderViewModel.createFolder(folderName.value)
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier.padding(bottom = 16.dp),
                onClick = { createFolder.value = false }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        })
}