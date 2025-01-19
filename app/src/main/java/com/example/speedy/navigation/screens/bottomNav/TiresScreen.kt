package com.example.speedy.navigation.screens.bottomNav


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import com.example.speedy.ui.utils.CustomOutlinedTextField
import com.example.speedy.ui.utils.InputType
import com.example.speedy.ui.utils.SharedViewModel
import com.example.speedy.ui.utils.TabTitle


@Composable
fun TiresScreen(sharedViewModel: SharedViewModel) {
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
        TabTitle(title = "Tires Details")

        CustomOutlinedTextField(
            value = sharedViewModel.tireWidth,
            onValueChange = {
                sharedViewModel.tireWidth = it
            },
            label = "Tire Width",
            metricSuffix = "mm",
            imperialSuffix = "mm",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.tireAspectRatio,
            onValueChange = {
                sharedViewModel.tireAspectRatio = it
            },
            label = "Tire Aspect Ratio",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.tireWheelDiameter,
            onValueChange = {
                sharedViewModel.tireWheelDiameter = it
            },
            label = "Wheel Diameter ",
            metricSuffix = "inch",
            imperialSuffix = "inch",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))


        CustomOutlinedTextField(
            value = sharedViewModel.tireFrictionCoeff,
            onValueChange = {
                sharedViewModel.tireFrictionCoeff = it
            },
            label = "Friction Coefficient",
            inputType = InputType.Decimal,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.tireRollingCoeff,
            onValueChange = {
                sharedViewModel.tireRollingCoeff = it
            },
            label = "Tire Rolling Resistance",
            inputType = InputType.Decimal,
        )


        Spacer(modifier = Modifier.height(32.dp))

    }
}
