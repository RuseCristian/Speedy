package com.example.speedy.firestore



import android.content.Context
import android.widget.Toast
import com.example.speedy.ui.utils.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object DatasetUtils {
    private val _userDatasets = MutableStateFlow<List<String>>(emptyList())
    val userDatasets: StateFlow<List<String>> = _userDatasets

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun renameDataset(
        oldName: String,
        newName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        val userCollection = firestore.collection("users").document(userId).collection("datasets")

        // Rename operation: Copy to new document, delete old document
        userCollection.document(oldName).get()
            .addOnSuccessListener { oldDoc ->
                if (oldDoc.exists()) {
                    val data = oldDoc.data
                    if (data != null) {
                        userCollection.document(newName).set(data)
                            .addOnSuccessListener {
                                userCollection.document(oldName).delete()
                                    .addOnSuccessListener {
                                        val updatedDatasets = _userDatasets.value.toMutableList()
                                        updatedDatasets.remove(oldName)
                                        updatedDatasets.add(newName)
                                        _userDatasets.value = updatedDatasets
                                        onComplete(true, null) // Rename successful
                                    }
                                    .addOnFailureListener { deleteError ->
                                        onComplete(false, "Error deleting old dataset: ${deleteError.localizedMessage}")
                                    }
                            }
                            .addOnFailureListener { setError ->
                                onComplete(false, "Error saving dataset with new name: ${setError.localizedMessage}")
                            }
                    } else {
                        onComplete(false, "Old dataset data is null.")
                    }
                } else {
                    onComplete(false, "Old dataset not found.")
                }
            }
            .addOnFailureListener { getError ->
                onComplete(false, "Error retrieving old dataset: ${getError.localizedMessage}")
            }
    }


    fun deleteDataset(datasetName: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        firestore.collection("users")
            .document(userId)
            .collection("datasets")
            .document(datasetName)
            .delete()
            .addOnSuccessListener {
                val updatedDatasets = _userDatasets.value.toMutableList()
                updatedDatasets.remove(datasetName)
                _userDatasets.value = updatedDatasets
                onComplete(true, null) // Successfully deleted
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onComplete(false, e.localizedMessage) // Error during deletion
            }
    }


    fun saveDataset(
        datasetName: String,
        sharedViewModel: SharedViewModel,
        context: Context,
        onComplete: (Boolean, String?) -> Unit
    ) {
        // Validate data before saving
        val validationResult = sharedViewModel.validateData(false)
        if (!validationResult.isValid) {
            // Show a toast or handle validation errors
            Toast.makeText(context, validationResult.message, Toast.LENGTH_LONG).show()
            onComplete(false, validationResult.message)
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        val datasetsCollection = firestore.collection("users").document(userId).collection("datasets")

        // Collect all fields from SharedViewModel
        val datasetData = mapOf(
            "carName" to sharedViewModel.carName,
            "carMass" to sharedViewModel.carMass,
            "carCeneterMassDistribution" to sharedViewModel.carCeneterMassDistribution,
            "carWeightTransferSwitch" to sharedViewModel.carWeightTransferSwitch,
            "carCenterofMassHeight" to sharedViewModel.carCenterofMassHeight,
            "carWheelBase" to sharedViewModel.carWheelBase,
            "drivetrainRPMTorqueEntriesCount" to sharedViewModel.drivetrainRPMTorqueEntriesCount,
            "drivetrainOffClutchRPM" to sharedViewModel.drivetrainOffClutchRPM,
            "drivetrainGasStartingLevel" to sharedViewModel.drivetrainGasStartingLevel,
            "drivetrainshiftTime" to sharedViewModel.drivetrainshiftTime,
            "drivetrainDrivetrainLayout" to sharedViewModel.drivetrainDrivetrainLayout,
            "drivetrainDrivetrainLoss" to sharedViewModel.drivetrainDrivetrainLoss,
            "drivetrainFinalDrive" to sharedViewModel.drivetrainFinalDrive,
            "drivetrainGearRatioCount" to sharedViewModel.drivetrainGearRatioCount,
            "gearRatiosList" to sharedViewModel.gearRatiosList,
            // Convert rpmTorqueList into a list of maps to avoid nested arrays
            "rpmTorqueList" to sharedViewModel.rpmTorqueList.map { pair ->
                mapOf("rpm" to pair.first, "torque" to pair.second)
            },
            "tireWidth" to sharedViewModel.tireWidth,
            "tireAspectRatio" to sharedViewModel.tireAspectRatio,
            "tireWheelDiameter" to sharedViewModel.tireWheelDiameter,
            "tireFrictionCoeff" to sharedViewModel.tireFrictionCoeff,
            "tireRollingCoeff" to sharedViewModel.tireRollingCoeff,
            "aeroDragCoeff" to sharedViewModel.aeroDragCoeff,
            "aeroFrontalArea" to sharedViewModel.aeroFrontalArea,
            "aeroAirDensity" to sharedViewModel.aeroAirDensity,
            "aeroDownforceSwitch" to sharedViewModel.aeroDownforceSwitch,
            "aeroNegativeLiftCoeff" to sharedViewModel.aeroNegativeLiftCoeff,
            "aeroDownforceTotalArea" to sharedViewModel.aeroDownforceTotalArea,
            "aeroDownforceDistribution" to sharedViewModel.aeroDownforceDistribution,
        )

        // Check if a dataset with the same name already exists
        datasetsCollection.document(datasetName).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(false, "A dataset with this name already exists.")
                } else {
                    // Save the new dataset
                    datasetsCollection.document(datasetName).set(datasetData)
                        .addOnSuccessListener {
                            val updatedDatasets = _userDatasets.value.toMutableList()
                            updatedDatasets.add(datasetName)
                            _userDatasets.value = updatedDatasets
                            onComplete(true, null)
                        }
                        .addOnFailureListener { e ->
                            onComplete(false, e.localizedMessage)
                        }
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }

    fun fetchUserDatasets() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val userId = currentUser.uid
        firestore.collection("users")
            .document(userId)
            .collection("datasets")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val datasets = querySnapshot.documents.map { it.id }
                _userDatasets.value = datasets
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun loadDataset(
        datasetName: String,
        sharedViewModel: SharedViewModel,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        val datasetsCollection = firestore.collection("users").document(userId).collection("datasets")

        datasetsCollection.document(datasetName).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Reset the SharedViewModel state
                    sharedViewModel.resetState()

                    // Load values from Firestore document into the SharedViewModel
                    document.data?.let { data ->
                        sharedViewModel.carName = data["carName"] as? String ?: ""
                        sharedViewModel.carMass = data["carMass"] as? String ?: ""
                        sharedViewModel.carCeneterMassDistribution = data["carCeneterMassDistribution"] as? String ?: ""
                        sharedViewModel.carWeightTransferSwitch = data["carWeightTransferSwitch"] as? Boolean ?: false
                        sharedViewModel.carCenterofMassHeight = data["carCenterofMassHeight"] as? String ?: ""
                        sharedViewModel.carWheelBase = data["carWheelBase"] as? String ?: ""

                        sharedViewModel.drivetrainRPMTorqueEntriesCount = (data["drivetrainRPMTorqueEntriesCount"] as? Long)?.toInt() ?: 5
                        sharedViewModel.drivetrainOffClutchRPM = data["drivetrainOffClutchRPM"] as? String ?: ""
                        sharedViewModel.drivetrainGasStartingLevel = data["drivetrainGasStartingLevel"] as? String ?: ""
                        sharedViewModel.drivetrainshiftTime = data["drivetrainshiftTime"] as? String ?: ""
                        sharedViewModel.drivetrainDrivetrainLayout = data["drivetrainDrivetrainLayout"] as? String ?: "Rear Wheel Drive"
                        sharedViewModel.drivetrainDrivetrainLoss = data["drivetrainDrivetrainLoss"] as? String ?: ""
                        sharedViewModel.drivetrainFinalDrive = data["drivetrainFinalDrive"] as? String ?: ""
                        sharedViewModel.drivetrainGearRatioCount = (data["drivetrainGearRatioCount"] as? Long)?.toInt() ?: 3

                        val gearRatios = data["gearRatiosList"] as? List<String> ?: emptyList()
                        sharedViewModel.gearRatiosList.addAll(gearRatios)

                        val rpmTorqueList = data["rpmTorqueList"] as? List<Map<String, String>> ?: emptyList()
                        sharedViewModel.rpmTorqueList.addAll(rpmTorqueList.map { Pair(it["rpm"] ?: "", it["torque"] ?: "") })

                        sharedViewModel.tireWidth = data["tireWidth"] as? String ?: ""
                        sharedViewModel.tireAspectRatio = data["tireAspectRatio"] as? String ?: ""
                        sharedViewModel.tireWheelDiameter = data["tireWheelDiameter"] as? String ?: ""
                        sharedViewModel.tireFrictionCoeff = data["tireFrictionCoeff"] as? String ?: ""
                        sharedViewModel.tireRollingCoeff = data["tireRollingCoeff"] as? String ?: ""

                        sharedViewModel.aeroDragCoeff = data["aeroDragCoeff"] as? String ?: ""
                        sharedViewModel.aeroFrontalArea = data["aeroFrontalArea"] as? String ?: ""
                        sharedViewModel.aeroAirDensity = data["aeroAirDensity"] as? String ?: ""
                        sharedViewModel.aeroDownforceSwitch = data["aeroDownforceSwitch"] as? Boolean ?: false
                        sharedViewModel.aeroNegativeLiftCoeff = data["aeroNegativeLiftCoeff"] as? String ?: ""
                        sharedViewModel.aeroDownforceTotalArea = data["aeroDownforceTotalArea"] as? String ?: ""
                        sharedViewModel.aeroDownforceDistribution = data["aeroDownforceDistribution"] as? String ?: ""

                        sharedViewModel.initialSpeed = data["initialSpeed"] as? String ?: ""
                        sharedViewModel.finalSpeed = data["finalSpeed"] as? String ?: ""
                    }
                    onComplete(true, null)
                } else {
                    onComplete(false, "Dataset not found.")
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }


}
