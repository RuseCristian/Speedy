package com.example.speedy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speedy.Screens
import com.example.speedy.navigation.screens.AboutScreen
import com.example.speedy.navigation.screens.DataSetsScreen
import com.example.speedy.navigation.screens.bottomNav.AerodynamicsScreen
import com.example.speedy.navigation.screens.bottomNav.CarScreen
import com.example.speedy.navigation.screens.bottomNav.DrivetrainScreen
import com.example.speedy.navigation.screens.bottomNav.ResultsScreen
import com.example.speedy.navigation.screens.SettingsScreen
import com.example.speedy.navigation.screens.bottomNav.TiresScreen
import com.example.speedy.ui.utils.SharedViewModel


@Composable
fun NavDrawerBotMenu(
    sharedViewModel: SharedViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState = drawerState,
                navController = navController,
                coroutineScope = coroutineScope,
                sharedViewModel = sharedViewModel
            )
        }
    ) {
        Scaffold(
            topBar = { AppTopBar(drawerState, coroutineScope, title = "Speedy") },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screens.Car.screen,
                modifier = Modifier.padding(paddingValues),
            ) {
                composable(Screens.Car.screen) { CarScreen(sharedViewModel) }
                composable(Screens.Drivetrain.screen) { DrivetrainScreen(sharedViewModel) }
                composable(Screens.Tires.screen) { TiresScreen(sharedViewModel) }
                composable(Screens.Aerodynamics.screen) { AerodynamicsScreen(sharedViewModel) }
                composable(Screens.Results.screen) { ResultsScreen(sharedViewModel) }
                composable(Screens.Settings.screen) {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle
                    )
                }
                composable(Screens.About.screen){ AboutScreen() }
                composable(Screens.DataSets.screen) { DataSetsScreen(sharedViewModel) }
            }
        }
    }
}
