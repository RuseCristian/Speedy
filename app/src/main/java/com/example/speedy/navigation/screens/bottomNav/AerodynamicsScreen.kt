package com.example.speedy.navigation.screens.bottomNav


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import com.example.speedy.ui.utils.CustomOutlinedTextField
import com.example.speedy.ui.utils.InputType
import com.example.speedy.ui.utils.SharedViewModel
import com.example.speedy.ui.utils.TabTitle


@Composable
fun AerodynamicsScreen(sharedViewModel: SharedViewModel) {
    // Measurement unit state

    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp) // Apply consistent padding
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabTitle(title = "Aerodynamics Details")

        // Text fields
        CustomOutlinedTextField(
            value = sharedViewModel.aeroDragCoeff,
            onValueChange = { sharedViewModel.aeroDragCoeff = it },
            label = "Coefficient of Drag",
            inputType = InputType.Decimal,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.aeroFrontalArea,
            onValueChange = { sharedViewModel.aeroFrontalArea = it },
            label = "Frontal Area",
            metricSuffix = "m\u00B2",
            imperialSuffix = "ft\u00B2",
            inputType = InputType.Decimal,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.aeroAirDensity,
            onValueChange = { sharedViewModel.aeroAirDensity = it },
            label = "Air Density",
            metricSuffix = "kg/m\u00B3",
            imperialSuffix = "sl/ft\u00B3",
            inputType = InputType.Decimal,
            decimalPrecision = 3,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Downforce Calculations Section
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Match the width of text fields
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Downforce Calculations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Switch(
                    checked = sharedViewModel.aeroDownforceSwitch,
                    onCheckedChange = { sharedViewModel.aeroDownforceSwitch = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                    )
                )
            }

            if (sharedViewModel.aeroDownforceSwitch) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enabling this option includes downforce calculations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "More info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(26.dp)
                            .clickable {}
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conditionally show additional fields
        if (sharedViewModel.aeroDownforceSwitch) {
            CustomOutlinedTextField(
                value = sharedViewModel.aeroNegativeLiftCoeff,
                onValueChange = { sharedViewModel.aeroNegativeLiftCoeff = it },
                label = "Negative Lift Coefficient",
                inputType = InputType.Decimal,
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = sharedViewModel.aeroDownforceTotalArea,
                onValueChange = { sharedViewModel.aeroDownforceTotalArea = it },
                label = "Downforce Total Area",
                metricSuffix = "m\u00B2",
                imperialSuffix = "ft\u00B2",
                inputType = InputType.Decimal,
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = sharedViewModel.aeroDownforceDistribution,
                onValueChange = { sharedViewModel.aeroDownforceDistribution = it },
                label = "Downforce Distribution",
                metricSuffix = "%",
                imperialSuffix = "%",
                supportingText = "% of downforce over the front axle",
                inputType = InputType.DecimalRange,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

    }
}
