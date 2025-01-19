package com.example.speedy

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speedy.firestore.FirestoreUtils.getUserPreference
import com.example.speedy.firestore.FirestoreUtils.updateUserPreference
import com.example.speedy.navigation.NavDrawerBotMenu
import com.example.speedy.navigation.screens.LoadingScreen
import com.example.speedy.navigation.screens.LoginScreen
import com.example.speedy.ui.theme.SpeedyMaterialTheme
import com.example.speedy.ui.utils.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object ThemePreferenceManager {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    private val THEME_NAME_KEY = stringPreferencesKey("theme_name")

    // Save the dark theme preference
    suspend fun setDarkTheme(context: Context, isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDarkTheme
        }
    }

    // Read the dark theme preference
    fun isDarkTheme(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false // Default to light theme
        }
    }

    // Save the selected theme name
    suspend fun setThemeName(context: Context, themeName: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_NAME_KEY] = themeName
        }
    }

    // Read the selected theme name
    fun getThemeName(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_NAME_KEY] ?: "Default" // Default theme
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val sharedViewModel: SharedViewModel = viewModel()
            val auth = FirebaseAuth.getInstance()
            val context = LocalContext.current

            // State to manage theme and loading
            var isDarkTheme by remember { mutableStateOf(false) }
            var selectedTheme by remember { mutableStateOf("Default") }
            var isLoading by remember { mutableStateOf(true) }

            // Observe theme preferences
            LaunchedEffect(Unit) {
                combine(
                    ThemePreferenceManager.isDarkTheme(context),
                    ThemePreferenceManager.getThemeName(context)
                ) { darkTheme, themeName ->
                    isDarkTheme = darkTheme
                    selectedTheme = themeName
                }.collect {
                    isLoading = false // Stop loading once both preferences are loaded
                }
            }

            SpeedyMaterialTheme(darkTheme = isDarkTheme) {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LoadingScreen()
                }

                AnimatedVisibility(
                    visible = !isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        if (auth.currentUser != null) {
                            NavDrawerBotMenu(
                                sharedViewModel = sharedViewModel,
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { newTheme ->
                                    isDarkTheme = newTheme
                                    // Save the theme locally
                                    lifecycleScope.launch {
                                        ThemePreferenceManager.setDarkTheme(context, newTheme)
                                    }
                                }
                            )
                        } else {
                            LoginScreen {
                                recreate() // Restart the activity after login
                            }
                        }
                    }
                }
            }
        }
    }
}
