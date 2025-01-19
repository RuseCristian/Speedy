package com.example.speedy.navigation.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.speedy.R
import com.example.speedy.firestore.FirestoreUtils
import com.example.speedy.firestore.UserPreferencesCache
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    val oneTapClient = remember { Identity.getSignInClient(context) }

    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(context.getString(R.string.your_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    var isSigningIn by remember { mutableStateOf(false) }
    var signInError by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken, auth) { success, error ->
                        if (success) {
                            FirestoreUtils.initializeUserIfNeeded { initSuccess, initError ->
                                if (initSuccess) {
                                    UserPreferencesCache.preferences = null // Clear cache
                                    onLoginSuccess()
                                } else {
                                    signInError = "Failed to initialize user: $initError"
                                    isSigningIn = false
                                }
                            }
                        } else {
                            signInError = error
                            isSigningIn = false
                        }
                    }
                } else {
                    signInError = "Google ID Token is null."
                    isSigningIn = false
                }
            } catch (e: Throwable) {
                Log.e("Google One Tap Login Error", "$e")
                isSigningIn = false
            }
        } else {
            signInError = "Sign-in canceled or failed."
            isSigningIn = false
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Speedy",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "The vehicle longitudinal performance estimator",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.baseline_air_24),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 32.dp)
        )

        if (isSigningIn) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = {
                    isSigningIn = true
                    signInError = null
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            launcher.launch(
                                IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                            )
                        }
                        .addOnFailureListener { e ->
                            signInError = "Sign-in initiation failed: ${e.localizedMessage}"
                            isSigningIn = false
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In with Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (signInError != null) {
            Text(
                text = signInError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
fun firebaseAuthWithGoogle(
    idToken: String,
    auth: FirebaseAuth,
    onComplete: (Boolean, String?) -> Unit
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign-in success
                onComplete(true, null)
            } else {
                // Sign-in failure
                val errorMessage = task.exception?.localizedMessage ?: "Unknown error occurred."
                onComplete(false, errorMessage)
            }
        }
        .addOnFailureListener { e ->
            // Handle failure explicitly
            val errorMessage = e.localizedMessage ?: "Unknown error occurred."
            onComplete(false, errorMessage)
        }
}