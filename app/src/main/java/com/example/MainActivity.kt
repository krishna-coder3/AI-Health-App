package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.data.ai.AiHealthService
import com.example.data.repository.HealthRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.screens.auth.SignInScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check initialization with launch intent before UI/ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    // Attempt silent auto-sign in if no current user
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            val clientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                null
            }

            if (!clientId.isNullOrBlank()) {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                scope.launch {
                    try {
                        val result = credentialManager.getCredential(context, request)
                        val credential = result.credential
                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                            Firebase.auth.signInWithCredential(authCredential).await()
                            currentUser = Firebase.auth.currentUser
                        }
                    } catch (e: Exception) {
                        // Silent check failed, user will see Google Sign-In button
                    }
                }
            }
        }
    }

    if (currentUser == null) {
        SignInScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        val repository = remember { HealthRepository(context) }
        val aiService = remember { AiHealthService() }

        // Initialize user profile in background
        LaunchedEffect(currentUser!!.uid) {
            scope.launch {
                try {
                    repository.getOrCreateUserProfile()
                } catch (e: Exception) {
                    // handled inside repository
                }
            }
        }

        AppNavigation(
            repository = repository,
            aiService = aiService,
            onSignOutComplete = {
                currentUser = null
            }
        )
    }
}
