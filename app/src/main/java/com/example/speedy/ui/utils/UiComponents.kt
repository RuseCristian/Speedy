package com.example.speedy.ui.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.speedy.firestore.FirestoreUtils.getUserPreference


@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    supportingText: String? = null, // Supporting text
    showError: Boolean = false,
    errorMessage: String = "Invalid input",
    metricSuffix: String? = null, // Metric suffix
    imperialSuffix: String? = null, // Imperial suffix
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    inputType: InputType = InputType.String, // Input type for filtering
    decimalPrecision: Int = 2 // Number of allowed decimal places
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) } // Track focus state
    var isMetric by remember { mutableStateOf(true) } // Default value until preferences are fetched

    LaunchedEffect(Unit) {
        getUserPreference("isMetric") { result, _ ->
            isMetric = result as? Boolean ?: true // Default to true if preference not found or type mismatch
        }
    }


    // Select the appropriate suffix based on the measurement system
    val suffix = if (isMetric) metricSuffix else imperialSuffix

    // Filter the input based on the input type
    val filteredOnValueChange: (String) -> Unit = { input ->
        when (inputType) {
            InputType.String -> onValueChange(input) // Accept any input
            InputType.Decimal -> {
                if (input.isBlank() || input.matches(Regex("^[0-9]*\\.?[0-9]{0,$decimalPrecision}\$"))) {
                    onValueChange(input)
                }
            }
            InputType.DecimalRange -> {
                if (input.isBlank() || (input.matches(Regex("^[0-9]*\\.?[0-9]{0,$decimalPrecision}\$")) && input.toDoubleOrNull()?.let { it in 0.0..100.0 } == true)) {
                    onValueChange(input)
                }
            }
            InputType.Number -> {
                if (input.isBlank() || input.matches(Regex("^[0-9]*\$"))) {
                    onValueChange(input)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() } // Clear focus when clicking outside
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Center the OutlinedTextField
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f), // Centered and limited width
                horizontalAlignment = Alignment.Start // Align supporting text to the start of this Column
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = filteredOnValueChange, // Use filtered input
                    label = { Text(label) },
                    placeholder = { Text(placeholder) },
                    maxLines = 1,
                    isError = showError,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { }
                                .size(26.dp) // Match size with text field icons
                        )
                    },
                    suffix = {
                        if (suffix != null) Text(suffix) // Display the suffix dynamically
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = when (inputType) {
                            InputType.Decimal, InputType.DecimalRange -> KeyboardType.Decimal
                            InputType.Number -> KeyboardType.Number
                            else -> KeyboardType.Text
                        },
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onImeAction?.invoke() ?: focusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused } // Track focus state
                )

                // Supporting text
                if (isFocused && supportingText != null) {
                    Text(
                        text = supportingText,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp) // Add space between the text field and supporting text
                    )
                }

                // Error message
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp) // Add space between the text field and error text
                    )
                }
            }
        }
    }
}


// Enum to define the input types
enum class InputType {
    String,          // Any string input
    Decimal,         // Positive decimals
    DecimalRange,    // Decimal range 0-100
    Number           // Positive integers
}


@Composable
fun TabTitle(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Add padding around the title
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 28.sp, // Slightly larger font for emphasis
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary, // Use the primary color from the theme
            overflow = TextOverflow.Ellipsis, // Handle overflow with ellipsis
            maxLines = 1, // Keep it a single line
            modifier = Modifier
                .padding(bottom = 8.dp) // Space between the title and the decorative line
        )
        // Add a subtle decorative line below the title
        Box(
            modifier = Modifier
                .width(120.dp) // Decorative line width
                .height(4.dp) // Decorative line height
                .clip(MaterialTheme.shapes.medium) // Rounded corners for the line
                .background(MaterialTheme.colorScheme.primary) // Line color matches theme
        )
        Spacer(modifier = Modifier.height(24.dp)) // Add space below the title and content
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemedCustomSlider(
    value: Int,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    step: Int = 1,
) {
    val steps = ((valueRange.endInclusive - valueRange.start) / step).toInt() - 1

    Column(modifier = modifier) {
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(16.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toInt().toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        )
    }
}

