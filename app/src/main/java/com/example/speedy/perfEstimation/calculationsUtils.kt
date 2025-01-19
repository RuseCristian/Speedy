package com.example.speedy.perfEstimation
import android.util.Log
import com.example.speedy.ui.utils.SharedViewModel
import kotlin.math.max

fun linearInterpolation(inputArray: List<Float>, totalPoints: Int): List<Float> {
    if (inputArray.size < 2 || totalPoints < 2) {
        throw IllegalArgumentException("Input array must have at least two points, and totalPoints must be at least 2.")
    }

    val interpolatedArray = mutableListOf<Float>()
    val n = inputArray.size

    val step = (n - 1).toFloat() / (totalPoints - 1)

    for (i in 0 until totalPoints) {
        val position = i * step
        val lowerIndex = position.toInt()
        val upperIndex = (lowerIndex + 1).coerceAtMost(n - 1)
        val t = position - lowerIndex

        val interpolatedValue = if (upperIndex < n) {
            inputArray[lowerIndex] + t * (inputArray[upperIndex] - inputArray[lowerIndex])
        } else {
            inputArray[lowerIndex]
        }
        interpolatedArray.add(interpolatedValue)
    }

    return interpolatedArray
}

fun searchSorted(array: List<Float>, value: Float): Int {
    var low = 0
    var high = array.size

    while (low < high) {
        val mid = (low + high) / 2
        if (array[mid] < value) {
            low = mid + 1
        } else {
            high = mid
        }
    }
    return low
}

fun findAccelFromSpeed(speed: Float, speedValues: List<Float>, accelValues: List<Float>): Float {
    val rpmIndex = searchSorted(speedValues, speed)
    return if (rpmIndex != -1) accelValues[rpmIndex] else 0f
}

fun findOptimumUpshiftPoint(gearsData: List<GearData>): List<Int> {
    val optimumShiftPoints = mutableListOf<Int>()

    // Iterate through each gear except the last one
    for (i in 0 until gearsData.size - 1) {
        val currentGearAcceleration = gearsData[i].torqueWheelCurveForce
        val nextGearAcceleration = gearsData[i + 1].torqueWheelCurveForce
        val currentGearSpeedCurve = gearsData[i].speedCurve
        val nextGearSpeedCurve = gearsData[i + 1].speedCurve

        val maxSize = max(currentGearAcceleration.size, nextGearAcceleration.size)

        // Iterate in reverse through the acceleration and speed curves
        for (j in maxSize - 1 downTo 0) {
            val currentExists = j < currentGearAcceleration.size
            val nextExists = j < nextGearAcceleration.size

            // If next gear's acceleration doesn't exist, the current RPM is the upshift point
            if (!nextExists) {
                optimumShiftPoints.add(j)
                break
            }

            // If both curves exist, compare acceleration values
            if (currentExists && currentGearAcceleration[j] >= findAccelFromSpeed(
                    currentGearSpeedCurve[j],
                    nextGearSpeedCurve,
                    nextGearAcceleration
                )
            ) {
                optimumShiftPoints.add(j)
                break
            }
        }
    }

    return optimumShiftPoints
}

data class GearData(
    val speedCurve: MutableList<Float> = mutableListOf(),
    val airResistanceCurve: MutableList<Float> = mutableListOf(),
    val downforceCurve: MutableList<Float> = mutableListOf(),
    val torqueWheelCurveNm: MutableList<Float> = mutableListOf(),
    val torqueWheelCurveForce: MutableList<Float> = mutableListOf(),
    var torqueEngineCurveNm: MutableList<Float> = mutableListOf(),
    val horsepowerCurve: MutableList<Float> = mutableListOf(),
    val accelerationCurve: MutableList<Float> = mutableListOf(),
    var dropdownRpm: Int? = null, // RPM at which the engine drops after upshifting
    var topSpeed: Double? = null, // Maximum speed achievable in this gear
    var upshiftRpm: Int? = null, // RPM at which the gear is upshifted
    var idleRpm: Int? = null,
    var rpmCurve: MutableList<Int> = mutableListOf()
)

