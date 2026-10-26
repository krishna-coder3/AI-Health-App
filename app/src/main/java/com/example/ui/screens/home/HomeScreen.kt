package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthLog
import com.example.data.model.UserProfile
import com.example.data.repository.HealthRepository
import com.example.ui.components.EmergencyAlertDialog
import com.example.ui.components.HealthStatCard
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.HealthBlueSecondary
import com.example.ui.theme.HealthEmergencyContainer
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthSuccess
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.HealthWarning
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    repository: HealthRepository,
    onNavigateToAssistant: () -> Unit,
    onNavigateToRemedies: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToSymptomChecker: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val userProfile by repository.observeUserProfile().collectAsState(initial = null)
    val healthLogs by repository.observeHealthLogs().collectAsState(initial = emptyList())
    val announcements by repository.observeAnnouncements().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showEmergencyDialog by remember { mutableStateOf(false) }

    val latestLog = healthLogs.firstOrNull() ?: HealthLog()

    if (showEmergencyDialog) {
        EmergencyAlertDialog(onDismiss = { showEmergencyDialog = false })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Greeting & AI Assistant Pill
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, ${userProfile?.displayName?.ifBlank { "User" } ?: "User"}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (userProfile?.role == "owner") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (userProfile?.role == "owner") "Admin / Owner" else "Member",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (userProfile?.role == "owner") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (userProfile?.role == "owner") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Admin Panel",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { onNavigateToAdmin() }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }

                        // Emergency quick button
                        IconButton(
                            onClick = { showEmergencyDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HealthEmergencyContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency Alert",
                                tint = HealthEmergencyRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Assistant Highlight Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(HealthTealPrimary, HealthBlueSecondary)
                                )
                            )
                            .clickable { onNavigateToAssistant() }
                            .padding(14.dp)
                            .testTag("ai_assistant_pill_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ask AI Health Assistant",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Ask symptoms & tips in Hindi, Hinglish or English",
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Health Summary Cards Grid
        item {
            Text(
                text = "Today's Health Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Blood Pressure
                HealthStatCard(
                    title = "Blood Pressure",
                    value = "${latestLog.systolic}/${latestLog.diastolic}",
                    unit = "mmHg",
                    icon = Icons.Default.Speed,
                    statusText = if (latestLog.systolic <= 120 && latestLog.diastolic <= 80) "Optimal" else "Elevated",
                    statusColor = if (latestLog.systolic <= 120 && latestLog.diastolic <= 80) HealthSuccess else HealthWarning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTracker
                )

                // Heart Rate
                HealthStatCard(
                    title = "Heart Rate",
                    value = "${latestLog.heartRate}",
                    unit = "bpm",
                    icon = Icons.Default.MonitorHeart,
                    statusText = if (latestLog.heartRate in 60..100) "Normal" else "Review",
                    statusColor = if (latestLog.heartRate in 60..100) HealthSuccess else HealthWarning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTracker
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Blood Sugar
                HealthStatCard(
                    title = "Blood Sugar",
                    value = "${latestLog.bloodSugar}",
                    unit = "mg/dL",
                    icon = Icons.Default.Vaccines,
                    statusText = if (latestLog.bloodSugar < 100) "Normal" else "High",
                    statusColor = if (latestLog.bloodSugar < 100) HealthSuccess else HealthWarning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTracker
                )

                // Weight / BMI
                val bmiVal = userProfile?.bmi ?: 0.0
                HealthStatCard(
                    title = "Weight / BMI",
                    value = if (bmiVal > 0) "$bmiVal" else "${latestLog.weight.toInt()} kg",
                    unit = if (bmiVal > 0) "BMI" else "kg",
                    icon = Icons.Default.Scale,
                    statusText = userProfile?.bmiCategory ?: "Good",
                    statusColor = HealthSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTracker
                )
            }
        }

        // Water Intake quick card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = "Water Intake",
                            tint = HealthBlueSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Water Intake",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${latestLog.waterIntakeMl}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " / 2500 ml",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                val updatedLog = latestLog.copy(
                                    date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                                    waterIntakeMl = latestLog.waterIntakeMl + 250
                                )
                                repository.addHealthLog(updatedLog)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HealthBlueSecondary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("quick_add_water_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+250ml", fontSize = 12.sp)
                    }
                }
            }
        }

        // 3. Quick Action Hub
        item {
            Text(
                text = "Health Services & Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionSquare(
                    title = "Symptom\nChecker",
                    icon = Icons.Default.Psychology,
                    color = HealthTealPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSymptomChecker
                )
                QuickActionSquare(
                    title = "Medical\nReports",
                    icon = Icons.Default.UploadFile,
                    color = HealthBlueSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReports
                )
                QuickActionSquare(
                    title = "Home\nRemedies",
                    icon = Icons.Default.Favorite,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRemedies
                )
                QuickActionSquare(
                    title = "Book\nDoctor",
                    icon = Icons.Default.MonitorHeart,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAppointments
                )
            }
        }

        // 4. Announcements
        if (announcements.isNotEmpty()) {
            item {
                Text(
                    text = "Health Bulletins & Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(announcements.take(2)) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ann.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ann.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ann.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // 5. Medical Disclaimer
        item {
            MedicalDisclaimerBanner()
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuickActionSquare(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
