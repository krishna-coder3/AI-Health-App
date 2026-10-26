package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.HealthLog
import com.example.data.repository.HealthRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class HealthRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun testUnauthenticatedWriteFails() = runBlocking {
        auth.signOut()
        val repository = HealthRepository(firestore)

        try {
            repository.addHealthLog(
                HealthLog(
                    date = "2026-10-26",
                    systolic = 120,
                    diastolic = 80
                )
            )
            fail("Expected unauthenticated write to throw an exception")
        } catch (e: IllegalStateException) {
            // Expected requireUserId() failure
            assertTrue(e.message!!.contains("User must be signed in"))
        }
    }

    @Test
    fun testAuthenticatedUserCanSaveAndFetchHealthLog() = runBlocking {
        val uid = signInTestUser("testuser@example.com")
        val repository = HealthRepository(firestore)

        repository.addHealthLog(
            HealthLog(
                userId = uid,
                date = "2026-10-26",
                systolic = 118,
                diastolic = 78,
                heartRate = 70,
                bloodSugar = 92
            )
        )

        val profile = repository.getOrCreateUserProfile()
        assertNotNull(profile)
        assertEquals("user", profile.role)
    }
}
