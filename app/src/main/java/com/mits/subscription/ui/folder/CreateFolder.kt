package com.mits.subscription.ui.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import com.mits.subscription.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FolderCreatingDialog(
    createFolder: MutableState<Boolean>,
    createFolderViewModel: CreateFolderViewModel
) {
    val folderName = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            createFolder.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.title_create_folder))
        },
        text = {
            Column() {
                TextField(
                    value = folderName.value,
                    onValueChange = { folderName.value = it }
                )
            }
        },
        confirmButton = {
            Button(
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
                onClick = { createFolder.value = false }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        })

}