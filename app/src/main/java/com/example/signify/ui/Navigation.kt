package com.example.signify.ui

import TextRecognitionScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.signify.presentation.CurrencyScreen
@Composable
fun Navigation(applicationContext: Context)
{
    var navController= rememberNavController()

    NavHost(navController = navController, startDestination =com.example.signify.ui.Screen.HomeScreen.route )
    {

        composable(route=com.example.signify.ui.Screen.CurrencyScreen.route) {
            CurrencyScreen(applicationContext = applicationContext)
        }
        composable(route=com.example.signify.ui.Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route=com.example.signify.ui.Screen.TextRecognitionScreen.route){
            TextRecognitionScreen(applicationContext = applicationContext)
        }
        composable(route=com.example.signify.ui.Screen.LookScreen.route){
            Look(applicationContext)
        }
        composable(route=com.example.signify.ui.Screen.DiscussScreen.route){
            Discuss(applicationContext)
        }
    }
}