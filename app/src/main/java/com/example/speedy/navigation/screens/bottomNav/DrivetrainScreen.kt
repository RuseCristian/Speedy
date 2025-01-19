package com.example.speedy.navigation.screens.bottomNav


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Alignment
import com.example.speedy.ui.utils.CustomDropdownMenu
import com.example.speedy.ui.utils.CustomOutlinedTextField
import com.example.speedy.ui.utils.GearRatioSection
import com.example.speedy.ui.utils.InputType
import com.example.speedy.ui.utils.RPMTorqueSection
import com.example.speedy.ui.utils.SharedViewModel
import com.example.speedy.ui.utils.TabTitle

@Composable
fun DrivetrainScreen(sharedViewModel: SharedViewModel) {
    // States for the text fields
    var currentPage by remember { mutableStateOf(0) }
    val dropdownOptions = listOf("Rear Wheel Drive", "Front Wheel Drive", "All Wheel Drive")
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp) // Apply consistent padding
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TabTitle(title = "Drivetrain Details")

        Spacer(modifier = Modifier.height(16.dp))

        RPMTorqueSection(
            rpmTorqueList = sharedViewModel.rpmTorqueList,
            sliderValue = sharedViewModel.drivetrainRPMTorqueEntriesCount,
            onSliderValueChange = { newCount ->
                sharedViewModel.drivetrainRPMTorqueEntriesCount = newCount.toInt()
                val currentSize = sharedViewModel.rpmTorqueList.size

                when {
                    newCount > currentSize -> {
                        // Add empty entries to match the new count
                        repeat((newCount - currentSize).toInt()) {
                            sharedViewModel.rpmTorqueList.add("" to "") // Add empty pairs
                        }
                    }
                    newCount < currentSize -> {
                        // Remove excess entries
                        repeat((currentSize - newCount).toInt()) {
                            sharedViewModel.rpmTorqueList.removeAt(sharedViewModel.rpmTorqueList.size - 1)
                        }
                    }
                }
            }, // Update slider value
            currentPage = currentPage,
            onPageChange = { currentPage = it } // Update current page
        )


        Spacer(modifier = Modifier.height(32.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.drivetrainOffClutchRPM,
            onValueChange = { sharedViewModel.drivetrainOffClutchRPM = it },
            label = "Off Clutch RPM",
            inputType = InputType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.drivetrainGasStartingLevel,
            onValueChange = { sharedViewModel.drivetrainGasStartingLevel = it },
            label = "Gas Starting level (%)",
            imperialSuffix = "%",
            metricSuffix =  "%",
            inputType = InputType.DecimalRange
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.drivetrainshiftTime,
            onValueChange = { sharedViewModel.drivetrainshiftTime = it },
            label = "Shift time",
            metricSuffix = "s",
            imperialSuffix = "s",
            inputType = InputType.Decimal
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Match the width of text fields
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Drivetrain Layout",
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Gear Ratios Info",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { /* Add action here if needed */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            options = dropdownOptions,
            selectedOption = sharedViewModel.drivetrainDrivetrainLayout, // Bind to ViewModel
            onOptionSelected = { sharedViewModel.drivetrainDrivetrainLayout = it },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Drivetrain Loss Field
        CustomOutlinedTextField(
            value = sharedViewModel.drivetrainDrivetrainLoss,
            onValueChange = { sharedViewModel.drivetrainDrivetrainLoss = it },
            label = "Drivetrain Loss (%)",
            metricSuffix = "%",
            imperialSuffix = "%",
            inputType = InputType.DecimalRange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Final Drive Ratio Field
        CustomOutlinedTextField(
            value = sharedViewModel.drivetrainFinalDrive,
            onValueChange = { sharedViewModel.drivetrainFinalDrive = it },
            label = "Final Drive Ratio",
            inputType = InputType.Decimal
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gear Ratios Section
        GearRatioSection(
            gearRatiosList = sharedViewModel.gearRatiosList,
            sliderValue = sharedViewModel.drivetrainGearRatioCount,
            onSliderValueChange = { newCount ->
                sharedViewModel.drivetrainGearRatioCount = newCount.toInt()
                val currentSize = sharedViewModel.gearRatiosList.size

                when {
                    newCount > currentSize -> {
                        // Add empty entries to match the new count
                        repeat((newCount - currentSize).toInt()) {
                            sharedViewModel.gearRatiosList.add("") // Add empty strings
                        }
                    }
                    newCount < currentSize -> {
                        // Remove excess entries
                        repeat((currentSize - newCount).toInt()) {
                            sharedViewModel.gearRatiosList.removeAt(sharedViewModel.gearRatiosList.size - 1)
                        }
                    }
                }
            }

        )
    }
}

