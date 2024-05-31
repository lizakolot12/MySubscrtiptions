package com.mits.subscription

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mits.subscription.ui.creating.CreatingScreen
import com.mits.subscription.ui.detail.DetailScreen
import com.mits.subscription.ui.list.ListScreen
import com.mits.subscription.ui.theme.SubscriptionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TEST", "on create")
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
            Log.e("TEST", "list")
            ListScreen(navController)
        }

        composable(Navigation.NEW.route) {
            CreatingScreen(navController)
        }

        composable(
            Navigation.DETAIL.route + "/{subscriptionId}",
            arguments = listOf(navArgument("subscriptionId") { type = NavType.LongType })
        ) {
            DetailScreen(navController)
        }
    }
}