@Composable
fun GearRatioTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    inputType: InputType = InputType.Decimal,
    decimalPrecision: Int = 2,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current // Manage focus

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f), // Ensure same width as other fields
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { input ->
                    if (inputType == InputType.Decimal &&
                        (input.isBlank() || input.matches(Regex("^[0-9]*\\.?[0-9]{0,$decimalPrecision}\$")))
                    ) {
                        onValueChange(input)
                    }
                },
                label = { Text(label) },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.clearFocus() } // Clear focus when pressing "Next"
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable(interactionSource = remember { MutableInteractionSource() })
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            focusManager.clearFocus() // Ensure focus is cleared when unfocused
                        }
                    }
            )
        }
    }
}




@Composable
fun RPMTorqueInput(
    rpmValue: String,
    onRPMValueChange: (String) -> Unit,
    torqueValue: String,
    onTorqueValueChange: (String) -> Unit,
    index: Int,
    isMetric: Boolean = true, // Toggle between metric and imperial
    onTorqueImeAction: (() -> Unit)? = null, // Callback for torque ImeAction Done
    modifier: Modifier = Modifier
) {


    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center the fields
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f), // Match the width of CustomOutlinedTextField
            horizontalAlignment = Alignment.Start // Align content to the start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between fields
            ) {
                OutlinedTextField(
                    value = rpmValue,
                    onValueChange = { input ->
                        val filteredInput = input.filter { it.isDigit() } // Allow only digits
                        if (filteredInput.isNotEmpty() && filteredInput.toIntOrNull() != null) {
                            onRPMValueChange(filteredInput)
                        } else if (filteredInput.isEmpty()) {
                            onRPMValueChange("") // Allow clearing the field
                        }
                    },
                    label = { Text("RPM $index") },
                    placeholder = { Text("0") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                    ),
                    modifier = Modifier
                        .weight(1f) // Equal weight for alignment
                )


                OutlinedTextField(
                    value = torqueValue,
                    onValueChange = { input ->
                        val filteredInput = input.filter { it.isDigit() || it == '.' } // Allow digits and a decimal point
                        val decimalRegex = Regex("^[0-9]*\\.?[0-9]{0,3}\$") // Matches positive decimals with up to 3 decimals

                        if (filteredInput.matches(decimalRegex)) {
                            onTorqueValueChange(filteredInput)
                        } else if (filteredInput.isEmpty()) {
                            onTorqueValueChange("") // Allow clearing the field
                        }
                    },
                    label = { Text("Torque $index") },
                    placeholder = { Text("0") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() } // Clear focus when Enter is pressed
                    ),
                    suffix = {
                        Text(
                            if (isMetric) "Nm" else "lb-ft",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.weight(1f) // Equal weight for alignment
                )


            }
        }
    }
}


@Composable
fun GearRatioSection(
    gearRatiosList: SnapshotStateList<String>,
    sliderValue: Int,
    onSliderValueChange: (Float) -> Unit
) {
    val maxGears = sliderValue.toInt()

    // Dynamically update the gear ratios list
    LaunchedEffect(sliderValue) {
        if (gearRatiosList.size < maxGears) {
            gearRatiosList.addAll(List(maxGears - gearRatiosList.size) { "" })
        } else if (gearRatiosList.size > maxGears) {
            gearRatiosList.removeRange(maxGears, gearRatiosList.size)
        }
    }


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
                text = "Gear Ratios",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Slider for Gear Ratios
        ThemedCustomSlider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = 2f..7f,
            step = 1,
            modifier = Modifier.fillMaxWidth(0.8f) // Center slider by reducing its width
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gear Ratio Input Fields
        gearRatiosList.forEachIndexed { index, value ->
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                GearRatioTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (index < gearRatiosList.size) {
                            gearRatiosList[index] = newValue
                        }
                    },
                    label = "Gear ${index + 1}",
                    modifier = Modifier.fillMaxWidth() // Text field spans the full width of its parent Box
                )
            }
            }
        }
    }





