package com.example.ui.screens.doctors

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.repository.HealthRepository
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.theme.HealthBlueSecondary
import com.example.ui.theme.HealthEmergencyContainer
import com.example.ui.theme.HealthEmergencyRed
import com.example.ui.theme.HealthSuccess
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.HealthWarning
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DoctorBookingScreen(
    repository: HealthRepository
) {
    val doctors by repository.observeDoctors().collectAsState(initial = emptyList())
    val myAppointments by repository.observeMyAppointments().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Find Doctors, 1: My Appointments
    var selectedSpecialty by remember { mutableStateOf("All Specialties") }
    var selectedDoctorForBooking by remember { mutableStateOf<Doctor?>(null) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            repository.seedDoctorsIfEmpty()
        }
    }

    val specialties = listOf(
        "All Specialties",
        "Cardiologist",
        "General Physician",
        "Dermatologist",
        "Pediatrician",
        "Gynecologist",
        "Orthopedic",
        "Neurologist",
        "Psychiatrist"
    )

    val filteredDoctors = doctors.filter { doc ->
        selectedSpecialty == "All Specialties" || doc.specialty.equals(selectedSpecialty, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("doctor_booking_screen")
    ) {
        // Tab Header
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
                    text = { Text("Find Doctors (${doctors.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Appointments (${myAppointments.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Find Doctors
                // Specialty horizontal chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(specialties) { spec ->
                        val isSelected = selectedSpecialty == spec
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSpecialty = spec },
                            label = { Text(spec, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        MedicalDisclaimerBanner()
                    }

                    items(filteredDoctors) { doctor ->
                        DoctorCard(
                            doctor = doctor,
                            onBookClicked = { selectedDoctorForBooking = doctor }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }

            1 -> {
                // My Appointments
                if (myAppointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No appointments booked yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Browse doctors and schedule your medical consultation.",
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
                        items(myAppointments) { appt ->
                            AppointmentCard(
                                appointment = appt,
                                onCancel = {
                                    scope.launch {
                                        repository.updateAppointmentStatus(appt.id, "cancelled")
                                    }
                                },
                                onReschedule = {
                                    appointmentToReschedule = appt
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Booking Dialog
    if (selectedDoctorForBooking != null) {
        BookingModalDialog(
            doctor = selectedDoctorForBooking!!,
            onDismiss = { selectedDoctorForBooking = null },
            onConfirmBooking = { date, slot, notes ->
                scope.launch {
                    repository.bookAppointment(
                        doctorId = selectedDoctorForBooking!!.id,
                        doctorName = selectedDoctorForBooking!!.name,
                        specialty = selectedDoctorForBooking!!.specialty,
                        date = date,
                        timeSlot = slot,
                        fee = selectedDoctorForBooking!!.fee,
                        notes = notes
                    )
                    selectedDoctorForBooking = null
                    selectedTab = 1 // Switch to appointments tab
                }
            }
        )
    }

    // Reschedule Dialog
    if (appointmentToReschedule != null) {
        RescheduleModalDialog(
            appointment = appointmentToReschedule!!,
            onDismiss = { appointmentToReschedule = null },
            onConfirmReschedule = { newDate, newSlot ->
                scope.launch {
                    repository.rescheduleAppointment(
                        appointmentId = appointmentToReschedule!!.id,
                        newDate = newDate,
                        newSlot = newSlot
                    )
                    appointmentToReschedule = null
                }
            }
        )
    }
}

@Composable
private fun DoctorCard(
    doctor: Doctor,
    onBookClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("doctor_card_${doctor.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = HealthTealPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = doctor.specialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = HealthTealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = doctor.hospital,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${doctor.rating}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = doctor.bio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Consultation Fee",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${doctor.fee}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onBookClicked,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary),
                    modifier = Modifier.testTag("book_doctor_button_${doctor.id}")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Book Now")
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onCancel: () -> Unit,
    onReschedule: () -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = appointment.specialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = HealthTealPrimary
                    )
                }

                // Status chip
                Surface(
                    color = when (appointment.status) {
                        "booked" -> HealthSuccess.copy(alpha = 0.15f)
                        "rescheduled" -> HealthWarning.copy(alpha = 0.15f)
                        "cancelled" -> HealthEmergencyContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = appointment.status.replaceFirstChar { it.uppercase() },
                        color = when (appointment.status) {
                            "booked" -> HealthSuccess
                            "rescheduled" -> HealthWarning
                            "cancelled" -> HealthEmergencyRed
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = HealthTealPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(appointment.date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Schedule, contentDescription = null, tint = HealthTealPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(appointment.timeSlot, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }

            if (appointment.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${appointment.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (appointment.status != "cancelled") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReschedule,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reschedule")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthEmergencyRed)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun BookingModalDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onConfirmBooking: (String, String, String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val availableDates = remember {
        (1..5).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
            dateFormat.format(cal.time)
        }
    }

    var selectedDate by remember { mutableStateOf(availableDates.first()) }
    var selectedSlot by remember { mutableStateOf(doctor.availableSlots.firstOrNull() ?: "10:00 AM") }
    var patientNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Book Doctor Appointment", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("${doctor.name} (${doctor.specialty})", style = MaterialTheme.typography.bodyMedium, color = HealthTealPrimary)
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("1. Choose Consultation Date", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableDates) { d ->
                            val isSel = selectedDate == d
                            Surface(
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedDate = d }
                            ) {
                                Text(
                                    text = d,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text("2. Available Time Slots", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(doctor.availableSlots) { slot ->
                            val isSel = selectedSlot == slot
                            Surface(
                                color = if (isSel) HealthTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.clickable { selectedSlot = slot }
                            ) {
                                Text(
                                    text = slot,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = patientNotes,
                        onValueChange = { patientNotes = it },
                        label = { Text("Brief reason or symptoms") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Consultation Fee:", fontWeight = FontWeight.Bold)
                        Text("₹${doctor.fee}", fontWeight = FontWeight.Bold, color = HealthTealPrimary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmBooking(selectedDate, selectedSlot, patientNotes) },
                colors = ButtonDefaults.buttonColors(containerColor = HealthTealPrimary)
            ) {
                Text("Confirm Booking")
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
fun RescheduleModalDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onConfirmReschedule: (String, String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val availableDates = remember {
        (2..6).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
            dateFormat.format(cal.time)
        }
    }
    val slots = listOf("10:00 AM", "11:30 AM", "02:00 PM", "04:30 PM", "06:00 PM")

    var selectedDate by remember { mutableStateOf(availableDates.first()) }
    var selectedSlot by remember { mutableStateOf(slots.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reschedule Appointment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Doctor: ${appointment.doctorName}")
                Text("Select New Date:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableDates) { d ->
                        val isSel = selectedDate == d
                        Surface(
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { selectedDate = d }
                        ) {
                            Text(
                                text = d,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text("Select New Slot:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(slots) { s ->
                        val isSel = selectedSlot == s
                        Surface(
                            color = if (isSel) HealthTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { selectedSlot = s }
                        ) {
                            Text(
                                text = s,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmReschedule(selectedDate, selectedSlot) }) {
                Text("Update Date & Slot")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
