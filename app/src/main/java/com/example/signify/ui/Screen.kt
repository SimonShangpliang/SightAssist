package com.example.signify.ui

sealed class Screen(val route:String) {
    object CurrencyScreen:Screen("currency_screen")
    object HomeScreen:Screen("home_screen")
    object TextRecognitionScreen:Screen("textrecognition_screen")
    object LookScreen:Screen("look_screen")
    object DiscussScreen:Screen("discuss_screen")

}