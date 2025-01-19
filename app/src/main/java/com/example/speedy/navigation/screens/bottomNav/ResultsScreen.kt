package com.example.speedy.navigation.screens.bottomNav

import android.util.Log
import android.view.ViewGroup
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import com.example.speedy.perfEstimation.GearData
import com.example.speedy.perfEstimation.estimateAcceleration
import com.example.speedy.ui.utils.CustomOutlinedTextField
import com.example.speedy.ui.utils.InputType
import com.example.speedy.ui.utils.SharedViewModel
import com.example.speedy.ui.utils.TabTitle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.speedy.perfEstimation.getGearData
import com.example.speedy.ui.utils.ChartLine
import com.example.speedy.ui.utils.ExampleGraph
import com.example.speedy.ui.utils.ExpandableFancyCard
import com.example.speedy.ui.utils.LineGraph
import com.google.android.play.core.integrity.x
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf






@Composable
fun ResultsScreen(sharedViewModel: SharedViewModel) {
    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    // State to manage graph visibility and data
    var showGraph by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabTitle(title = "Results")

        // Input fields for Initial Speed and Final Speed
        CustomOutlinedTextField(
            value = sharedViewModel.initialSpeed,
            onValueChange = { sharedViewModel.initialSpeed = it },
            label = "Initial Speed",
            metricSuffix = "kmh",
            imperialSuffix = "mph",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomOutlinedTextField(
            value = sharedViewModel.finalSpeed,
            onValueChange = { sharedViewModel.finalSpeed = it },
            label = "Final Speed",
            metricSuffix = "kmh",
            imperialSuffix = "mph",
            inputType = InputType.Number,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Generate Data Button
        var showDialog by remember { mutableStateOf(false) }
        var validationMessage by remember { mutableStateOf("") }
        Button(
            onClick = {
                val validationResult = sharedViewModel.validateData()
                if (!validationResult.isValid) {
                    validationMessage = validationResult.message ?: "Validation failed."
                    showDialog = true
                } else {
                    validationMessage = "Data generated successfully!"
                    val gearDataList = getGearData(sharedViewModel)
                    if (gearDataList.isNotEmpty()) {
                        showGraph = true
                    } else {
                        validationMessage = "No gear data available."
                        showDialog = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Data")
        }


        Spacer(modifier = Modifier.height(16.dp))


        // Display Graph after Data Generation
        if (showGraph) {
            val data = getGearData(sharedViewModel)
            val Uiparameters = sharedViewModel.getConvertedParameters()

            val result_time  = estimateAcceleration(sharedViewModel)
            val maxHp = data[0].horsepowerCurve.max().toInt()
            val maxHpRPMValue = data[0].horsepowerCurve.indexOf (data[0].horsepowerCurve.max()) + data[0].idleRpm!!

            val maxTorqueMetric = data[0].torqueEngineCurveNm.max().toInt()
            val maxTorqueRPMValue = data[0].torqueEngineCurveNm.indexOf(data[0].torqueEngineCurveNm.max()) + data[0].idleRpm!!
            val weightMetric = Uiparameters.carMass

            val numberOfGears = data.size
            val tireSizes = "${Uiparameters.tireWidth}/${(Uiparameters.tireAspectRatio!! * 100).toInt()}/R${Uiparameters.tireWheelDiameter}"

            val topSpeedMetric = data
                .mapNotNull { it.topSpeed } // Extract non-null topSpeed values
                .maxOrNull()                // Find the highest value
                ?.times(3.6)                // Convert to metric (km/h)
                ?.toInt()                   // Convert to Int

            val massDistributionString = "${(Uiparameters.carCenterMassDistribution!! * 100).toInt()}% Front ${(100 - Uiparameters.carCenterMassDistribution!!* 100).toInt()}% Rear"

            Text(text = "${Uiparameters.initialSpeed!!.times(3.6).toInt()}km/h to " +
            "${Uiparameters.finalSpeed!!.times(3.6).toInt()}km/h in " +
                    "${"%.1f".format(result_time)}s",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            ExpandableFancyCard(
                lines = listOf(
                    "Car Specifications",
                    "Name: ${Uiparameters.carName}",
                    "Mass: $weightMetric kg",
                    "Mass Distribution: $massDistributionString",
                    "Tire Sizes: $tireSizes",
                    "Drag Coefficient: ${Uiparameters.aeroDragCoeff}",
                    "Frontal Area: ${Uiparameters.aeroFrontalArea} m\u00B2",
                    "Top Speed: $topSpeedMetric km/h",
                )
            )

            ExpandableFancyCard(
                lines = listOf(
                    "Drivetrain Performance", // Title
                    "Max Horsepower: $maxHp HP @ $maxHpRPMValue RPM",
                    "Max Torque: $maxTorqueMetric Nm @ $maxTorqueRPMValue RPM",
                    "Drive Train Layout: ${Uiparameters.drivetrainLayout}",
                    "Drivetrain Efficiency Loss: ${Uiparameters.drivetrainLoss!!.times(100)}%",
                    "Transmission: $numberOfGears-speed ",
                )
            )
            val upshiftPointsStringMetric = data.take(data.size - 1).mapIndexed { index, gear ->
                val upshiftRpm = gear.idleRpm?.let { gear.upshiftRpm?.plus(it) }
                "Gear ${index + 1} → ${index + 2}: $upshiftRpm RPM"
            }

            val topSpeedsStringMetric = data.mapIndexed { index, gear ->
                val topSpeed = gear.topSpeed?.times(3.6)?.toInt() // Convert to km/h
                "Gear ${index + 1} Top Speed: ${topSpeed ?: "N/A"} km/h"
            }

            // Combine both sections into one list
            val gearDataString = listOf(
                "Gear Data", // Title
                "Optimum Upshift Points for Acceleration:"
            ) + upshiftPointsStringMetric + listOf("", "Top Speeds:") + topSpeedsStringMetric

            ExpandableFancyCard(lines = gearDataString)


        /*
            Text(
                text = "Torque & Horsepower vs RPM (First Gear)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (data.isNotEmpty()) {
                val rpmCurve = data[0].rpmCurve.map { it.toFloat() } // X data
                val torqueCurve = data[0].torqueEngineCurveNm // Y data
                val simpleLine = ChartLine(
                    xValues = rpmCurve,
                    yValues = torqueCurve,
                    color = Color.Blue
                )

                // Define legend labels corresponding to the chart lines
                val legendLabels = listOf("Torque")

                Box(modifier = Modifier.height(300.dp).padding(16.dp)) {
                    LineGraph(
                        chartLines = listOf(simpleLine),
                        title = "Engine",
                        xAxisLabel = "RPM",
                        yAxisLabel = "Torque (Nm)",
                        showGrid = true,
                        smoothLines = true,
                        maxPoints = 100,
                        legendLabels = legendLabels, // Pass legend labels here
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
            */

        }
    }
}
