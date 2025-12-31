package com.behcetemre.parayonetimi.util

import androidx.compose.ui.graphics.Color

sealed class Screen (val route: String) {
    object HomeScreen : Screen("home_screen")
    object DetailScreen : Screen("detail_screen/{categoryId}"){
        fun routeWithArgs(categoryId: Int) : String{
            return route.replace("{categoryId}", "$categoryId")
        }
    }
}