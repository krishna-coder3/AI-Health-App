package com.example.ui.screens.reports

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.ai.AiHealthService
import com.example.data.ai.ReportAnalysisResult
import com.example.data.model.MedicalReport
import com.example.data.repository.HealthRepository
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.HealthBlueSecondary
import com.example.ui.theme.HealthEmergencyContainer
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthTealPrimary
import kotlinx.coroutines.launch

@Composable
fun ReportSummaryScreen(
    repository: HealthRepository,
    aiService: AiHealthService
) {
    val context = LocalContext.current
    val userProfile by repository.observeUserProfile().collectAsState(initial = null)
    val savedReports by repository.observeMedicalReports().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Analyze New Report, 1: Saved Reports History
    var reportTitle by remember { mutableStateOf("") }
    var reportText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var currentResult by remember { mutableStateOf<ReportAnalysisResult?>(null) }

    // Android zero-permission photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val sampleTemplates = mapOf(
        "Lipid Profile" to """
            Total Cholesterol: 245 mg/dL (Normal: <200)
            HDL Cholesterol: 38 mg/dL (Normal: >40)
            LDL Cholesterol: 165 mg/dL (Normal: <100)
            Triglycerides: 210 mg/dL (Normal: <150)
        """.trimIndent(),
        "CBC Blood Count" to """
            Hemoglobin (Hb): 10.8 g/dL (Normal: 12.0 - 15.5)
            WBC Count: 11,200 /mcL (Normal: 4,000 - 11,000)
            Platelet Count: 210,000 /mcL (Normal: 150,000 - 450,000)
            RBC: 4.1 million/mcL (Normal: 4.2 - 5.4)
        """.trimIndent(),
        "Thyroid Profile" to """
            TSH (Thyroid Stimulating Hormone): 6.4 uIU/mL (Normal: 0.4 - 4.2)
            Total T3: 95 ng/dL (Normal: 80 - 200)
            Total T4: 6.8 ug/dL (Normal: 5.0 - 12.0)
        """.trimIndent(),
        "Blood Sugar" to """
            Fasting Blood Glucose: 138 mg/dL (Normal: 70 - 99)
            HbA1c: 7.2% (Normal: <5.7%, Prediabetes: 5.7-6.4%, Diabetes: >=6.5%)
        """.trimIndent()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("report_summary_screen")
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = HealthTealPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Analyze Report", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Saved Reports (${savedReports.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        MedicalDisclaimerBanner()
                    }

                    // Card for input
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Upload or Paste Lab Report",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "AI explains complex clinical values in simple terms and highlights out-of-range figures.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = reportTitle,
                                    onValueChange = { reportTitle = it },
                                    label = { Text("Report Title (e.g. Annual Blood Checkup)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick template pills
                                Text(
                                    text = "Or fill with sample report data:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(sampleTemplates.keys.toList()) { key ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.clickable {
                                                reportTitle = key
                                                reportText = sampleTemplates[key] ?: ""
                                            }
                                        ) {
                                            Text(
                                                text = "+ $key",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = HealthBlueSecondary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = reportText,
                                    onValueChange = { reportText = it },
                                    label = { Text("Report Text / Laboratory Values") },
                                    placeholder = { Text("e.g. Cholesterol: 240 mg/dL, Hb: 11.2 g/dL...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 5,
                                    maxLines = 10
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Photo picker button
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (selectedImageUri != null) "Change Report Photo" else "Attach Report Photo")
                                    }

                                    if (selectedImageUri != null) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Image attached ✓",
                                            color = Color(0xFF10B981),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Analyze button
                    item {
                        Button(
                            onClick = {
                                val text = reportText.ifBlank { "Sample routine lab report" }
                                val title = reportTitle.ifBlank { "Medical Lab Report" }
                                isAnalyzing = true
                                currentResult = null

                                scope.launch {
                                    val bitmap = selectedImageUri?.let { uri ->
                                        try {
                                            context.contentResolver.openInputStream(uri)?.use { stream ->
                                                BitmapFactory.decodeStream(stream)
                                            }
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }

                                    val lang = userProfile?.languagePreference ?: "English"
                                    val result = aiService.analyzeMedicalReport(text, bitmap, lang)
                                    currentResult = result

                                    // Save report to user's reports collection in Firestore
                                    repository.saveMedicalReport(
                                        MedicalReport(
                                            userId = repository.requireUserId(),
                                            title = title,
                                            summaryText = result.simpleSummary,
                                            abnormalValues = result.abnormalValues,
                                            suggestedQuestions = result.suggestedQuestions
                                        )
                                    )
                                    isAnalyzing = false
                                }
                            },
                            enabled = (reportText.isNotBlank() || selectedImageUri != null) && !isAnalyzing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("analyze_report_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Analyzing Lab Report with AI...")
                            } else {
                                Icon(Icons.Default.UploadFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyze & Explain Report with AI", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Result breakdown
                    if (currentResult != null) {
                        val res = currentResult!!
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "AI Report Explanation",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = res.simpleSummary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 22.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Abnormal values callout
                                    Text(
                                        text = "Values Outside Standard Range",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = HealthEmergencyRed
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    res.abnormalValues.forEach { valStr ->
                                        Surface(
                                            color = HealthEmergencyContainer,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = HealthEmergencyRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = valStr,
                                                    color = HealthEmergencyRed,
                                                    fontWeight = FontWeight.SemiBold,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Suggested questions for doctor
                                    Text(
                                        text = "Questions to Ask Your Doctor",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = HealthBlueSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    res.suggestedQuestions.forEach { q ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.HelpOutline,
                                                contentDescription = null,
                                                tint = HealthBlueSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
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
                                            text = "⚠️ ${res.disclaimer}",
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

            1 -> {
                // Saved Reports History
                if (savedReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No saved report summaries",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Analyze a lab report to save your explanation history.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(savedReports) { report ->
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
                                            text = report.title,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = HealthTealPrimary
                                        )

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    if (report.id.isNotBlank()) {
                                                        repository.deleteMedicalReport(report.id)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = report.summaryText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (report.abnormalValues.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Flagged Values:",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = HealthEmergencyRed
                                        )
                                        report.abnormalValues.forEach { v ->
                                            Text(
                                                text = "• $v",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = HealthEmergencyRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
