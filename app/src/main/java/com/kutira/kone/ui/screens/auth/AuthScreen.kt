package com.kutira.kone.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutira.kone.ui.components.GradientBackground
import com.kutira.kone.ui.viewmodel.AuthViewModel
import androidx.navigation.NavController
import androidx.compose.material3.TextButton

@Composable
fun AuthScreen(
    navController: NavController,
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val activity = LocalContext.current as? ComponentActivity ?: return

    GradientBackground {
        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Text("← Back")
            }
            Text(
                "Sign in with phone",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "We will send a 6-digit OTP. Use your country code, e.g. +91XXXXXXXXXX",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.loading
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.sendOtp(phone.trim(), activity) },
                enabled = !ui.loading && phone.length >= 8,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ui.otpSent) "Resend OTP" else "Send OTP")
            }
            if (ui.otpSent) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !ui.loading
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.verifyOtp(otp.trim(), onAuthenticated) },
                    enabled = !ui.loading && otp.length >= 4,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify & continue")
                }
            }
            ui.error?.let { err ->
                Spacer(Modifier.height(12.dp))
                Text(err, color = MaterialTheme.colorScheme.error)
            }
            if (ui.loading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
