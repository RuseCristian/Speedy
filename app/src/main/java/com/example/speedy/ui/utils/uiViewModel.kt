package com.example.speedy.ui.utils

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Represents the result of validation
data class ValidationResult(
    val isValid: Boolean,
    val screen: Screens? = null, // The screen where the error occurred
    val message: String? = null // Detailed error message
)

// Represents different screens/tabs
enum class Screens(val screen: String) {
    Car("car_screen"),
    Drivetrain("drivetrain_screen"),
    Tires("tires_screen"),
    Aerodynamics("aerodynamics_screen"),
    Results("results_screen")
}


class SharedViewModel : ViewModel() {

    // Car Details Tab
    var carName by mutableStateOf("")
    var carMass by mutableStateOf("")
    var carCeneterMassDistribution by mutableStateOf("")
    var carWeightTransferSwitch by mutableStateOf(false) // Default value is false
    var carCenterofMassHeight by mutableStateOf("")
    var carWheelBase by mutableStateOf("") 
        

    // Drivetrain Details Tab
    var drivetrainRPMTorqueEntriesCount by mutableStateOf(5)
    var drivetrainOffClutchRPM by mutableStateOf("")
    var drivetrainGasStartingLevel by mutableStateOf("")
    var drivetrainshiftTime by mutableStateOf("")
    var drivetrainDrivetrainLayout by mutableStateOf("Rear Wheel Drive")
    var drivetrainDrivetrainLoss by mutableStateOf("")
    var drivetrainFinalDrive by mutableStateOf("")
    var drivetrainGearRatioCount by mutableStateOf(3)

    var rpmTorqueList = mutableStateListOf<Pair<String, String>>()
    var gearRatiosList = mutableStateListOf<String>()
        

    // Tires Tab
    var tireWidth by mutableStateOf("")
    var tireAspectRatio by mutableStateOf("")
    var tireWheelDiameter by mutableStateOf("")
    var tireFrictionCoeff by mutableStateOf("")
    var tireRollingCoeff by mutableStateOf("") 
        


    // Aero Tab
    var aeroDragCoeff by mutableStateOf("")
    var aeroFrontalArea by mutableStateOf("")
    var aeroAirDensity by mutableStateOf("")
    var aeroDownforceSwitch by mutableStateOf(false)
    var aeroNegativeLiftCoeff by mutableStateOf("")
    var aeroDownforceTotalArea by mutableStateOf("")
    var aeroDownforceDistribution by mutableStateOf("") 
        
    // Results Screen

    var initialSpeed by mutableStateOf("")
    var finalSpeed by mutableStateOf("")

    fun resetState() {

        // Car Details Tab
        carName = ""
        carMass = ""
        carCeneterMassDistribution = ""
        carWeightTransferSwitch = false
        carCenterofMassHeight = ""
        carWheelBase = ""

        // Drivetrain Details Tab
        drivetrainRPMTorqueEntriesCount = 5
        drivetrainOffClutchRPM = ""
        drivetrainGasStartingLevel = ""
        drivetrainshiftTime = ""
        drivetrainDrivetrainLayout = "Rear Wheel Drive"
        drivetrainDrivetrainLoss = ""
        drivetrainFinalDrive = ""
        drivetrainGearRatioCount = 3
        rpmTorqueList = mutableStateListOf<Pair<String, String>>()
        gearRatiosList = mutableStateListOf<String>()

        // Tires Tab
        tireWidth = ""
        tireAspectRatio = ""
        tireWheelDiameter = ""
        tireFrictionCoeff = ""
        tireRollingCoeff = ""

        // Aero Tab
        aeroDragCoeff = ""
        aeroFrontalArea = ""
        aeroAirDensity = ""
        aeroDownforceSwitch = false
        aeroNegativeLiftCoeff = ""
        aeroDownforceTotalArea = ""
        aeroDownforceDistribution = ""

        // Results Tab
        initialSpeed = ""
        finalSpeed = ""
    }


