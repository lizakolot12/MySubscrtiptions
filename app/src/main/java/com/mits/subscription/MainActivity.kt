package com.mits.subscription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mits.subscription.ui.creating.CreatingScreen
import com.mits.subscription.ui.creating.CreatingViewModel
import com.mits.subscription.ui.list.ListScreen
import com.mits.subscription.ui.list.ListViewModel
import com.mits.subscription.ui.theme.Purple40
import com.mits.subscription.ui.theme.PurpleGrey80
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

                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, "", tint = Purple40)
                    }

                },
                backgroundColor = PurpleGrey80,
            )


        },
        containerColor = LightGray,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = Red,
                icon = { Icon(Icons.Filled.Add, "", tint = White) },
                text = { Text(text = stringResource(R.string.btn_new), color = White) },
                onClick = {
                    navController.navigate("create")
                }
            )
        }
    ) {
        // Screen content
        NavHost(navController = navController, startDestination = "list") {

            composable("list") {
                val listViewModel: ListViewModel by activity.viewModels()
                ListScreen(navController, listViewModel)

            }

            composable("create") {
                val createViewModel: CreatingViewModel by activity.viewModels()
                CreatingScreen(navController, createViewModel, activity)
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
