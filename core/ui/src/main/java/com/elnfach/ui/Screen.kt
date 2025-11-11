package com.elnfach.ui

sealed class Screen(
    val route: String
) {
    object Login: Screen("login")
    object Home: Screen("home")
    object Realms: Screen("realms")
}