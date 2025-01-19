package com.example.speedy.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object UserPreferencesCache {
    var preferences: MutableMap<String, Any?>? = null
}

object FirestoreUtils {
    private const val TAG = "FirestoreUtils"
    private const val USERS_COLLECTION = "users"
    private const val DATASETS_COLLECTION = "datasets"

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    // Existing methods
    fun initializeUserIfNeeded(onComplete: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "No authenticated user.")
            return
        }

        val userId = currentUser.uid
        val userDocument = firestore.collection(USERS_COLLECTION).document(userId)

        userDocument.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "User already exists in Firestore.")
                    onComplete(true, null)
                } else {
                    Log.d(TAG, "User does not exist. Creating structure...")
                    val initialData = mapOf(
                        "preferences" to mapOf(
                            "isMetric" to true // Default value for preferences
                        )
                    )
                    userDocument.set(initialData)
                        .addOnSuccessListener {
                            Log.d(TAG, "User structure created successfully.")
                            onComplete(true, null)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error creating user structure.", e)
                            onComplete(false, e.localizedMessage)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching user document.", e)
                onComplete(false, e.localizedMessage)
            }
    }

    fun getUserPreference(
        preferenceKey: String,
        onResult: (Any?, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onResult(null, "No authenticated user.")
            return
        }

        val userId = currentUser.uid
        val userDocument = firestore.collection(USERS_COLLECTION).document(userId)

        if (UserPreferencesCache.preferences != null) {
            onResult(UserPreferencesCache.preferences?.get(preferenceKey), null)
        } else {
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        UserPreferencesCache.preferences =
                            document.get("preferences") as? MutableMap<String, Any?>
                        onResult(UserPreferencesCache.preferences?.get(preferenceKey), null)
                    } else {
                        onResult(null, "User preferences not found.")
                    }
                }
                .addOnFailureListener { e ->
                    onResult(null, e.localizedMessage)
                }
        }
    }

    fun updateUserPreference(
        preferenceKey: String,
        newValue: Any,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onResult(false, "No authenticated user.")
            return
        }

        val userId = currentUser.uid
        val userDocument = firestore.collection(USERS_COLLECTION).document(userId)

        userDocument.update("preferences.$preferenceKey", newValue)
            .addOnSuccessListener {
                if (UserPreferencesCache.preferences == null) {
                    UserPreferencesCache.preferences = mutableMapOf()
                }
                UserPreferencesCache.preferences?.set(preferenceKey, newValue)
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }

    fun saveDataset(
        datasetName: String,
        datasetData: Map<String, Any>,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        // Get the current user's ID
        val userId = currentUser.uid

        // Sanitize the dataset name to avoid invalid characters
        val sanitizedDatasetName = datasetName.replace("[/\\.\\#\\$\\[\\]]".toRegex(), "_")

        // Reference the user's datasets sub-collection
        val datasetsCollection = firestore.collection("users").document(userId).collection("datasets")

        // Check if a dataset with the same name already exists
        datasetsCollection.document(sanitizedDatasetName).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(false, "A dataset with this name already exists.")
                } else {
                    // Save the new dataset
                    datasetsCollection.document(sanitizedDatasetName).set(datasetData)
                        .addOnSuccessListener {
                            onComplete(true, null)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error saving dataset.", e)
                            onComplete(false, e.localizedMessage)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking for duplicate dataset.", e)
                onComplete(false, e.localizedMessage)
            }
    }



    fun getDatasets(onComplete: (List<String>, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            onComplete(emptyList(), "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        val datasetsCollection = firestore.collection("users").document(userId).collection("datasets")

        datasetsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val datasetNames = querySnapshot.documents.map { it.id }
                onComplete(datasetNames, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching datasets.", e)
                onComplete(emptyList(), e.localizedMessage)
            }
    }


    fun deleteDataset(datasetName: String, onComplete: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            onComplete(false, "User not authenticated.")
            return
        }

        val userId = currentUser.uid
        val datasetsCollection = firestore.collection("users").document(userId).collection("datasets")

        datasetsCollection.document(datasetName).delete()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting dataset.", e)
                onComplete(false, e.localizedMessage)
            }
    }

}
