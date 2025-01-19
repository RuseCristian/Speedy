package com.example.speedy.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.speedy.R
import com.example.speedy.firestore.UserPreferencesCache
import com.example.speedy.ui.utils.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun DrawerContent(
    drawerState: DrawerState,
    navController: NavController,
    coroutineScope: CoroutineScope,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        // Drawer Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Profile Image
                androidx.compose.foundation.Image(
                    painter = rememberAsyncImagePainter(currentUser?.photoUrl ?: R.drawable.baseline_account_circle_24),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // User Name
                Text(
                    text = currentUser?.displayName ?: "Guest",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                // User Email
                Text(
                    text = currentUser?.email ?: "No Email",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Divider Below Header
        Divider(thickness = 1.dp)

        // Home Navigation Item
        NavigationDrawerItem(
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate("car") // Navigate to the "Home" screen
            }
        )

        // Settings Navigation Item
        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate("settings") // Navigate to the "Settings" screen
            }
        )

        // Divider Between Sections
        Divider(thickness = 1.dp)

        // Datasets Navigation Item
        NavigationDrawerItem(
            label = { Text("Datasets") },
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate("datasets") // Navigate to the "Settings" screen
            }
        )

        // About Navigation Item
        NavigationDrawerItem(
            label = { Text("About") },
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                navController.navigate("about") // Navigate to the "Settings" screen
            }
        )

        // Final Divider
        Divider(thickness = 1.dp)

        // Logout Section
        NavigationDrawerItem(
            label = { Text("Logout") },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            selected = false,
            onClick = {
                coroutineScope.launch {
                    drawerState.close()
                    UserPreferencesCache.preferences = null // Clearing preference cache
                    auth.signOut()
                    sharedViewModel.resetState()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    (context as Activity).recreate() // Navigate back to login
                }
            }
        )
    }
}
