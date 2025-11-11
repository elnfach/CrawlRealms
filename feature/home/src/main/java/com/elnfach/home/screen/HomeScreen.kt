package com.elnfach.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elnfach.home.viewmodel.HomeViewModel
import com.elnfach.ui.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        if(false) {
            navController.navigate(Screen.Realms.route) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        homeViewModel.startListening()
    }

    HomeContent()
}

@Composable
fun HomeContent()
{
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(onClick = {

        }) {
            Text("Создать игру")
        }
        TextButton(onClick = {

        }) {
            Text("Присоединиться к игре")
        }
        TextButton(onClick = {

        }) {
            Text("Настройки")
        }
        TextButton(onClick = {

        }) {
            Text("Выйти из игры")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview()
{
    HomeContent()
}