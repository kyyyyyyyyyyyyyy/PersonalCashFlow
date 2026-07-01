package com.example.cashflow.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.data.remote.FirebaseService
import com.example.cashflow.navigation.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1500)
        if (FirebaseService.isSignedIn()) {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.SIGN_IN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Personal Cashflow", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun SignInScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Personal Cashflow", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(48.dp))

            if (errorMsg != null) {
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
            }

            GoogleSignInButton(navController, scope, isLoading) { l, e ->
                isLoading = l; errorMsg = e
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isLoading = true; errorMsg = null
                        FirebaseService.signInAnonymously()
                            .onSuccess {
                                isLoading = false
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(Routes.SIGN_IN) { inclusive = true }
                                }
                            }.onFailure { e ->
                                isLoading = false; errorMsg = "Gagal masuk: ${e.message}"
                            }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lanjutkan sebagai Tamu")
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    navController: NavController,
    scope: kotlinx.coroutines.CoroutineScope,
    isLoading: Boolean,
    onStateChange: (Boolean, String?) -> Unit
) {
    val context = LocalContext.current
    val gso = remember {
        try {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(com.example.cashflow.R.string.default_web_client_id))
                .requestEmail()
                .build()
        } catch (_: Exception) {
            null
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            scope.launch {
                onStateChange(true, null)
                FirebaseService.signInWithGoogle(account.idToken ?: "")
                    .onSuccess {
                        onStateChange(false, null)
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.SIGN_IN) { inclusive = true }
                        }
                    }.onFailure { e ->
                        onStateChange(false, "Gagal Google Sign-In: ${e.message}")
                    }
            }
        } catch (_: Exception) {
            onStateChange(false, null)
        }
    }

    Button(
        onClick = {
            if (gso != null) {
                val client = GoogleSignIn.getClient(context, gso)
                launcher.launch(client.signInIntent)
            } else {
                onStateChange(false, "Google Sign-In tidak tersedia. Gunakan tombol Tamu.")
            }
        },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Masuk dengan Google")
    }
}
