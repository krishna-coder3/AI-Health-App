package com.example.ui.screens.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.model.UserProfile
import com.example.data.repository.HealthRepository
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthTealPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: HealthRepository,
    onSignOutComplete: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userProfile by repository.observeUserProfile().collectAsState(initial = null)

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editGender by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }
    var editAllergies by remember { mutableStateOf("") }
    var editConditions by remember { mutableStateOf("") }
    var editLanguage by remember { mutableStateOf("English") }

    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

    val profile = userProfile ?: UserProfile()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User profile header card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Avatar",
                            tint = HealthTealPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile.displayName.ifBlank { "User" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = profile.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = if (profile.role == "owner") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (profile.role == "owner") "🛡️ Account Role: Owner / Admin" else "Member Account",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile.role == "owner") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (profile.role == "owner") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Admin / Owner Panel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Vitals and physical profile card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Health & Physical Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = {
                                if (!isEditing) {
                                    editName = profile.displayName
                                    editAge = profile.age.toString()
                                    editGender = profile.gender
                                    editHeight = profile.height.toString()
                                    editWeight = profile.weight.toString()
                                    editAllergies = profile.allergies
                                    editConditions = profile.conditions
                                    editLanguage = profile.languagePreference
                                    isEditing = true
                                } else {
                                    // Save changes
                                    scope.launch {
                                        repository.updateUserProfile(
                                            profile.copy(
                                                displayName = editName,
                                                age = editAge.toIntOrNull() ?: profile.age,
                                                gender = editGender,
                                                height = editHeight.toDoubleOrNull() ?: profile.height,
                                                weight = editWeight.toDoubleOrNull() ?: profile.weight,
                                                allergies = editAllergies,
                                                conditions = editConditions,
                                                languagePreference = editLanguage
                                            )
                                        )
                                        isEditing = false
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Save" else "Edit",
                                tint = HealthTealPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editGender,
                                onValueChange = { editGender = it },
                                label = { Text("Gender") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editHeight,
                                onValueChange = { editHeight = it },
                                label = { Text("Height (cm)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editWeight,
                                onValueChange = { editWeight = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editAllergies,
                            onValueChange = { editAllergies = it },
                            label = { Text("Known Allergies (e.g. Peanuts, Penicillin)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editConditions,
                            onValueChange = { editConditions = it },
                            label = { Text("Pre-existing Conditions (e.g. Asthma, Hypertension)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ProfileInfoRow("Age", "${profile.age} years")
                        ProfileInfoRow("Gender", profile.gender)
                        ProfileInfoRow("Height", "${profile.height.toInt()} cm")
                        ProfileInfoRow("Weight", "${profile.weight.toInt()} kg")
                        ProfileInfoRow("Calculated BMI", "${profile.bmi} (${profile.bmiCategory})")
                        ProfileInfoRow("Allergies", profile.allergies.ifBlank { "None recorded" })
                        ProfileInfoRow("Conditions", profile.conditions.ifBlank { "None recorded" })
                    }
                }
            }
        }

        // Language preference
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = HealthTealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Assistant Language Preference",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("English", "Hindi", "Hinglish").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = profile.languagePreference == lang,
                                onClick = {
                                    scope.launch {
                                        repository.updateUserProfile(profile.copy(languagePreference = lang))
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (lang) {
                                    "Hindi" -> "हिंदी (Hindi)"
                                    "Hinglish" -> "Hinglish (Hindi in Roman script)"
                                    else -> "English"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Privacy & Terms
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = HealthTealPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Privacy Policy & Data Security",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = { showPrivacyPolicy = true }) {
                            Text("View", fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = HealthTealPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Terms of Service & Clinical Disclaimer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = { showTerms = true }) {
                            Text("View", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            MedicalDisclaimerBanner()
        }

        // Sign out button
        item {
            Button(
                onClick = {
                    val credentialManager = CredentialManager.create(context)
                    Firebase.auth.signOut()
                    scope.launch {
                        try {
                            credentialManager.clearCredentialState(ClearCredentialStateRequest())
                        } catch (e: Exception) {
                            // ignore
                        } finally {
                            onSignOutComplete()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Sign Out",
                    tint = HealthEmergencyRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Account", color = HealthEmergencyRed, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicy = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "AI Health Companion respects user confidentiality. Your health logs, appointment bookings, and personal vitals are encrypted and stored in your dedicated Firestore documents. Personal health data is never shared with third parties or advertisers."
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyPolicy = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showTerms) {
        AlertDialog(
            onDismissRequest = { showTerms = false },
            title = { Text("Medical Terms & Disclaimer", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "AI Health Companion is an educational wellness tool. It does not provide definitive medical diagnoses, prescription medications, or emergency medical treatment. In the event of acute symptoms such as severe chest pain, shortness of breath, sudden numbness, or heavy bleeding, immediately seek local emergency services (112 / 911)."
                )
            },
            confirmButton = {
                Button(onClick = { showTerms = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
