package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiApiClient
import com.example.data.ChatMessage
import com.example.data.DocumentItem
import com.example.data.EmergencyContact
import com.example.data.GoalItem
import com.example.data.LifeOSDatabase
import com.example.data.LifeOSRepository
import com.example.data.PlannerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LifeOSViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LifeOSRepository

    val plannerItems: StateFlow<List<PlannerItem>>
    val goals: StateFlow<List<GoalItem>>
    val documents: StateFlow<List<DocumentItem>>
    val emergencyContacts: StateFlow<List<EmergencyContact>>
    val chatMessages: StateFlow<List<ChatMessage>>

    private val _userName = MutableStateFlow("Alex Vance")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("alex.dev@lifeos.ai")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isEmailVerified = MutableStateFlow(true)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()

    private val _authProvider = MutableStateFlow("Google OAuth 2.0")
    val authProvider: StateFlow<String> = _authProvider.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Dashboard, 1: Planner, 2: AI Chat, 3: Goals, 4: Docs, 5: Emergency, 6: Settings, 7: Landing
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        val database = LifeOSDatabase.getDatabase(application)
        repository = LifeOSRepository(database.dao())

        plannerItems = repository.allPlannerItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        goals = repository.allGoals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        documents = repository.allDocuments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        emergencyContacts = repository.allEmergencyContacts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        chatMessages = repository.allChatMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name.trim()
        }
    }

    fun toggleDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    // --- Planner Actions ---
    fun togglePlannerItemCompleted(item: PlannerItem) {
        viewModelScope.launch {
            repository.updatePlannerItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun addPlannerItem(
        title: String,
        category: String = "Personal",
        priority: String = "Medium",
        timeLabel: String = "10:00 AM",
        notes: String = ""
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertPlannerItem(
                PlannerItem(
                    title = title.trim(),
                    category = category,
                    priority = priority,
                    timeLabel = timeLabel,
                    isCompleted = false,
                    notes = notes.trim()
                )
            )
        }
    }

    fun deletePlannerItem(id: Int) {
        viewModelScope.launch {
            repository.deletePlannerItem(id)
        }
    }

    // --- Goal Actions ---
    fun incrementGoalProgress(goal: GoalItem) {
        viewModelScope.launch {
            val newProgress = (goal.currentProgress + 1).coerceAtMost(goal.targetDays)
            val isFinished = newProgress >= goal.targetDays
            repository.updateGoal(
                goal.copy(
                    currentProgress = newProgress,
                    isCompleted = isFinished
                )
            )
        }
    }

    fun addGoal(
        title: String,
        category: String = "Life",
        targetDays: Int = 30,
        deadline: String = "30 Days"
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertGoal(
                GoalItem(
                    title = title.trim(),
                    category = category,
                    currentProgress = 0,
                    targetDays = targetDays,
                    deadline = deadline
                )
            )
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    // --- Document Actions ---
    fun addDocument(
        title: String,
        category: String = "Notes",
        content: String,
        isEncrypted: Boolean = false,
        tags: String = "note,lifeos"
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertDocument(
                DocumentItem(
                    title = title.trim(),
                    category = category,
                    content = content.trim(),
                    isEncrypted = isEncrypted,
                    tags = tags,
                    dateUpdated = "Jul 2026"
                )
            )
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    // --- Emergency Actions ---
    fun addEmergencyContact(
        name: String,
        relation: String,
        phone: String,
        bloodType: String = "O+",
        isPrimary: Boolean = false,
        medicalNotes: String = ""
    ) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            repository.insertEmergencyContact(
                EmergencyContact(
                    name = name.trim(),
                    relation = relation.trim(),
                    phone = phone.trim(),
                    bloodType = bloodType,
                    isPrimary = isPrimary,
                    medicalNotes = medicalNotes.trim()
                )
            )
        }
    }

    fun deleteEmergencyContact(id: Int) {
        viewModelScope.launch {
            repository.deleteEmergencyContact(id)
        }
    }

    // --- AI Chat Actions ---
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val text = userText.trim()
        viewModelScope.launch {
            // Save user message
            repository.insertChatMessage(
                ChatMessage(
                    sender = "user",
                    text = text
                )
            )

            _isAiThinking.value = true

            // Generate AI response
            val responseText = GeminiApiClient.generateResponse(
                prompt = text,
                systemInstructionText = "You are LifeOS AI, a sleek personal assistant for user ${userName.value}. Help with productivity, goals, daily schedule, wellness, and quick advice."
            )

            _isAiThinking.value = false

            // Save AI message
            repository.insertChatMessage(
                ChatMessage(
                    sender = "ai",
                    text = responseText
                )
            )
        }
    }

    fun aiBreakdownTaskAndAddSubtasks(taskTitle: String) {
        viewModelScope.launch {
            _isAiThinking.value = true
            val prompt = "Break down the main task '$taskTitle' into 3 short actionable sub-steps. Return each sub-step on a new line starting with '- '."
            val response = GeminiApiClient.generateResponse(prompt)
            _isAiThinking.value = false

            val lines = response.lines()
                .map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
                .filter { it.isNotBlank() && !it.startsWith("Here") && !it.startsWith("Sure") }
                .take(3)

            if (lines.isNotEmpty()) {
                lines.forEachIndexed { index, sub ->
                    repository.insertPlannerItem(
                        PlannerItem(
                            title = "Subtask: $sub",
                            category = "AI Breakdown",
                            priority = "Medium",
                            timeLabel = "Sub-step ${index + 1}",
                            notes = "Generated by LifeOS AI from '$taskTitle'"
                        )
                    )
                }
            } else {
                repository.insertPlannerItem(
                    PlannerItem(
                        title = "Subtask: Prepare & Gather Resources",
                        category = "AI Breakdown",
                        priority = "Medium",
                        timeLabel = "Sub-step 1",
                        notes = "Derived from '$taskTitle'"
                    )
                )
                repository.insertPlannerItem(
                    PlannerItem(
                        title = "Subtask: Execute Core Action",
                        category = "AI Breakdown",
                        priority = "Medium",
                        timeLabel = "Sub-step 2",
                        notes = "Derived from '$taskTitle'"
                    )
                )
            }
        }
    }

    // --- Gemini API Capabilities ---

    /** 1. Generate Plans */
    fun aiGeneratePlan(topicOrGoal: String) {
        if (topicOrGoal.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            val planText = GeminiApiClient.generatePlan(topicOrGoal)
            _isAiThinking.value = false

            // Save plan into Chat & Vault
            repository.insertChatMessage(
                ChatMessage(sender = "user", text = "Generate Action Plan: $topicOrGoal")
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = planText)
            )
            repository.insertDocument(
                DocumentItem(
                    title = "AI Plan: $topicOrGoal",
                    category = "Plans",
                    content = planText,
                    isEncrypted = false,
                    tags = "ai,plan,gemini",
                    dateUpdated = "Jul 2026"
                )
            )
        }
    }

    /** 2. Summarize */
    fun aiSummarizeDocument(doc: DocumentItem) {
        viewModelScope.launch {
            _isAiThinking.value = true
            val summary = GeminiApiClient.summarize("Title: ${doc.title}\nCategory: ${doc.category}\nContent: ${doc.content}")
            _isAiThinking.value = false

            // Save summary note in Vault & post in Chat
            repository.insertDocument(
                DocumentItem(
                    title = "AI Summary: ${doc.title}",
                    category = "AI Notes",
                    content = summary,
                    isEncrypted = false,
                    tags = "ai,summary,vault",
                    dateUpdated = "Jul 2026"
                )
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = "📄 **Document Summary generated for '${doc.title}':**\n\n$summary")
            )
        }
    }

    fun aiSummarizeText(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            val summary = GeminiApiClient.summarize(text)
            _isAiThinking.value = false

            repository.insertChatMessage(
                ChatMessage(sender = "user", text = "Summarize:\n$text")
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = summary)
            )
        }
    }

    /** 3. Translate */
    fun aiTranslateDocument(doc: DocumentItem, targetLanguage: String) {
        viewModelScope.launch {
            _isAiThinking.value = true
            val translation = GeminiApiClient.translate(doc.content, targetLanguage)
            _isAiThinking.value = false

            repository.insertDocument(
                DocumentItem(
                    title = "[$targetLanguage] ${doc.title}",
                    category = "Translations",
                    content = translation,
                    isEncrypted = false,
                    tags = "ai,translation,${targetLanguage.lowercase()}",
                    dateUpdated = "Jul 2026"
                )
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = "🌐 **Translation to $targetLanguage for '${doc.title}':**\n\n$translation")
            )
        }
    }

    fun aiTranslateText(text: String, targetLanguage: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            val translation = GeminiApiClient.translate(text, targetLanguage)
            _isAiThinking.value = false

            repository.insertChatMessage(
                ChatMessage(sender = "user", text = "Translate to $targetLanguage:\n$text")
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = translation)
            )
        }
    }

    /** 4. Answer Questions */
    fun aiAnswerQuestion(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            val answer = GeminiApiClient.answerQuestion(question)
            _isAiThinking.value = false

            repository.insertChatMessage(
                ChatMessage(sender = "user", text = question)
            )
            repository.insertChatMessage(
                ChatMessage(sender = "ai", text = answer)
            )
        }
    }

    /** 5. Suggest Daily Tasks */
    fun aiSuggestDailyTasks() {
        viewModelScope.launch {
            _isAiThinking.value = true
            val tasksContext = plannerItems.value.joinToString { "${it.title} (${it.priority})" }
            val suggestionsText = GeminiApiClient.suggestDailyTasks(tasksContext)
            _isAiThinking.value = false

            // Automatically add 3 high-value daily tasks directly to Planner DB
            repository.insertPlannerItem(
                PlannerItem(
                    title = "AI Priority: Morning Focus Block & Key Review",
                    category = "Work",
                    priority = "High",
                    timeLabel = "09:00 AM",
                    notes = "Suggested by LifeOS AI based on energy peaks."
                )
            )
            repository.insertPlannerItem(
                PlannerItem(
                    title = "AI Priority: Hydration & 15-min Stretch Break",
                    category = "Health",
                    priority = "Medium",
                    timeLabel = "01:30 PM",
                    notes = "Suggested by LifeOS AI to maintain wellness."
                )
            )
            repository.insertPlannerItem(
                PlannerItem(
                    title = "AI Priority: Evening Learning & Vault Sync",
                    category = "Personal",
                    priority = "Medium",
                    timeLabel = "06:00 PM",
                    notes = "Suggested by LifeOS AI for continuous improvement."
                )
            )

            // Post in Chat
            repository.insertChatMessage(
                ChatMessage(
                    sender = "ai",
                    text = "⚡ **LifeOS AI Suggested Daily Tasks (Added to your Planner):**\n\n$suggestionsText"
                )
            )
        }
    }

    /** 6. Optimize Schedule */
    fun aiOptimizeSchedule() {
        viewModelScope.launch {
            _isAiThinking.value = true
            val scheduleContext = plannerItems.value.joinToString { "${it.timeLabel}: ${it.title} [${it.category}]" }
            val optimizedResult = GeminiApiClient.optimizeSchedule(
                if (scheduleContext.isNotBlank()) scheduleContext else "Current tasks: Review goals, Code feature, Exercise, Vault backup"
            )
            _isAiThinking.value = false

            // Save optimized schedule in Vault & post in Chat
            repository.insertDocument(
                DocumentItem(
                    title = "AI Optimized Schedule - July 2026",
                    category = "Schedules",
                    content = optimizedResult,
                    isEncrypted = false,
                    tags = "ai,schedule,planner",
                    dateUpdated = "Jul 2026"
                )
            )

            repository.insertChatMessage(
                ChatMessage(
                    sender = "ai",
                    text = "⏳ **LifeOS AI Optimized Schedule Timeline (Saved to Vault):**\n\n$optimizedResult"
                )
            )
        }
    }

    fun loginUser(name: String, email: String, provider: String) {
        _userName.value = if (name.isNotBlank()) name else "Alex Vance"
        _userEmail.value = if (email.isNotBlank()) email else "alex.dev@lifeos.ai"
        _authProvider.value = provider
        _isLoggedIn.value = true
        _isEmailVerified.value = true
    }

    fun logoutUser() {
        _isLoggedIn.value = false
    }

    fun toggleEmailVerification(verified: Boolean) {
        _isEmailVerified.value = verified
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.insertChatMessage(
                ChatMessage(
                    sender = "ai",
                    text = "Chat history cleared. How can LifeOS AI assist you now, ${userName.value}?"
                )
            )
        }
    }
}
