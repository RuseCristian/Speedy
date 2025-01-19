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
fun CarScreen(sharedViewModel: SharedViewModel) {

    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp) // Apply consistent padding
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .fillMaxWidth(0.8f)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabTitle(title = "Car Details")

        // Text fields
        CustomOutlinedTextField(
            value = sharedViewModel.carName,
            onValueChange = { sharedViewModel.carName = it },
            label = "Car Name",
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.carMass,
            onValueChange = { sharedViewModel.carMass = it },
            label = "Car Mass",
            metricSuffix = "kg",
            imperialSuffix = "lbs",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.carCeneterMassDistribution,
            onValueChange = { sharedViewModel.carCeneterMassDistribution = it },
            label = "Center Mass Distribution",
            metricSuffix = "%",
            imperialSuffix = "%",
            supportingText = "% of mass over the front axle",
            inputType = InputType.DecimalRange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Transfer Calculations Section
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
                    text = "Weight Transfer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Switch(
                    checked = sharedViewModel.carWeightTransferSwitch,
                    onCheckedChange = { sharedViewModel.carWeightTransferSwitch = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                    )
                )
            }

            if (sharedViewModel.carWeightTransferSwitch) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enabling this option includes weight transfer in calculations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "More info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 8.dp) // Adjust padding for alignment
                            .size(26.dp) // Match size with text field icons
                            .clickable {
                                // Handle info button click
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conditionally show additional fields
        if (sharedViewModel.carWeightTransferSwitch) {
            CustomOutlinedTextField(
                value = sharedViewModel.carCenterofMassHeight,
                onValueChange = { sharedViewModel.carCenterofMassHeight = it },
                label = "Center of Mass Height",
                metricSuffix = "m",
                imperialSuffix = "feet",
                inputType = InputType.Decimal,
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = sharedViewModel.carWheelBase,
                onValueChange = { sharedViewModel.carWheelBase = it },
                label = "Wheel Base",
                metricSuffix = "m",
                imperialSuffix = "feet",
                inputType = InputType.Decimal,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

    }


}
