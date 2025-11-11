package com.elnfach.auth.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun AuthScreen(
    navController: NavController,
) {
    AuthContent()
}

@Composable
fun AuthContent()
{

}

@Preview()
@Composable
fun RegistrationScreenPreview() {
    AuthContent()
}