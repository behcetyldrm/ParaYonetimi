package com.behcetemre.parayonetimi.util

sealed class Screen (val route: String) {
    object HomeScreen : Screen("home_screen")
    //object SpendingScreen : Screen("spending_screen")
}