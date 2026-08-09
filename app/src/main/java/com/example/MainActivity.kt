package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNav
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AIChatScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.EmergencyScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HealthScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.LifeOSTheme
import com.example.viewmodel.LifeOSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: LifeOSViewModel = viewModel()

            val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
            val userName by viewModel.userName.collectAsStateWithLifecycle()
            val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
            val isEmailVerified by viewModel.isEmailVerified.collectAsStateWithLifecycle()
            val authProvider by viewModel.authProvider.collectAsStateWithLifecycle()

            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()

            var showAuthModalScreen by remember { mutableStateOf(false) }

            val plannerItems by viewModel.plannerItems.collectAsStateWithLifecycle()
            val goals by viewModel.goals.collectAsStateWithLifecycle()
            val documents by viewModel.documents.collectAsStateWithLifecycle()
            val emergencyContacts by viewModel.emergencyContacts.collectAsStateWithLifecycle()
            val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

            val tabTitles = listOf(
                "LifeOS Dashboard",
                "Daily Organizer",
                "LifeOS AI Assistant",
                "Goals & Habits",
                "Vault & Notes",
                "Emergency Hub",
                "Settings & Profile",
                "LifeOS AI SaaS Showcase",
                "Finance Dashboard",
                "Health & Vitals Tracker",
                "Admin Console & Analytics"
            )

            val currentTitle = tabTitles.getOrElse(selectedTab) { "LifeOS AI" }

            LifeOSTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopNavBar(
                            title = currentTitle,
                            userName = userName,
                            focusScore = if (plannerItems.isNotEmpty()) {
                                ((plannerItems.count { it.isCompleted }.toFloat() / plannerItems.size) * 100).toInt()
                            } else 88,
                            onAiClick = { viewModel.setSelectedTab(2) },
                            onProfileClick = { viewModel.setSelectedTab(6) }
                        )
                    },
                    bottomBar = {
                        BottomNav(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.setSelectedTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Crossfade(
                            targetState = selectedTab,
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 900.dp),
                            label = "TabScreenTransition"
                        ) { targetTab ->
                            when (targetTab) {
                                0 -> DashboardScreen(
                                    userName = userName,
                                    plannerItems = plannerItems,
                                    goals = goals,
                                    onTogglePlannerItem = { viewModel.togglePlannerItemCompleted(it) },
                                    onNavigateTab = { viewModel.setSelectedTab(it) },
                                    onQuickAiPrompt = { viewModel.sendChatMessage(it) },
                                    onEmergencyCall = {
                                        val primaryPhone = emergencyContacts.firstOrNull { c -> c.isPrimary }?.phone ?: "911"
                                        launchPhoneDialer(primaryPhone)
                                    }
                                )

                                1 -> PlannerScreen(
                                    plannerItems = plannerItems,
                                    onToggleCompleted = { viewModel.togglePlannerItemCompleted(it) },
                                    onAddTask = { title, cat, prio, time, notes ->
                                        viewModel.addPlannerItem(title, cat, prio, time, notes)
                                    },
                                    onDeleteTask = { viewModel.deletePlannerItem(it) },
                                    onAiBreakdownTask = { viewModel.aiBreakdownTaskAndAddSubtasks(it) },
                                    onSuggestDailyTasks = { viewModel.aiSuggestDailyTasks() },
                                    onOptimizeSchedule = { viewModel.aiOptimizeSchedule() }
                                )

                                2 -> AIChatScreen(
                                    messages = chatMessages,
                                    isThinking = isAiThinking,
                                    onSendMessage = { viewModel.sendChatMessage(it) },
                                    onClearChat = { viewModel.clearChatHistory() },
                                    onGeneratePlan = { viewModel.aiGeneratePlan(it) },
                                    onSummarizeText = { viewModel.aiSummarizeText(it) },
                                    onTranslateText = { text, lang -> viewModel.aiTranslateText(text, lang) },
                                    onAnswerQuestion = { viewModel.aiAnswerQuestion(it) },
                                    onSuggestDailyTasks = { viewModel.aiSuggestDailyTasks() },
                                    onOptimizeSchedule = { viewModel.aiOptimizeSchedule() }
                                )

                                3 -> GoalsScreen(
                                    goals = goals,
                                    onIncrementProgress = { viewModel.incrementGoalProgress(it) },
                                    onAddGoal = { title, cat, days, deadline ->
                                        viewModel.addGoal(title, cat, days, deadline)
                                    },
                                    onDeleteGoal = { viewModel.deleteGoal(it) }
                                )

                                4 -> DocumentsScreen(
                                    documents = documents,
                                    onAddDocument = { title, cat, content, enc, tags ->
                                        viewModel.addDocument(title, cat, content, enc, tags)
                                    },
                                    onDeleteDocument = { viewModel.deleteDocument(it) },
                                    onAiSummarizeDocument = { viewModel.aiSummarizeDocument(it) },
                                    onAiTranslateDocument = { doc, lang -> viewModel.aiTranslateDocument(doc, lang) }
                                )

                                5 -> EmergencyScreen(
                                    contacts = emergencyContacts,
                                    onCallContact = { launchPhoneDialer(it) },
                                    onAddContact = { name, rel, phone, blood, primary, notes ->
                                        viewModel.addEmergencyContact(name, rel, phone, blood, primary, notes)
                                    },
                                    onDeleteContact = { viewModel.deleteEmergencyContact(it) }
                                )

                                6 -> SettingsScreen(
                                    userName = userName,
                                    userEmail = userEmail,
                                    isLoggedIn = isLoggedIn,
                                    isEmailVerified = isEmailVerified,
                                    authProvider = authProvider,
                                    isDarkTheme = isDarkTheme,
                                    onUpdateUserName = { viewModel.updateUserName(it) },
                                    onToggleDarkTheme = { viewModel.toggleDarkTheme(it) },
                                    onResetSeedData = {
                                        viewModel.addPlannerItem("Review LifeOS Tasks", "Work", "High", "09:00 AM", "Sample task")
                                    },
                                    onLogout = { viewModel.logoutUser() },
                                    onSendVerification = { viewModel.toggleEmailVerification(true) },
                                    onShowAuthModal = { showAuthModalScreen = true }
                                )

                                7 -> LandingScreen(
                                    onNavigateToApp = { viewModel.setSelectedTab(0) },
                                    onTryAiPrompt = {
                                        viewModel.sendChatMessage(it)
                                        viewModel.setSelectedTab(2)
                                    }
                                )

                                8 -> FinanceScreen(
                                    onNavigateTab = { viewModel.setSelectedTab(it) }
                                )

                                9 -> HealthScreen(
                                    onNavigateTab = { viewModel.setSelectedTab(it) }
                                )

                                10 -> AdminScreen(
                                    onNavigateTab = { viewModel.setSelectedTab(it) }
                                )
                            }
                        }

                        if (showAuthModalScreen || !isLoggedIn) {
                            AuthScreen(
                                onLoginSuccess = { name, email, provider ->
                                    viewModel.loginUser(name, email, provider)
                                    showAuthModalScreen = false
                                },
                                onContinueAsGuest = {
                                    viewModel.loginUser("Guest User", "guest@lifeos.local", "Offline Guest Mode")
                                    showAuthModalScreen = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun launchPhoneDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            // Fallback if no phone dialer app is present
        }
    }
}
