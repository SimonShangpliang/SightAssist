package com.example.signify.ui

import TextRecognitionScreen
import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.signify.presentation.CurrencyScreen
import kotlinx.coroutines.launch
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(applicationContext: Context)
{
    var navController= rememberNavController()
    val items = listOf(
        NavigationItem(
            title = com.example.signify.ui.Screen.HomeScreen.route,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            badgeCount = 45
        ),
        NavigationItem(
            title = com.example.signify.ui.Screen.CurrencyScreen.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ),

        NavigationItem(
            title = com.example.signify.ui.Screen.TextRecognitionScreen.route,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            badgeCount = 45
        ),
        NavigationItem(
            title = com.example.signify.ui.Screen.LookScreen.route,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            badgeCount = 45
        ),
        NavigationItem(
            title = com.example.signify.ui.Screen.DiscussScreen.route,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            badgeCount = 45
        )

    )
    var selectedItemIndex by remember { mutableStateOf(0)
    }
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope= rememberCoroutineScope()
    ModalNavigationDrawer(modifier = Modifier
        , drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.Black) {
                Spacer(modifier = Modifier.padding(top=10.dp))
                items.forEachIndexed { index, item ->
                    Spacer(modifier = Modifier.padding(10.dp))
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            navController.navigate(item.title)
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title

                            )
                        },
                        badge = {
                            item.badgeCount?.let {
                                Text(text = item.badgeCount.toString())
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)

                    )
                }

            }
        }, drawerState = drawerState
    ) {
        Scaffold(modifier= Modifier, containerColor =
        Color.Black//    Color(0x90000000)
            ,topBar = {
                TopAppBar(modifier= Modifier,colors= TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0x0FF8F8F8), ),
                    title = { Text(text = "SightAssist",color= Color.White)



                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",

                                tint= Color.White
                            )
                        }
                    }
                )
            }
        ) {it->
            Text("", modifier = Modifier.padding(it))
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
    }}
}
}