fun getGearData(sharedViewModel: SharedViewModel): List<GearData> {
    // Get converted parameters as CarParameters
    val parameters = sharedViewModel.getConvertedParameters()

    // Initialize gear data list
    val gearDataList = mutableListOf<GearData>()

    // Constants
    val g = 9.81f // Gravitational acceleration in m/s²
    val airDensity = parameters.aeroAirDensity ?: 1.225f // Default air density in kg/m³

    // Tire calculations
    val tireDiameter = ((parameters.tireWidth!! * parameters.tireAspectRatio!! * 2) / 25.4) + parameters.tireWheelDiameter!!
    val tireRadius = String.format("%.3f", tireDiameter / 39.37 / 2).toFloat()

    // Rolling resistance (calculated once)
    val rollingResistance = parameters.tireRollingCoeff!! * g * parameters.carMass!!

    // RPM and torque adjustments
    val rpms = parameters.rpmTorqueList.map { it.first!! }
    // Create RPM range array (exclusive of the upper bound)
    val rpmRange = (rpms.first() until rpms.last() + 1).toList()

    // Interpolate torque values to match the size of the RPM range
    val interpolatedTorques = linearInterpolation(parameters.rpmTorqueList.map { it.second!! }, rpmRange.size)

    // Iterate over each gear ratio
    for (gearRatio in parameters.gearRatiosList) {
        val gearData = GearData()

        // Iterate over interpolated RPM values
        var previousAcceleration = 0f
        var lastSpeed: Double? = null // Track the last speed for calculating top speed
        for (j in rpmRange.indices) {
            val rpmValue = rpmRange[j].toFloat()
            var torqueValue = interpolatedTorques[j] * parameters.drivetrainFinalDrive!! * gearRatio!!

            // Speed calculation
            val speedValue = (rpmValue * tireDiameter.toFloat() / (gearRatio!! * parameters.drivetrainFinalDrive!!.toFloat() * 336f)) * 1.609f / 3.6f

            // Air resistance calculation
            val airResValue = (parameters.aeroDragCoeff!! * parameters.aeroFrontalArea!! * airDensity * speedValue * speedValue) / 2f

            // Downforce calculation (speed-dependent)
            val downforceValue = if (parameters.aeroDownforceSwitch == true) {
                0.5f * airDensity * parameters.aeroNegativeLiftCoeff!! * parameters.aeroDownforceTotalArea!! * speedValue * speedValue
            } else {
                0.0f
            }

            // Weight transfer calculation (only if enabled)
            val weightTransfer = if (parameters.carWeightTransferSwitch == true) {
                previousAcceleration * parameters.carCenterOfMassHeight!! / parameters.carWheelBase!! * parameters.carMass!!
            } else {
                0f
            }

            // Front and rear downforce distribution
            val frontDownforce = downforceValue * (parameters.aeroDownforceDistribution ?: 0.5f)
            val rearDownforce = downforceValue * (1.0f - (parameters.aeroDownforceDistribution ?: 0.5f))

            // Maximum tractive force for current speed with weight transfer
            val maxTractiveForce = when (parameters.drivetrainLayout) {
                "Rear Wheel Drive" -> ((parameters.carMass!! * g * (1.0f - parameters.carCenterMassDistribution!!)) +
                        rearDownforce + weightTransfer) * parameters.tireFrictionCoeff!!
                "Front Wheel Drive" -> ((parameters.carMass!! * g * parameters.carCenterMassDistribution!!) +
                        frontDownforce - weightTransfer) * parameters.tireFrictionCoeff!!
                "All Wheel Drive" -> ((parameters.carMass!! * g) + downforceValue) * parameters.tireFrictionCoeff!!
                else -> 0.0f
            }

            // Torque at wheel considering opposing forces
            var torqueWheel = torqueValue / tireRadius - airResValue - rollingResistance

            if (torqueWheel > maxTractiveForce) {
                torqueWheel = maxTractiveForce
            }

            // Acceleration force calculation
            val acceleration = torqueWheel / parameters.carMass!!

            // Skip further calculations if acceleration is non-positive
            if (acceleration <= 0) {
                break
            }

            // Add data only if acceleration >= 0

            gearData.speedCurve.add(speedValue)
            gearData.airResistanceCurve.add(airResValue)
            gearData.downforceCurve.add(downforceValue)
            gearData.torqueWheelCurveForce.add(torqueWheel)
            gearData.torqueWheelCurveNm.add(torqueWheel * tireRadius)
            gearData.horsepowerCurve.add((interpolatedTorques[j] * rpmValue / 7127f))
            gearData.accelerationCurve.add(acceleration)

            // Update last speed for top speed calculation
            lastSpeed = speedValue.toDouble()
            previousAcceleration = acceleration.toFloat()
        }
        gearData.torqueEngineCurveNm = interpolatedTorques.toMutableList()
        gearData.rpmCurve = rpmRange.toMutableList()
        gearData.idleRpm = parameters.rpmTorqueList.first().first
        // Set top speed for the gear
        gearData.topSpeed = lastSpeed

        // Add gear data to list
        gearDataList.add(gearData)
    }


    // Find optimum upshift points
    val optimumUpshiftValues = findOptimumUpshiftPoint(gearDataList)

    // Add optimum upshift points and dropdown RPM values
    for (i in 0 until optimumUpshiftValues.size) {
        gearDataList[i].upshiftRpm = optimumUpshiftValues[i]
        gearDataList[i].topSpeed?.let { searchSorted(gearDataList[i + 1].speedCurve, it.toFloat()) }
            ?.let { gearDataList[i].dropdownRpm = rpmRange[it] - rpmRange[0] } // minus idle rpm

        Log.e("dropdown", gearDataList[i].dropdownRpm.toString())
    }
    Log.e("upshift", optimumUpshiftValues.toString())
    // Return the list of GearData
    return gearDataList
}

