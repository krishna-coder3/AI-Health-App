package com.example.ui.screens.tracker

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.HealthLog
import com.example.data.repository.HealthRepository
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.SimpleVitalsTrendChart
import com.example.ui.theme.HealthBlueSecondary
import com.example.ui.theme.HealthSuccess
import com.example.ui.theme.HealthTealPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MedicineReminder(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val timeSlot: String,
    var isTaken: Boolean = false
)

@Composable
fun HealthTrackerScreen(
    repository: HealthRepository
) {
    val healthLogs by repository.observeHealthLogs().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Charts, 1: History Log, 2: Medicines
    var showAddLogDialog by remember { mutableStateOf(false) }
    var showAddMedicineDialog by remember { mutableStateOf(false) }

    val medicines = remember {
        mutableStateListOf(
            MedicineReminder(name = "Vitamin D3", dosage = "60,000 IU", timeSlot = "09:00 AM (Sundays)", isTaken = true),
            MedicineReminder(name = "Omega-3 Fish Oil", dosage = "1000 mg", timeSlot = "01:00 PM (After Lunch)", isTaken = false),
            MedicineReminder(name = "Multivitamin", dosage = "1 tablet", timeSlot = "08:00 AM (Morning)", isTaken = true)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 2) showAddMedicineDialog = true else showAddLogDialog = true
                },
                containerColor = HealthTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_health_log_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("health_tracker_screen")
        ) {
            // Tabs
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
                        text = { Text("Vitals Trends", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Logs History", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Medicine Reminders", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Charts Overview
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            MedicalDisclaimerBanner()
                        }

                        // Chronological sorting for chart plotting
                        val chronologicalLogs = healthLogs.sortedBy { it.date }
                        val dates = chronologicalLogs.map { it.date.takeLast(5) } // "MM-dd"

                        // 1. Blood Pressure Trend
                        item {
                            val systolicPoints = chronologicalLogs.map { it.systolic.toFloat() }
                            SimpleVitalsTrendChart(
                                title = "Blood Pressure (Systolic)",
                                dataPoints = systolicPoints,
                                dates = dates,
                                lineColor = HealthTealPrimary,
                                unit = "mmHg"
                            )
                        }

                        // 2. Blood Sugar Trend
                        item {
                            val sugarPoints = chronologicalLogs.map { it.bloodSugar.toFloat() }
                            SimpleVitalsTrendChart(
                                title = "Fasting Blood Sugar",
                                dataPoints = sugarPoints,
                                dates = dates,
                                lineColor = HealthBlueSecondary,
                                unit = "mg/dL"
                            )
                        }

                        // 3. Heart Rate Trend
                        item {
                            val hrPoints = chronologicalLogs.map { it.heartRate.toFloat() }
                            SimpleVitalsTrendChart(
                                title = "Resting Heart Rate",
                                dataPoints = hrPoints,
                                dates = dates,
                                lineColor = Color(0xFFEF4444),
                                unit = "bpm"
                            )
                        }

                        // 4. Weight Trend
                        item {
                            val weightPoints = chronologicalLogs.map { it.weight.toFloat() }
                            SimpleVitalsTrendChart(
                                title = "Weight",
                                dataPoints = weightPoints,
                                dates = dates,
                                lineColor = Color(0xFF8B5CF6),
                                unit = "kg"
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }

                1 -> {
                    // Log History list
                    if (healthLogs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No vitals logged yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap the + button to log your first health reading.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(healthLogs) { log ->
                                LogHistoryCard(
                                    log = log,
                                    onDelete = {
                                        scope.launch {
                                            if (log.id.isNotBlank()) {
                                                repository.deleteHealthLog(log.id)
                                            }
                                        }
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(60.dp))
                            }
                        }
                    }
                }

                2 -> {
                    // Medicine Reminders
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = HealthTealPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Daily Medication & Vitamin Checklist",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        items(medicines) { med ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val index = medicines.indexOfFirst { it.id == med.id }
                                            if (index != -1) {
                                                medicines[index] = med.copy(isTaken = !med.isTaken)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (med.isTaken) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Status",
                                            tint = if (med.isTaken) HealthSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = med.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${med.dosage} • ${med.timeSlot}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            medicines.removeAll { it.id == med.id }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddLogDialog) {
        AddHealthLogDialog(
            onDismiss = { showAddLogDialog = false },
            onConfirm = { newLog ->
                scope.launch {
                    repository.addHealthLog(newLog)
                    showAddLogDialog = false
                }
            }
        )
    }

    if (showAddMedicineDialog) {
        AddMedicineDialog(
            onDismiss = { showAddMedicineDialog = false },
            onConfirm = { name, dose, slot ->
                medicines.add(MedicineReminder(name = name, dosage = dose, timeSlot = slot))
                showAddMedicineDialog = false
            }
        )
    }
}

@Composable
private fun LogHistoryCard(
    log: HealthLog,
    onDelete: () -> Unit
) {
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
                    text = log.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HealthTealPrimary
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalsCompactPill(Icons.Default.Speed, "BP", "${log.systolic}/${log.diastolic} mmHg")
                VitalsCompactPill(Icons.Default.MonitorHeart, "Heart Rate", "${log.heartRate} bpm")
                VitalsCompactPill(Icons.Default.Vaccines, "Sugar", "${log.bloodSugar} mg/dL")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalsCompactPill(Icons.Default.Scale, "Weight", "${log.weight} kg")
                VitalsCompactPill(Icons.Default.LocalDrink, "Water", "${log.waterIntakeMl} ml")
                VitalsCompactPill(Icons.Default.Bedtime, "Sleep", "${log.sleepHours} hrs")
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: ${log.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VitalsCompactPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddHealthLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (HealthLog) -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var systolic by remember { mutableStateOf("120") }
    var diastolic by remember { mutableStateOf("80") }
    var heartRate by remember { mutableStateOf("72") }
    var bloodSugar by remember { mutableStateOf("95") }
    var weight by remember { mutableStateOf("65.0") }
    var waterIntake by remember { mutableStateOf("2000") }
    var sleepHours by remember { mutableStateOf("7.5") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Health Vitals", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = systolic,
                            onValueChange = { systolic = it },
                            label = { Text("BP Systolic") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = diastolic,
                            onValueChange = { diastolic = it },
                            label = { Text("BP Diastolic") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heartRate,
                            onValueChange = { heartRate = it },
                            label = { Text("Heart Rate (bpm)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bloodSugar,
                            onValueChange = { bloodSugar = it },
                            label = { Text("Blood Sugar (mg/dL)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = waterIntake,
                            onValueChange = { waterIntake = it },
                            label = { Text("Water (ml)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = sleepHours,
                        onValueChange = { sleepHours = it },
                        label = { Text("Sleep Duration (hours)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Daily notes (e.g. 30min walk)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val log = HealthLog(
                        date = today,
                        systolic = systolic.toIntOrNull() ?: 120,
                        diastolic = diastolic.toIntOrNull() ?: 80,
                        heartRate = heartRate.toIntOrNull() ?: 72,
                        bloodSugar = bloodSugar.toIntOrNull() ?: 95,
                        weight = weight.toDoubleOrNull() ?: 65.0,
                        waterIntakeMl = waterIntake.toIntOrNull() ?: 2000,
                        sleepHours = sleepHours.toDoubleOrNull() ?: 7.5,
                        notes = notes
                    )
                    onConfirm(log)
                }
            ) {
                Text("Save Reading")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddMedicineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 tablet") }
    var timeSlot by remember { mutableStateOf("08:00 AM (Morning)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medicine Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine / Vitamin Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g. 500mg, 1 tablet)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text("Time Schedule") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, dosage, timeSlot)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add Reminder")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
