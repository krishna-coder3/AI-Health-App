package com.example.ui.screens.admin

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.Doctor
import com.example.data.model.Remedy
import com.example.data.model.UserProfile
import com.example.data.repository.HealthRepository
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthSuccess
import com.example.ui.theme.HealthTealPrimary
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    repository: HealthRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val allAppointments by repository.observeAllAppointments().collectAsState(initial = emptyList())
    val remedies by repository.observeRemedies().collectAsState(initial = emptyList())
    val doctors by repository.observeDoctors().collectAsState(initial = emptyList())
    val appSettings by repository.observeAppSettings().collectAsState(initial = AppSettings())

    var allUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Overview, 1: Remedies, 2: Doctors, 3: Appointments, 4: Users, 5: Broadcast, 6: Settings

    var showAddRemedyDialog by remember { mutableStateOf(false) }
    var showAddDoctorDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        allUsers = repository.getAllUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_screen")
    ) {
        // Admin header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = HealthTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Owner Admin Control Panel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "System Administration & Health Catalog Management",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section Tabs
        val tabs = listOf("Overview", "Remedies", "Doctors", "Appointments", "Users", "Announce", "Settings")
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = HealthTealPrimary,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == index) HealthTealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        when (selectedSection) {
            0 -> {
                // Overview metrics
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("Platform Analytics", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminMetricCard("Total Users", "${allUsers.size.coerceAtLeast(1)}", Icons.Default.People, Modifier.weight(1f))
                            AdminMetricCard("Appointments", "${allAppointments.size}", Icons.Default.CheckCircle, Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AdminMetricCard("Active Remedies", "${remedies.size}", Icons.Default.LocalHospital, Modifier.weight(1f))
                            AdminMetricCard("Doctors Available", "${doctors.size}", Icons.Default.LocalHospital, Modifier.weight(1f))
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Popular Features Usage", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("1. AI Health Assistant (Chat & Emergency Triage): 84%", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("2. Home Remedies & Desi Nuskhe Library: 72%", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("3. Health Tracker (BP, HR, Sugar & Water): 65%", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("4. Doctor Consultation Appointments: 48%", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Manage Remedies
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Manage Remedies (${remedies.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = { showAddRemedyDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Remedy")
                            }
                        }
                    }

                    items(remedies) { remedy ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(remedy.nameEn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("${remedy.nameHi} • ${remedy.category}", style = MaterialTheme.typography.bodySmall, color = HealthTealPrimary)
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repository.deleteRemedy(remedy.id)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HealthEmergencyRed)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Manage Doctors
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Manage Doctors (${doctors.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = { showAddDoctorDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Doctor")
                            }
                        }
                    }

                    items(doctors) { doc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(doc.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("${doc.specialty} • Fee: ₹${doc.fee} • Exp: ${doc.experienceYears} yrs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repository.deleteDoctor(doc.id)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = HealthEmergencyRed)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // All Appointments
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("All Patient Appointments (${allAppointments.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }

                    items(allAppointments) { appt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(appt.doctorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text("Patient: ${appt.userName.ifBlank { appt.userEmail }}", style = MaterialTheme.typography.bodySmall)
                                        Text("${appt.date} at ${appt.timeSlot}", style = MaterialTheme.typography.bodySmall, color = HealthTealPrimary)
                                    }
                                    Text(appt.status.uppercase(), fontWeight = FontWeight.Bold, color = HealthTealPrimary, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { scope.launch { repository.updateAppointmentStatus(appt.id, "completed") } },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Mark Complete", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { scope.launch { repository.updateAppointmentStatus(appt.id, "cancelled") } },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // Users list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Registered Users (${allUsers.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }

                    items(allUsers) { usr ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(usr.displayName.ifBlank { usr.email }, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text(usr.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Role: ${usr.role} • Status: ${if (usr.isBlocked) "Blocked" else "Active"}", fontSize = 11.sp, color = if (usr.isBlocked) HealthEmergencyRed else HealthSuccess)
                                }

                                if (usr.role != "owner") {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.setUserBlockedStatus(usr.userId, !usr.isBlocked)
                                                allUsers = repository.getAllUsers()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (usr.isBlocked) HealthSuccess else MaterialTheme.colorScheme.errorContainer
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (usr.isBlocked) "Unblock" else "Block", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            5 -> {
                // Send Announcement
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Broadcast Health Tip or Announcement", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showBroadcastDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Announcement")
                            }
                        }
                    }
                }
            }

            6 -> {
                // Global App Settings
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Operational Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("AI Health Assistant Engine", fontWeight = FontWeight.SemiBold)
                                    Text("Enable or pause Gemini model inferences", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = appSettings.aiEnabled,
                                    onCheckedChange = { newVal ->
                                        scope.launch {
                                            repository.updateAppSettings(appSettings.copy(aiEnabled = newVal))
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("System Maintenance Mode", fontWeight = FontWeight.SemiBold)
                                    Text("Show maintenance alert to normal users", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = appSettings.maintenanceMode,
                                    onCheckedChange = { newVal ->
                                        scope.launch {
                                            repository.updateAppSettings(appSettings.copy(maintenanceMode = newVal))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddRemedyDialog) {
        AddRemedyDialog(
            onDismiss = { showAddRemedyDialog = false },
            onConfirm = { remedy ->
                scope.launch {
                    repository.addRemedy(remedy)
                    showAddRemedyDialog = false
                }
            }
        )
    }

    if (showAddDoctorDialog) {
        AddDoctorDialog(
            onDismiss = { showAddDoctorDialog = false },
            onConfirm = { doc ->
                scope.launch {
                    repository.addDoctor(doc)
                    showAddDoctorDialog = false
                }
            }
        )
    }

    if (showBroadcastDialog) {
        BroadcastDialog(
            onDismiss = { showBroadcastDialog = false },
            onConfirm = { title, content ->
                scope.launch {
                    repository.publishAnnouncement(title, content)
                    showBroadcastDialog = false
                }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = HealthTealPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AddRemedyDialog(
    onDismiss: () -> Unit,
    onConfirm: (Remedy) -> Unit
) {
    var nameEn by remember { mutableStateOf("") }
    var nameHi by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cold and Cough") }
    var ingredients by remember { mutableStateOf("Ginger, Honey, Water") }
    var method by remember { mutableStateOf("Boil ginger in water. Add honey.") }
    var benefits by remember { mutableStateOf("Relieves cough and throat pain") }
    var doctorNote by remember { mutableStateOf("Consult doctor if symptoms persist beyond 10 days.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Home Remedy", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("English Name") }) }
                item { OutlinedTextField(value = nameHi, onValueChange = { nameHi = it }, label = { Text("Hindi Name (Devanagari)") }) }
                item { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }) }
                item { OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Ingredients (comma separated)") }) }
                item { OutlinedTextField(value = method, onValueChange = { method = it }, label = { Text("Method steps") }) }
                item { OutlinedTextField(value = benefits, onValueChange = { benefits = it }, label = { Text("Benefits (comma separated)") }) }
                item { OutlinedTextField(value = doctorNote, onValueChange = { doctorNote = it }, label = { Text("Doctor Note") }) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = Remedy(
                        nameEn = nameEn,
                        nameHi = nameHi,
                        category = category,
                        ingredients = ingredients.split(",").map { it.trim() },
                        method = method.split(".").filter { it.isNotBlank() }.map { it.trim() },
                        benefits = benefits.split(",").map { it.trim() },
                        doctorNote = doctorNote,
                        isDesi = true
                    )
                    onConfirm(r)
                },
                enabled = nameEn.isNotBlank() && doctorNote.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddDoctorDialog(
    onDismiss: () -> Unit,
    onConfirm: (Doctor) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("General Physician") }
    var fee by remember { mutableStateOf("500") }
    var hospital by remember { mutableStateOf("City Medical Center") }
    var bio by remember { mutableStateOf("Experienced doctor") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Doctor", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Doctor Name") })
                OutlinedTextField(value = specialty, onValueChange = { specialty = it }, label = { Text("Specialty") })
                OutlinedTextField(value = fee, onValueChange = { fee = it }, label = { Text("Consultation Fee (₹)") })
                OutlinedTextField(value = hospital, onValueChange = { hospital = it }, label = { Text("Hospital / Clinic") })
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val d = Doctor(
                        name = name,
                        specialty = specialty,
                        fee = fee.toIntOrNull() ?: 500,
                        hospital = hospital,
                        bio = bio,
                        availableSlots = listOf("10:00 AM", "12:00 PM", "03:00 PM", "05:00 PM")
                    )
                    onConfirm(d)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save Doctor")
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun BroadcastDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Announcement", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Message Content") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, content) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Send Broadcast")
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