fun estimateAcceleration(sharedViewModel: SharedViewModel): Float {
    val parameters = sharedViewModel.getConvertedParameters()
    val shiftingTime = parameters.drivetrainShiftTime ?: 0f
    val offClutchRpm = parameters.drivetrainOffClutchRPM ?: 0
    val idleRpm = parameters.rpmTorqueList.firstOrNull()?.first ?: 0
    var gasStartLevel = parameters.drivetrainGasStartingLevel ?: 0.0f
    var currentSpeed = parameters.initialSpeed?:0.0f
    var totalTime = 0.0f
    val g = 9.81f
    // Tire calculations
    val tireDiameter = ((parameters.tireWidth!! * parameters.tireAspectRatio!! * 2) / 25.4) + parameters.tireWheelDiameter!!
    val tireRadius = String.format("%.3f", tireDiameter / 39.37 / 2).toFloat()

    // Finding start parameters
    var currentGear = -1
    var currentRpm = -1
    val gearDataList = getGearData(sharedViewModel)

    // Check if initial speed is valid
    for (i in gearDataList.indices) {
        val idx = searchSorted(gearDataList[i].speedCurve, currentSpeed)
        if (idx < gearDataList[i].speedCurve.size) {
            currentGear = i
            currentRpm = idx
            break
        }
    }

    if (currentGear == -1 && currentRpm == -1) {
        println("Error: Initial speed is too high. The top speed of the car is lower than the initial speed inputted.")
        return (-1).toFloat()
    }

    if (gearDataList.last().topSpeed!! < parameters.finalSpeed!!) {
        println("Error: Final Speed is higher than top speed.")
        return (-1).toFloat()
    }

    // Main simulation loop
    while (currentSpeed < (parameters.finalSpeed ?: 0.0f)) {
        // Shifting up
        if (currentRpm == gearDataList[currentGear].upshiftRpm) {
            totalTime += shiftingTime
            currentRpm = gearDataList[currentGear].dropdownRpm!!
            currentGear += 1

            var decelerationTime = 0f
            while (decelerationTime < shiftingTime){
                val airResistance = gearDataList[currentGear].airResistanceCurve[currentRpm]
                val rollingResistance = parameters.tireRollingCoeff!! * g * parameters.carMass!!

                val totalDecelerationForce = airResistance + rollingResistance

                val deceleration = totalDecelerationForce / parameters.carMass!!

                decelerationTime += (gearDataList[currentGear].speedCurve[currentRpm] - gearDataList[currentGear].speedCurve[currentRpm - 1]) / deceleration
                currentSpeed = gearDataList[currentGear].speedCurve[currentRpm]
                currentRpm -= 1
            }
        }

        // standing from standstill
        if (currentRpm <= offClutchRpm - idleRpm) {
            val frictionForce = parameters.tireRollingCoeff!! * g * parameters.carMass!!
            val pushingForce = gearDataList[currentGear].torqueWheelCurveForce[currentRpm]
            var clutchDepression =
                1 - (frictionForce / (pushingForce * parameters.drivetrainGasStartingLevel!!))
            if (clutchDepression >= 1) {
                Log.e("not enough torque", "can not start moving the car")
                return (-1).toFloat()
            }
            val numberOfSteps = offClutchRpm - currentRpm - idleRpm
            val gasStep = (1 - parameters.drivetrainGasStartingLevel!!) / numberOfSteps
            val clutchStep = clutchDepression / numberOfSteps

            while (currentRpm <= offClutchRpm - idleRpm) {
                totalTime += ((gearDataList[currentGear].speedCurve[currentRpm + 1] - gearDataList[currentGear].speedCurve[currentRpm])  / (gearDataList[currentGear].accelerationCurve[currentRpm] * gasStartLevel * (1 - clutchDepression)))
                currentSpeed = gearDataList[currentGear].speedCurve[currentRpm]
                currentRpm += 1
                gasStartLevel += gasStep
                clutchDepression -= clutchStep
            }
        }

        // Accelerating
        totalTime += (gearDataList[currentGear].speedCurve[currentRpm+ 1] - gearDataList[currentGear].speedCurve[currentRpm]) / gearDataList[currentGear].accelerationCurve[currentRpm]
        currentSpeed = gearDataList[currentGear].speedCurve[currentRpm]
        currentRpm += 1
    }

    return totalTime
    Log.e("result","Total time to reach final speed: $totalTime seconds")
}