        // Debugging function
        fun logAllValues(tag: String = "SharedViewModelDebug") {
            Log.d(tag, "=== Car Details Tab ===")
            Log.d(tag, "Car Name: $carName")
            Log.d(tag, "Car Mass: $carMass")
            Log.d(tag, "Center Mass Distribution: $carCeneterMassDistribution")
            Log.d(tag, "Weight Transfer Switch: $carWeightTransferSwitch")
            Log.d(tag, "Center of Mass Height: $carCenterofMassHeight")
            Log.d(tag, "Wheel Base: $carWheelBase")

            Log.d(tag, "=== Drivetrain Details Tab ===")
            Log.d(tag, "RPM Torque Entries Count: $drivetrainRPMTorqueEntriesCount")
            Log.d(tag, "Off Clutch RPM: $drivetrainOffClutchRPM")
            Log.d(tag, "Gas Starting Level: $drivetrainGasStartingLevel")
            Log.d(tag, "Shift Time: $drivetrainshiftTime")
            Log.d(tag, "Drivetrain Layout: $drivetrainDrivetrainLayout")
            Log.d(tag, "Drivetrain Loss: $drivetrainDrivetrainLoss")
            Log.d(tag, "Final Drive: $drivetrainFinalDrive")
            Log.d(tag, "Gear Ratio Count: $drivetrainGearRatioCount")
            Log.d(tag, "Gear Ratios List: ${gearRatiosList.joinToString(", ")}")
            Log.d(tag, "RPM Torque List: ${rpmTorqueList.joinToString(", ") { "(${it.first}, ${it.second})" }}")

            Log.d(tag, "=== Tires Tab ===")
            Log.d(tag, "Tire Width: $tireWidth")
            Log.d(tag, "Tire Aspect Ratio: $tireAspectRatio")
            Log.d(tag, "Tire Wheel Diameter: $tireWheelDiameter")
            Log.d(tag, "Tire Friction Coefficient: $tireFrictionCoeff")
            Log.d(tag, "Tire Rolling Coefficient: $tireRollingCoeff")

            Log.d(tag, "=== Aero Tab ===")
            Log.d(tag, "Drag Coefficient: $aeroDragCoeff")
            Log.d(tag, "Frontal Area: $aeroFrontalArea")
            Log.d(tag, "Air Density: $aeroAirDensity")
            Log.d(tag, "Downforce Switch: $aeroDownforceSwitch")
            Log.d(tag, "Negative Lift Coefficient: $aeroNegativeLiftCoeff")
            Log.d(tag, "Downforce Total Area: $aeroDownforceTotalArea")
            Log.d(tag, "Downforce Distribution: $aeroDownforceDistribution")
        }

