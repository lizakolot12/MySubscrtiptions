package com.mits.subscription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mits.subscription.ui.creating.CreatingScreen
import com.mits.subscription.ui.creating.CreatingViewModel
import com.mits.subscription.ui.detail.DetailScreen
import com.mits.subscription.ui.detail.DetailViewModel
import com.mits.subscription.ui.list.ListScreen
import com.mits.subscription.ui.list.ListViewModel
import com.mits.subscription.ui.theme.SubscriptionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubscriptionTheme {
                Main(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Main(activity: ComponentActivity) {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
    val createFolder = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 45.dp),
                    )
                },

                actions = {
                    if (currentRoute.value?.destination?.route.equals(Navigation.LIST.route)) {
                        IconButton(onClick = { createFolder.value = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_create_new_folder_24),
                                "", tint = White
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    if (!currentRoute.value?.destination?.route.equals(Navigation.LIST.route)) {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(Icons.Rounded.ArrowBack, "", tint = White)
                        }

                    }
                },
            )
        },
        floatingActionButton = {
            if (currentRoute.value?.destination?.route.equals(Navigation.LIST.route)) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Filled.Add, "", tint = White) },
                    text = { Text(text = stringResource(R.string.btn_new), color = White) },
                    onClick = {
                        navController.navigate(Navigation.NEW.route)
                    },
                )
            }
        }
    ) {
        // Screen content
        NavHost(navController = navController, startDestination = Navigation.LIST.route) {

            composable(Navigation.LIST.route) {
                val listViewModel: ListViewModel by activity.viewModels()
                ListScreen(navController, listViewModel)

            }

            composable(Navigation.NEW.route) {
                val createViewModel: CreatingViewModel by activity.viewModels()
                createViewModel.init()
                CreatingScreen(navController, createViewModel)
            }

            composable(
                Navigation.DETAIL.route + "/{subscriptionId}",
                arguments = listOf(navArgument("subscriptionId") { type = NavType.LongType })
            ) { it ->
                val detailViewModel: DetailViewModel by activity.viewModels()
                detailViewModel.init(it.arguments?.getLong("subscriptionId"))
                DetailScreen(navController, detailViewModel)
            }
        }
        if (createFolder.value) {
            FolderCreatingDialog(createFolder)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FolderCreatingDialog(createFolder: MutableState<Boolean>) {
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
                onClick = { createFolder.value = false }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SubscriptionTheme {
        Main(MainActivity())
    }
}
