package com.mits.subscription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mits.subscription.presenatation.ui.creating.CreatingScreen
import com.mits.subscription.presenatation.ui.detail.DetailScreen
import com.mits.subscription.presenatation.ui.list.ListScreen
import com.mits.subscription.presenatation.ui.shared.SharedScreen
import com.mits.subscription.presenatation.ui.theme.SubscriptionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubscriptionTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Navigation.LIST.route) {
        composable(Navigation.LIST.route) {
            ListScreen({
                navController.navigate(Navigation.NEW.route)
            }, { item ->
                navController.navigate("detail/${item}")
            })
        }

        composable(Navigation.NEW.route) {
            CreatingScreen({ navController.navigateUp() })
        }

        composable(
            Navigation.DETAIL.route + "/{subscriptionId}",
            arguments = listOf(navArgument("subscriptionId") { type = NavType.LongType })
        ) {
            DetailScreen({ navController.navigateUp() })
        }
        composable(Navigation.SEND.route + "/sharedUri={sharedUri}",
            arguments = listOf(
                navArgument("sharedUri") {
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            val context = LocalContext.current
            SharedScreen({
                val activity = context as? Activity
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()

            })
        }
    }

    val context = LocalContext.current
    val sharedUri = remember {
        (context as? ComponentActivity)?.intent?.takeIf {
            it.action == Intent.ACTION_SEND
        }?.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
    }

    LaunchedEffect(sharedUri) {
        if (sharedUri != null) {
            val encoded = Uri.encode(sharedUri.toString())
            navController.navigate("${Navigation.SEND.route}/sharedUri=$encoded")
        }
    }
}