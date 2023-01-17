package com.mits.subscription

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(activity: ComponentActivity) {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(R.string.title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 45.dp),
                    )
                },

                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    if (!currentRoute.value?.destination?.route.equals(Navigation.LIST.route)) {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(Icons.Rounded.ArrowBack, "")
                        }

                    }
                },
            )
        },
        floatingActionButton = {
            if (currentRoute.value?.destination?.route.equals(Navigation.LIST.route)) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Filled.Add, "") },
                    text = { Text(text = stringResource(R.string.btn_new)) },
                    onClick = {
                        navController.navigate(Navigation.NEW.route)
                    },
                )
            }
        },

        ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
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
                    Log.e("TEST", "create detail screen " + it.arguments?.getLong("subscriptionId"))
                    val detailViewModel: DetailViewModel by activity.viewModels()
                    detailViewModel.init(it.arguments?.getLong("subscriptionId"))
                    DetailScreen(navController, detailViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SubscriptionTheme {
        Main(MainActivity())
    }
}