    fun validateData(checkspeed: Boolean = true): ValidationResult {
        // Car Details Tab Validation
        if (carName.isEmpty()) {
            return ValidationResult(false, Screens.Car, "Car Name is required. (Car Details screen)")
        }
        if (carMass.isEmpty()) {
            return ValidationResult(false, Screens.Car, "Car Mass is required. (Car Details screen)")
        }
        if (carCeneterMassDistribution.isEmpty()) {
            return ValidationResult(false, Screens.Car, "Center Mass Distribution is required. (Car Details screen)")
        }
        if (carWeightTransferSwitch) {
            if (carCenterofMassHeight.isEmpty()) {
                return ValidationResult(false, Screens.Car, "Center of Mass Height is required when Weight Transfer is enabled. (Car Details screen)")
            }
            if (carWheelBase.isEmpty()) {
                return ValidationResult(false, Screens.Car, "Wheel Base is required when Weight Transfer is enabled. (Car Details screen)")
            }
        }

        // Drivetrain Details Tab Validation
        if (rpmTorqueList.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "RPM-Torque List is required. (Drivetrain Details screen)")
        }
        var lastRpm = -1
        for ((index, pair) in rpmTorqueList.withIndex()) {
            val rpm = pair.first.toIntOrNull()
            if (rpm == null || rpm <= lastRpm) {
                return ValidationResult(
                    false,
                    Screens.Drivetrain,
                    "RPM values must be incremental. Example: RPM1: 2000, RPM2: 2500. Error at index ${index + 1}. (Drivetrain Details screen)"
                )
            }
            lastRpm = rpm
        }
        if (drivetrainOffClutchRPM.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "Off Clutch RPM is required. (Drivetrain Details screen)")
        }
        if (drivetrainGasStartingLevel.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "Gas Starting Level is required. (Drivetrain Details screen)")
        }
        if (drivetrainshiftTime.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "Shift Time is required. (Drivetrain Details screen)")
        }
        if (drivetrainDrivetrainLoss.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "Drivetrain Loss is required. (Drivetrain Details screen)")
        }
        if (drivetrainFinalDrive.isEmpty()) {
            return ValidationResult(false, Screens.Drivetrain, "Final Drive is required. (Drivetrain Details screen)")
        }
        if (gearRatiosList.any { it.isEmpty() }) {
            return ValidationResult(false, Screens.Drivetrain, "All Gear Ratios must have a value. (Drivetrain Details screen)")
        }

        // Tires Tab Validation
        if (tireWidth.isEmpty()) {
            return ValidationResult(false, Screens.Tires, "Tire Width is required. (Tires screen)")
        }
        if (tireAspectRatio.isEmpty()) {
            return ValidationResult(false, Screens.Tires, "Tire Aspect Ratio is required. (Tires screen)")
        }
        if (tireWheelDiameter.isEmpty()) {
            return ValidationResult(false, Screens.Tires, "Tire Wheel Diameter is required. (Tires screen)")
        }
        if (tireFrictionCoeff.isEmpty()) {
            return ValidationResult(false, Screens.Tires, "Tire Friction Coefficient is required. (Tires screen)")
        }
        if (tireRollingCoeff.isEmpty()) {
            return ValidationResult(false, Screens.Tires, "Tire Rolling Coefficient is required. (Tires screen)")
        }

        // Aero Tab Validation
        if (aeroDragCoeff.isEmpty()) {
            return ValidationResult(false, Screens.Aerodynamics, "Aero Drag Coefficient is required. (Aerodynamics screen)")
        }
        if (aeroFrontalArea.isEmpty()) {
            return ValidationResult(false, Screens.Aerodynamics, "Aero Frontal Area is required. (Aerodynamics screen)")
        }
        if (aeroAirDensity.isEmpty()) {
            return ValidationResult(false, Screens.Aerodynamics, "Aero Air Density is required. (Aerodynamics screen)")
        }
        if (aeroDownforceSwitch) {
            if (aeroNegativeLiftCoeff.isEmpty()) {
                return ValidationResult(false, Screens.Aerodynamics, "Negative Lift Coefficient is required when Downforce is enabled. (Aerodynamics screen)")
            }
            if (aeroDownforceTotalArea.isEmpty()) {
                return ValidationResult(false, Screens.Aerodynamics, "Downforce Total Area is required when Downforce is enabled. (Aerodynamics screen)")
            }
            if (aeroDownforceDistribution.isEmpty()) {
                return ValidationResult(false, Screens.Aerodynamics, "Downforce Distribution is required when Downforce is enabled. (Aerodynamics screen)")
            }
        }
        if ( checkspeed) {
            // Additional Validation: Initial Speed vs Final Speed
            val initialSpeedValue = initialSpeed.toIntOrNull()
            val finalSpeedValue = finalSpeed.toIntOrNull()
            if (initialSpeedValue == null || finalSpeedValue == null || finalSpeedValue <= initialSpeedValue) {
                return ValidationResult(
                    false,
                    Screens.Results,
                    "Final Speed must be greater than Initial Speed. (Results screen)"
                )
            }
        }
        // If all checks pass
        return ValidationResult(true)
    }

    fun getConvertedParameters(isMetric: Boolean = true): CarParameters {
        // Conversion factors
        val lbsToKg = 0.453592
        val feetToMeters = 0.3048
        val footPoundsToNm = 1.35582
        val ftSquaredToMSquared = 0.092903
        val slPerFtCubedToKgPerMCubed = 515.378818
        val kmhToMs = 1 / 3.6

        return CarParameters(
            // Car Details
            carName = carName,
            carMass = carMass.toIntOrNull()?.let { mass ->
                if (isMetric) mass else (mass * lbsToKg).toInt()
            },
            carCenterMassDistribution = carCeneterMassDistribution.toFloatOrNull()?.div(100),
            carWeightTransferSwitch = carWeightTransferSwitch,
            carCenterOfMassHeight = carCenterofMassHeight.toDoubleOrNull()?.let { height ->
                if (isMetric) height.toFloat() else (height * feetToMeters).toFloat()
            },
            carWheelBase = carWheelBase.toDoubleOrNull()?.let { base ->
                if (isMetric) base.toFloat() else (base * feetToMeters).toFloat()
            },

            // Drivetrain Details
            drivetrainRPMTorqueEntriesCount = drivetrainRPMTorqueEntriesCount,
            drivetrainOffClutchRPM = drivetrainOffClutchRPM.toIntOrNull(),
            drivetrainGasStartingLevel = drivetrainGasStartingLevel.toFloatOrNull()?.div(100),
            drivetrainShiftTime = drivetrainshiftTime.toFloatOrNull(),
            drivetrainLayout = drivetrainDrivetrainLayout,
            drivetrainLoss = drivetrainDrivetrainLoss.toFloatOrNull()?.div(100),
            drivetrainFinalDrive = drivetrainFinalDrive.toFloatOrNull(),
            drivetrainGearRatioCount = drivetrainGearRatioCount,
            rpmTorqueList = rpmTorqueList.map { pair ->
                Pair(
                    pair.first.toIntOrNull(),
                    pair.second.toDoubleOrNull()?.let { torque ->
                        // Convert drivetrainDrivetrainLoss from string to Float
                        val drivetrainLossValue = drivetrainDrivetrainLoss.toFloatOrNull()?.div(100) ?: 0f
                        // Convert to metric if needed
                        val metricTorque = if (isMetric) torque else torque * footPoundsToNm
                        // Apply drivetrain loss
                        val adjustedTorque = metricTorque * (1 - drivetrainLossValue)
                        adjustedTorque.toFloat()
                    }
                )
            },
            gearRatiosList = gearRatiosList.map { it.toFloatOrNull() },

            // Tires
            tireWidth = tireWidth.toIntOrNull(),
            tireAspectRatio = tireAspectRatio.toFloatOrNull()?.div(100),
            tireWheelDiameter = tireWheelDiameter.toIntOrNull(),
            tireFrictionCoeff = tireFrictionCoeff.toFloatOrNull(),
            tireRollingCoeff = tireRollingCoeff.toFloatOrNull(),

            // Aero Details
            aeroDragCoeff = aeroDragCoeff.toFloatOrNull(),
            aeroFrontalArea = aeroFrontalArea.toDoubleOrNull()?.let { area ->
                if (isMetric) area.toFloat() else (area * ftSquaredToMSquared).toFloat()
            },
            aeroAirDensity = aeroAirDensity.toDoubleOrNull()?.let { density ->
                if (isMetric) density.toFloat() else (density * slPerFtCubedToKgPerMCubed).toFloat()
            },
            aeroDownforceSwitch = aeroDownforceSwitch,
            aeroNegativeLiftCoeff = aeroNegativeLiftCoeff.toFloatOrNull(),
            aeroDownforceTotalArea = aeroDownforceTotalArea.toDoubleOrNull()?.let { area ->
                if (isMetric) area.toFloat() else (area * ftSquaredToMSquared).toFloat()
            },
            aeroDownforceDistribution = aeroDownforceDistribution.toFloatOrNull()?.div(100),

            // Results Screen
            initialSpeed = initialSpeed.toDoubleOrNull()?.let { speed ->
                if (isMetric) (speed * kmhToMs).toFloat() else speed.toFloat()
            },
            finalSpeed = finalSpeed.toDoubleOrNull()?.let { speed ->
                if (isMetric) (speed * kmhToMs).toFloat() else speed.toFloat()
            }
        )
    }

    fun loadPredefinedValues() {
        // Car Details
        carName = "Miata"
        carMass = "1050"
        carCeneterMassDistribution = "50"

        // Drivetrain
        drivetrainOffClutchRPM = "1400"
        drivetrainGasStartingLevel = "70"
        drivetrainDrivetrainLoss = "10"
        drivetrainshiftTime = "0.5"
        drivetrainFinalDrive = "4.3"
        drivetrainGearRatioCount = 5
        gearRatiosList.apply {
            clear()
            addAll(listOf("3.7", "2.2", "1.7", "1.0", "0.96"))
        }
        drivetrainRPMTorqueEntriesCount = 6
        rpmTorqueList.apply {
            clear()
            addAll(
                listOf(
                    Pair("1000", "250"),
                    Pair("2000", "255"),
                    Pair("3000", "280"),
                    Pair("4000", "310"),
                    Pair("5000", "300"),
                    Pair("6000", "270")
                )
            )
        }

        // Tires
        tireWidth = "195"
        tireAspectRatio = "45"
        tireWheelDiameter = "16"
        tireFrictionCoeff = "1.2"
        tireRollingCoeff = "0.01"

        // Aero
        aeroDragCoeff = "0.33"
        aeroFrontalArea = "2.5"
        aeroAirDensity = "1.225"

        // Results Screen
        initialSpeed = "0"
        finalSpeed = "100"
    }



}

