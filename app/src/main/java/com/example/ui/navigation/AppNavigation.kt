package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.ai.AiHealthService
import com.example.data.repository.HealthRepository
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.assistant.AiAssistantScreen
import com.example.ui.screens.doctors.DoctorBookingScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.remedies.RemediesScreen
import com.example.ui.screens.reports.ReportSummaryScreen
import com.example.ui.screens.symptomchecker.SymptomCheckerScreen
import com.example.ui.theme.HealthTealPrimary

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    REMEDIES("Remedies", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    TRACKER("Tracker", Icons.Filled.Speed, Icons.Outlined.Speed),
    APPOINTMENTS("Doctors", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

enum class SubScreen {
    NONE,
    AI_ASSISTANT,
    SYMPTOM_CHECKER,
    MEDICAL_REPORTS,
    ADMIN_PANEL
}

@Composable
fun AppNavigation(
    repository: HealthRepository,
    aiService: AiHealthService,
    onSignOutComplete: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    // Hardware/System back button handling
    BackHandler(enabled = currentSubScreen != SubScreen.NONE || currentDestination != AppDestination.HOME) {
        if (currentSubScreen != SubScreen.NONE) {
            currentSubScreen = SubScreen.NONE
        } else if (currentDestination != AppDestination.HOME) {
            currentDestination = AppDestination.HOME
        }
    }

    Scaffold(
        bottomBar = {
            if (currentSubScreen == SubScreen.NONE) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    AppDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = HealthTealPrimary
                            ),
                            modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentSubScreen) {
                SubScreen.AI_ASSISTANT -> {
                    AiAssistantScreen(
                        repository = repository,
                        aiService = aiService
                    )
                }

                SubScreen.SYMPTOM_CHECKER -> {
                    SymptomCheckerScreen(
                        repository = repository,
                        aiService = aiService
                    )
                }

                SubScreen.MEDICAL_REPORTS -> {
                    ReportSummaryScreen(
                        repository = repository,
                        aiService = aiService
                    )
                }

                SubScreen.ADMIN_PANEL -> {
                    AdminScreen(
                        repository = repository,
                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                    )
                }

                SubScreen.NONE -> {
                    when (currentDestination) {
                        AppDestination.HOME -> {
                            HomeScreen(
                                repository = repository,
                                onNavigateToAssistant = { currentSubScreen = SubScreen.AI_ASSISTANT },
                                onNavigateToRemedies = { currentDestination = AppDestination.REMEDIES },
                                onNavigateToTracker = { currentDestination = AppDestination.TRACKER },
                                onNavigateToSymptomChecker = { currentSubScreen = SubScreen.SYMPTOM_CHECKER },
                                onNavigateToReports = { currentSubScreen = SubScreen.MEDICAL_REPORTS },
                                onNavigateToAppointments = { currentDestination = AppDestination.APPOINTMENTS },
                                onNavigateToAdmin = { currentSubScreen = SubScreen.ADMIN_PANEL }
                            )
                        }

                        AppDestination.REMEDIES -> {
                            RemediesScreen(repository = repository)
                        }

                        AppDestination.TRACKER -> {
                            com.example.ui.screens.tracker.HealthTrackerScreen(repository = repository)
                        }

                        AppDestination.APPOINTMENTS -> {
                            DoctorBookingScreen(repository = repository)
                        }

                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                repository = repository,
                                onSignOutComplete = onSignOutComplete,
                                onNavigateToAdmin = { currentSubScreen = SubScreen.ADMIN_PANEL }
                            )
                        }
                    }
                }
            }
        }
    }
}
