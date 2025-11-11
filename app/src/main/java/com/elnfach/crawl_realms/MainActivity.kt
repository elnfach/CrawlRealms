package com.elnfach.crawl_realms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elnfach.auth.screen.LoginScreen
import com.elnfach.home.screen.HomeScreen
import com.elnfach.realms.screen.RealmScreen
import com.elnfach.ui.theme.CrawlRealmsTheme
import com.elnfach.ui.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrawlRealmsTheme {
                Scaffold { it ->
                    val navController = rememberNavController()
                    AppNavHost(navController, Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Realms.route) {
            RealmScreen()
        }
    }
}