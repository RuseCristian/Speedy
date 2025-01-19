package com.example.speedy.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.speedy.Screens
import androidx.compose.ui.res.painterResource
import com.example.speedy.R.drawable.baseline_air_24 as wind_icon
import com.example.speedy.R.drawable.transmission as transmission_icon
import com.example.speedy.R.drawable.baseline_tire_repair_24 as tire_icon
import com.example.speedy.R.drawable.baseline_car_repair_24 as car_icon
import com.example.speedy.R.drawable.baseline_auto_graph_24 as graph_icon


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items: List<Pair<Screens, Any>> = listOf(
        Screens.Car to car_icon,
        Screens.Drivetrain to transmission_icon,
        Screens.Tires to tire_icon,
        Screens.Aerodynamics to wind_icon,
        Screens.Results to graph_icon
    )

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (screen, icon) ->
                IconButton(
                    onClick = { navController.navigate(screen.screen) },
                    modifier = Modifier.background(
                        Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    )
                ) {
                    when (icon) {
                        is ImageVector -> Icon(
                            icon,
                            contentDescription = screen.screen,
                            modifier = Modifier.size(26.dp)
                        )
                        is Int -> Icon(
                            painter = painterResource(id = icon),
                            contentDescription = screen.screen,
                            modifier = Modifier.size(26.dp)
                        )
                        else -> throw IllegalArgumentException("Unsupported icon type")
                    }
                }
            }
        }
    }
}
