package com.example.speedy.navigation.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.speedy.ThemePreferenceManager
import com.example.speedy.firestore.FirestoreUtils.getUserPreference
import com.example.speedy.firestore.FirestoreUtils.updateUserPreference
import com.example.speedy.ui.theme.ThemeVariants
import com.example.speedy.ui.utils.TabTitle
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf("Default") }
    var isMetric by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }

    // Theme options
    val themeOptions = ThemeVariants.keys.toList()

    // Coroutine scope for suspend function calls
    val coroutineScope = rememberCoroutineScope()

    // Fetch preferences on load
    LaunchedEffect(Unit) {
        try {
            ThemePreferenceManager.getThemeName(context).collect { themeName ->
                selectedTheme = themeName
                isLoading = false // Stop loading when a value is received
            }
        } catch (e: Exception) {
            Log.e("SettingsScreen", "Error loading theme name: ${e.message}")
            selectedTheme = "Default"
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Theme Toggle Section
            Text(
                text = "Theme Mode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dark Mode Button
                Button(
                    onClick = { onThemeToggle(true) },
                    enabled = !isDarkTheme,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Dark")
                }

                // Light Mode Button
                Button(
                    onClick = { onThemeToggle(false) },
                    enabled = isDarkTheme,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Light")
                }
            }

            // Theme Selection
            Text(
                text = "Select Theme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                themeOptions.forEach { theme ->
                    val themeColors = ThemeVariants[theme] ?: ThemeVariants["Default"]!!
                    val themeVariant = if (isDarkTheme) themeColors.dark else themeColors.light

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (selectedTheme == theme) themeVariant.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable {
                                selectedTheme = theme
                                coroutineScope.launch {
                                    ThemePreferenceManager.setThemeName(context, theme)
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Color Preview
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(themeVariant.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(themeVariant.secondary)
                            )
                        }

                        // Theme Name
                        Text(
                            text = theme,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedTheme == theme) themeVariant.secondary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Measurement Unit Toggle
            Text(
                text = "Measurement Units",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (!isMetric) {
                            isMetric = true
                            updateUserPreference("isMetric", true) { success, error ->
                                if (success) {
                                    Log.d("Settings", "isMetric updated to Metric")
                                } else {
                                    Log.e("Settings", "Failed to update isMetric: $error")
                                }
                            }
                        }
                    },
                    enabled = !isMetric, // Disable button if already selected
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMetric) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isMetric) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Metric")
                }

                Button(
                    onClick = {
                        if (isMetric) {
                            isMetric = false
                            updateUserPreference("isMetric", false) { success, error ->
                                if (success) {
                                    Log.d("Settings", "isMetric updated to Imperial")
                                } else {
                                    Log.e("Settings", "Failed to update isMetric: $error")
                                }
                            }
                        }
                    },
                    enabled = isMetric, // Disable button if already selected
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isMetric) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (!isMetric) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Imperial")
                }

            }
        }
    }
}