data class CarParameters(
    // Car Details Tab
    var carName: String? = null,
    var carMass: Int? = null,
    var carCenterMassDistribution: Float? = null,
    var carWeightTransferSwitch: Boolean = false,
    var carCenterOfMassHeight: Float? = null,
    var carWheelBase: Float? = null,

    // Drivetrain Details Tab
    var drivetrainRPMTorqueEntriesCount: Int? = null,
    var drivetrainOffClutchRPM: Int? = null,
    var drivetrainGasStartingLevel: Float? = null,
    var drivetrainShiftTime: Float? = null,
    var drivetrainLayout: String? = null,
    var drivetrainLoss: Float? = null,
    var drivetrainFinalDrive: Float? = null,
    var drivetrainGearRatioCount: Int? = null,
    var rpmTorqueList: List<Pair<Int?, Float?>> = emptyList(),
    var gearRatiosList: List<Float?> = emptyList(),

    // Tires Tab
    var tireWidth: Int? = null,
    var tireAspectRatio: Float? = null,
    var tireWheelDiameter: Int? = null,
    var tireFrictionCoeff: Float? = null,
    var tireRollingCoeff: Float? = null,

    // Aero Tab
    var aeroDragCoeff: Float? = null,
    var aeroFrontalArea: Float? = null,
    var aeroAirDensity: Float? = null,
    var aeroDownforceSwitch: Boolean = false,
    var aeroNegativeLiftCoeff: Float? = null,
    var aeroDownforceTotalArea: Float? = null,
    var aeroDownforceDistribution: Float? = null,

    // Results Screen
    var initialSpeed: Float? = null,
    var finalSpeed: Float? = null
)

