package com.example.data.seed

import com.example.data.model.Doctor

object SeedDoctors {
    val doctors: List<Doctor> = listOf(
        Doctor(
            id = "doc_1",
            name = "Dr. Rajesh Sharma, MD",
            specialty = "Cardiologist",
            fee = 800,
            rating = 4.9,
            experienceYears = 16,
            hospital = "Apex Heart & Vascular Institute, New Delhi",
            bio = "Specializing in preventive cardiology, hypertension, lipid management, and echocardiography.",
            availableSlots = listOf("09:30 AM", "11:00 AM", "02:30 PM", "04:30 PM", "06:00 PM")
        ),
        Doctor(
            id = "doc_2",
            name = "Dr. Ananya Sen, MBBS, MD",
            specialty = "General Physician",
            fee = 500,
            rating = 4.8,
            experienceYears = 12,
            hospital = "CareMax Multi-Speciality Clinic, Mumbai",
            bio = "Dedicated to comprehensive adult healthcare, seasonal viral infections, diabetes control, and lifestyle medicine.",
            availableSlots = listOf("10:00 AM", "11:30 AM", "01:00 PM", "03:30 PM", "05:00 PM")
        ),
        Doctor(
            id = "doc_3",
            name = "Dr. Priya Deshmukh, MD, DNB",
            specialty = "Dermatologist",
            fee = 650,
            rating = 4.9,
            experienceYears = 11,
            hospital = "DermaGlow Skin & Laser Center, Bengaluru",
            bio = "Expert in medical dermatology, acne management, chronic eczema, hair loss, and cosmetic laser solutions.",
            availableSlots = listOf("10:30 AM", "12:00 PM", "03:00 PM", "04:30 PM")
        ),
        Doctor(
            id = "doc_4",
            name = "Dr. Vikramaditya Verma, MD, DCH",
            specialty = "Pediatrician",
            fee = 600,
            rating = 4.9,
            experienceYears = 14,
            hospital = "Little Angels Children's Hospital, Pune",
            bio = "Child health specialist with deep expertise in neonatal wellness, pediatric immunizations, and asthma care.",
            availableSlots = listOf("09:00 AM", "10:30 AM", "12:00 PM", "04:00 PM", "05:30 PM")
        ),
        Doctor(
            id = "doc_5",
            name = "Dr. Sunita Kulkarni, MS, DNB",
            specialty = "Gynecologist",
            fee = 750,
            rating = 4.8,
            experienceYears = 18,
            hospital = "Mother & Child Wellness Hospital, Hyderabad",
            bio = "Experienced in obstetrics, high-risk pregnancy management, PCOS, menstrual disorders, and menopausal care.",
            availableSlots = listOf("11:00 AM", "01:30 PM", "03:00 PM", "05:30 PM")
        ),
        Doctor(
            id = "doc_6",
            name = "Dr. Amit Roy, MS (Ortho)",
            specialty = "Orthopedic",
            fee = 700,
            rating = 4.7,
            experienceYears = 15,
            hospital = "Bone & Joint Excellence Clinic, Kolkata",
            bio = "Specializing in arthritis management, sports injuries, joint replacement rehab, and spinal wellness.",
            availableSlots = listOf("09:30 AM", "11:00 AM", "02:00 PM", "04:00 PM")
        ),
        Doctor(
            id = "doc_7",
            name = "Dr. Meenakshi Iyer, MD, DM",
            specialty = "Neurologist",
            fee = 950,
            rating = 4.9,
            experienceYears = 20,
            hospital = "NeuroCare Research Institute, Chennai",
            bio = "Expert in migraine management, neuropathy, vertigo, seizure disorders, and sleep neurological health.",
            availableSlots = listOf("10:00 AM", "12:30 PM", "03:30 PM", "06:00 PM")
        ),
        Doctor(
            id = "doc_8",
            name = "Dr. Sameer Kapoor, MD (Psychiatry)",
            specialty = "Psychiatrist",
            fee = 900,
            rating = 4.9,
            experienceYears = 13,
            hospital = "MindBalance Clinic, Gurugram",
            bio = "Specializing in anxiety disorders, stress management, depression therapy, and sleep cycle restoration.",
            availableSlots = listOf("11:00 AM", "02:00 PM", "04:30 PM", "06:30 PM")
        )
    )
}