@Composable
fun RPMTorqueSection(
    rpmTorqueList: SnapshotStateList<Pair<String, String>>,
    sliderValue: Int,
    onSliderValueChange: (Float) -> Unit,
    currentPage: Int,
    onPageChange: (Int) -> Unit
) {
    var isMetric by remember { mutableStateOf(true) } // Default value until preferences are fetched

    LaunchedEffect(Unit) {
        getUserPreference("isMetric") { result, _ ->
            isMetric = result as? Boolean ?: true // Default to true if preference not found or type mismatch
        }
    }

    val itemsPerPage = 5
    val maxEntries = sliderValue.toInt()
    val totalPages = (maxEntries + itemsPerPage - 1) / itemsPerPage

    // Update list dynamically
    LaunchedEffect(sliderValue) {
        if (rpmTorqueList.size < maxEntries) {
            rpmTorqueList.addAll(List(maxEntries - rpmTorqueList.size) { Pair("", "") })
        } else if (rpmTorqueList.size > maxEntries) {
            rpmTorqueList.removeRange(maxEntries, rpmTorqueList.size)
        }
        if (currentPage >= totalPages) {
            onPageChange(totalPages - 1) // Adjust page if slider reduces total pages
        }
    }

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
                text = "Engine RPM & Torque entries",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Engine RPM & Torque entries Info",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { /* Add action here if needed */ }
            )
        }
    }

        // Slider for RPM/Torque
        ThemedCustomSlider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = 5f..25f,
            step = 1,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Paginated RPMTorqueInput
        val startIndex = currentPage * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, rpmTorqueList.size)

        for (index in startIndex until endIndex) {
            RPMTorqueInput(
                rpmValue = rpmTorqueList[index].first,
                onRPMValueChange = { newValue ->
                    rpmTorqueList[index] = rpmTorqueList[index].copy(first = newValue)
                },
                torqueValue = rpmTorqueList[index].second,
                onTorqueValueChange = { newValue ->
                    rpmTorqueList[index] = rpmTorqueList[index].copy(second = newValue)
                },
                isMetric = isMetric,
                index = index + 1
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pagination Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentPage > 0) onPageChange(currentPage - 1) },
                enabled = currentPage > 0,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Previous")
            }

            Text(
                text = "Page ${currentPage + 1} of $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Button(
                onClick = { if (currentPage < totalPages - 1) onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages - 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Next")
            }
        }
    }



@Composable
fun CustomDropdownMenu(
    options: List<String>,
    selectedOption: String, // Externally controlled selected option
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select an option"
) {
    var expanded by remember { mutableStateOf(false) }
    var dropdownWidth by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        // Button to toggle dropdown
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    dropdownWidth = coordinates.size.width // Capture button width
                }
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small // Subtle rounded corners
        ) {
            Text(
                text = selectedOption.ifEmpty { placeholder }, // Display external selectedOption
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { dropdownWidth.toDp() }) // Match parent width
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onOptionSelected(option) // Notify parent of selection
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExpandableFancyCard(
    lines: List<String>,
    modifier: Modifier = Modifier
) {
    // State to track whether the card is expanded
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { isExpanded = !isExpanded } // Toggle expansion on click
            .animateContentSize() // Smoothly animate size changes
            .padding(16.dp) // Internal padding
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between lines
        ) {
            lines.forEachIndexed { index, line ->
                // Show the title (first line) always and conditionally show other lines
                if (index == 0 || isExpanded) {
                    Text(
                        text = line,
                        style = if (index == 0) {
                            // Style for the first line (e.g., title or key spec)
                            MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            // Style for subsequent lines (e.g., values or descriptions)
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    }
}
