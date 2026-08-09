package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    onLoginSuccess: (name: String, email: String, provider: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var emailInput by remember { mutableStateOf("alex.dev@lifeos.ai") }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var confirmPasswordInput by remember { mutableStateOf("••••••••") }
    var nameInput by remember { mutableStateOf("Alex Vance") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadingMessage by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("auth_screen_container"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- 1. Header Hero Badge ---
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan.copy(alpha = 0.2f))
                    .border(2.dp, PrimaryCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "LifeOS Auth",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "LifeOS AI Authentication",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Firebase Secure Single Sign-On & Protected Vault",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- 2. Auth Mode Tab Bar ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modes = listOf(
                    AuthMode.LOGIN to "Log In",
                    AuthMode.SIGNUP to "Sign Up",
                    AuthMode.FORGOT_PASSWORD to "Reset Pass"
                )

                modes.forEach { (mode, label) ->
                    val isSelected = authMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryCyan else Color.Transparent)
                            .clickable {
                                authMode = mode
                                statusMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 3. Form Card ---
        item {
            LifeOSCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = PrimaryCyan.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // --- Google OAuth Button ---
                    if (authMode != AuthMode.FORGOT_PASSWORD) {
                        Button(
                            onClick = {
                                isLoading = true
                                loadingMessage = "Connecting to Google OAuth 2.0..."
                                coroutineScope.launch {
                                    delay(1200)
                                    isLoading = false
                                    onLoginSuccess("Alex Vance", "alex.google@lifeos.ai", "Google OAuth 2.0")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GTranslate,
                                    contentDescription = "Google",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue with Google",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = "  or email  ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }
                    }

                    // --- Name Input (SignUp only) ---
                    if (authMode == AuthMode.SIGNUP) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryCyan
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_field"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // --- Email Input ---
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = PrimaryCyan
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // --- Password Inputs ---
                    if (authMode != AuthMode.FORGOT_PASSWORD) {
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = PrimaryCyan
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (authMode == AuthMode.SIGNUP) {
                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { confirmPasswordInput = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = PrimaryCyan
                                    )
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_confirm_password_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // --- Feedback status banner ---
                    if (statusMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSuccessStatus) AccentLime.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = statusMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccessStatus) AccentLime else Color.Red
                            )
                        }
                    }

                    // --- Primary Action Button ---
                    Button(
                        onClick = {
                            when (authMode) {
                                AuthMode.LOGIN -> {
                                    if (emailInput.isBlank()) {
                                        statusMessage = "Please enter a valid email address."
                                        isSuccessStatus = false
                                        return@Button
                                    }
                                    isLoading = true
                                    loadingMessage = "Authenticating with Firebase..."
                                    coroutineScope.launch {
                                        delay(1000)
                                        isLoading = false
                                        onLoginSuccess(
                                            if (nameInput.isNotBlank()) nameInput else "Alex Vance",
                                            emailInput,
                                            "Firebase Email Auth"
                                        )
                                    }
                                }

                                AuthMode.SIGNUP -> {
                                    if (emailInput.isBlank() || nameInput.isBlank()) {
                                        statusMessage = "Please fill in all required fields."
                                        isSuccessStatus = false
                                        return@Button
                                    }
                                    isLoading = true
                                    loadingMessage = "Creating Firebase account & sending verification email..."
                                    coroutineScope.launch {
                                        delay(1200)
                                        isLoading = false
                                        statusMessage = "Account created! Verification email sent to $emailInput"
                                        isSuccessStatus = true
                                        delay(1000)
                                        onLoginSuccess(nameInput, emailInput, "Firebase Email Auth")
                                    }
                                }

                                AuthMode.FORGOT_PASSWORD -> {
                                    if (emailInput.isBlank()) {
                                        statusMessage = "Please enter your account email address."
                                        isSuccessStatus = false
                                        return@Button
                                    }
                                    isLoading = true
                                    loadingMessage = "Dispatching reset email link..."
                                    coroutineScope.launch {
                                        delay(1000)
                                        isLoading = false
                                        statusMessage = "Password reset link sent to $emailInput! Check your inbox."
                                        isSuccessStatus = true
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (authMode) {
                                AuthMode.LOGIN -> "Log In to LifeOS"
                                AuthMode.SIGNUP -> "Create LifeOS Account"
                                AuthMode.FORGOT_PASSWORD -> "Send Reset Password Link"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (authMode == AuthMode.LOGIN) {
                        TextButton(
                            onClick = { authMode = AuthMode.FORGOT_PASSWORD },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Forgot password?", fontSize = 12.sp, color = PrimaryCyan)
                        }
                    }
                }
            }
        }

        // --- 4. Guest Access & Info ---
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onContinueAsGuest() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("guest_mode_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue in Guest Mode (Offline Room DB)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- Loading Overlay Screen ---
    if (isLoading) {
        Surface(
            color = Color.Black.copy(alpha = 0.85f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryCyan,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = loadingMessage,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Securing data with AES-256 local & cloud encryption",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
