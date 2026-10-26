package com.example.ui.screens.symptomchecker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiHealthService
import com.example.data.ai.SymptomAnalysisResult
import com.example.data.repository.HealthRepository
import com.example.ui.components.EmergencyAlertDialog
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.HealthBlueSecondary
import com.example.ui.theme.HealthEmergencyContainer
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthSuccess
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.HealthWarning
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCheckerScreen(
    repository: HealthRepository,
    aiService: AiHealthService
) {
    val userProfile by repository.observeUserProfile().collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    val commonSymptoms = listOf(
        "Headache", "Fever / Chills", "Sore Throat", "Runny Nose", "Dry Cough",
        "Chest Tightness", "Shortness of breath", "Nausea / Vomiting", "Stomach Ache / Acidity",
        "Loose motions / Diarrhea", "Bloating / Gas", "Fatigue / Weakness", "Joint / Body Ache",
        "Skin Rash / Itching", "Dizziness", "Insomnia / Anxiety"
    )

    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var durationDays by remember { mutableFloatStateOf(2f) }
    var severity by remember { mutableStateOf("Moderate") }
    var additionalNotes by remember { mutableStateOf("") }

    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<SymptomAnalysisResult?>(null) }
    var showEmergencyDialog by remember { mutableStateOf(false) }

    if (showEmergencyDialog) {
        EmergencyAlertDialog(onDismiss = { showEmergencyDialog = false })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("symptom_checker_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = HealthTealPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "AI Symptom Checker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Identify possible causes and appropriate home care",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            MedicalDisclaimerBanner()
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Select your symptoms (${selectedSymptoms.size} chosen)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        commonSymptoms.forEach { symptom ->
                            val isChosen = selectedSymptoms.contains(symptom)
                            FilterChip(
                                selected = isChosen,
                                onClick = {
                                    if (isChosen) selectedSymptoms.remove(symptom) else selectedSymptoms.add(symptom)
                                },
                                label = { Text(symptom, fontSize = 12.sp) },
                                leadingIcon = if (isChosen) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. How long have you felt this? (${durationDays.toInt()} days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Slider(
                        value = durationDays,
                        onValueChange = { durationDays = it },
                        valueRange = 1f..14f,
                        steps = 12,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Severity Level",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Mild", "Moderate", "Severe").forEach { sev ->
                            val isSelected = severity == sev
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { severity = sev },
                                color = if (isSelected) {
                                    when (sev) {
                                        "Mild" -> HealthSuccess.copy(alpha = 0.2f)
                                        "Moderate" -> HealthWarning.copy(alpha = 0.2f)
                                        else -> HealthEmergencyContainer
                                    }
                                } else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = sev,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        when (sev) {
                                            "Mild" -> HealthSuccess
                                            "Moderate" -> HealthWarning
                                            else -> HealthEmergencyRed
                                        }
                                    } else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        placeholder = { Text("Any other sensations or existing conditions...") },
                        label = { Text("Additional notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (selectedSymptoms.isNotEmpty() && !isAnalyzing) {
                        val combined = selectedSymptoms.joinToString(" ") + " " + additionalNotes
                        if (aiService.checkIsEmergency(combined)) {
                            showEmergencyDialog = true
                        }

                        isAnalyzing = true
                        analysisResult = null

                        scope.launch {
                            val lang = userProfile?.languagePreference ?: "English"
                            val res = aiService.analyzeSymptoms(
                                symptoms = selectedSymptoms.toList(),
                                durationDays = durationDays.toInt(),
                                severity = severity,
                                additionalNotes = additionalNotes,
                                preferredLanguage = lang
                            )
                            analysisResult = res
                            isAnalyzing = false
                        }
                    }
                },
                enabled = selectedSymptoms.isNotEmpty() && !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("analyze_symptoms_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Analyzing Symptoms with AI...")
                } else {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Symptoms with AI", fontWeight = FontWeight.Bold)
                }
            }
        }

        val currentAnalysis = analysisResult
        if (currentAnalysis != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Analysis Results",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = when (currentAnalysis.urgencyLevel) {
                                    "Emergency Care Required" -> HealthEmergencyContainer
                                    "Consult Doctor Soon" -> HealthWarning.copy(alpha = 0.2f)
                                    else -> HealthSuccess.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentAnalysis.urgencyLevel,
                                    color = when (currentAnalysis.urgencyLevel) {
                                        "Emergency Care Required" -> HealthEmergencyRed
                                        "Consult Doctor Soon" -> HealthWarning
                                        else -> HealthSuccess
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Possible General Causes",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = HealthTealPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        for (cause in currentAnalysis.possibleCauses) {
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = HealthTealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(cause, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Home Care & Comfort Tips",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = HealthBlueSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        for (tip in currentAnalysis.homeCareTips) {
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = HealthBlueSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tip, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Suggested Questions for Doctor",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8B5CF6)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        for (q in currentAnalysis.questionsForDoctor) {
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(q, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ ${currentAnalysis.disclaimer}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