/*
    Log.e("Debug", "Car Details Tab")
    Log.e("Debug", "Car Name: ${parameters.carName}")
    Log.e("Debug", "Car Mass: ${parameters.carMass}")
    Log.e("Debug", "Car Center Mass Distribution: ${parameters.carCenterMassDistribution}")
    Log.e("Debug", "Car Weight Transfer Switch: ${parameters.carWeightTransferSwitch}")
    Log.e("Debug", "Car Center of Mass Height: ${parameters.carCenterOfMassHeight}")
    Log.e("Debug", "Car Wheel Base: ${parameters.carWheelBase}")

    Log.e("Debug", "Drivetrain Details Tab")
    Log.e("Debug", "Drivetrain RPM Torque Entries Count: ${parameters.drivetrainRPMTorqueEntriesCount}")
    Log.e("Debug", "Drivetrain Off Clutch RPM: ${parameters.drivetrainOffClutchRPM}")
    Log.e("Debug", "Drivetrain Gas Starting Level: ${parameters.drivetrainGasStartingLevel}")
    Log.e("Debug", "Drivetrain Shift Time: ${parameters.drivetrainShiftTime}")
    Log.e("Debug", "Drivetrain Layout: ${parameters.drivetrainLayout}")
    Log.e("Debug", "Drivetrain Loss: ${parameters.drivetrainLoss}")
    Log.e("Debug", "Drivetrain Final Drive: ${parameters.drivetrainFinalDrive}")
    Log.e("Debug", "Drivetrain Gear Ratio Count: ${parameters.drivetrainGearRatioCount}")
    Log.e("Debug", "RPM Torque List: ${parameters.rpmTorqueList}")
    Log.e("Debug", "Gear Ratios List: ${parameters.gearRatiosList}")

    Log.e("Debug", "Tires Tab")
    Log.e("Debug", "Tire Width: ${parameters.tireWidth}")
    Log.e("Debug", "Tire Aspect Ratio: ${parameters.tireAspectRatio}")
    Log.e("Debug", "Tire Wheel Diameter: ${parameters.tireWheelDiameter}")
    Log.e("Debug", "Tire Friction Coeff: ${parameters.tireFrictionCoeff}")
    Log.e("Debug", "Tire Rolling Coeff: ${parameters.tireRollingCoeff}")

    Log.e("Debug", "Aero Tab")
    Log.e("Debug", "Aero Drag Coeff: ${parameters.aeroDragCoeff}")
    Log.e("Debug", "Aero Frontal Area: ${parameters.aeroFrontalArea}")
    Log.e("Debug", "Aero Air Density: ${parameters.aeroAirDensity}")
    Log.e("Debug", "Aero Downforce Switch: ${parameters.aeroDownforceSwitch}")
    Log.e("Debug", "Aero Negative Lift Coeff: ${parameters.aeroNegativeLiftCoeff}")
    Log.e("Debug", "Aero Downforce Total Area: ${parameters.aeroDownforceTotalArea}")
    Log.e("Debug", "Aero Downforce Distribution: ${parameters.aeroDownforceDistribution}")

    Log.e("Debug", "Results Screen")
    Log.e("Debug", "Initial Speed: ${parameters.initialSpeed}")
    Log.e("Debug", "Final Speed: ${parameters.finalSpeed}")


 */