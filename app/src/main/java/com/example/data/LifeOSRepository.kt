package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LifeOSRepository(private val dao: LifeOSDao) {

    val allPlannerItems: Flow<List<PlannerItem>> = dao.getAllPlannerItems()
    val allGoals: Flow<List<GoalItem>> = dao.getAllGoals()
    val allDocuments: Flow<List<DocumentItem>> = dao.getAllDocuments()
    val allEmergencyContacts: Flow<List<EmergencyContact>> = dao.getAllEmergencyContacts()
    val allChatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages()

    suspend fun initializeSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentPlanner = dao.getAllPlannerItems().first()
        if (currentPlanner.isEmpty()) {
            // Seed Planner
            dao.insertPlannerItem(
                PlannerItem(
                    title = "Morning High-Priority Workout & Meditation",
                    category = "Health",
                    priority = "High",
                    timeLabel = "07:30 AM",
                    isCompleted = true,
                    notes = "30 mins cardio + 10 mins mindfulness breathwork."
                )
            )
            dao.insertPlannerItem(
                PlannerItem(
                    title = "Review Weekly Goals & Project Milestones",
                    category = "Work",
                    priority = "High",
                    timeLabel = "09:00 AM",
                    isCompleted = false,
                    notes = "Check LifeOS Goal tracker & organize priority sprints."
                )
            )
            dao.insertPlannerItem(
                PlannerItem(
                    title = "AI Schedule Optimization & Task Breakdown",
                    category = "Personal",
                    priority = "Medium",
                    timeLabel = "11:30 AM",
                    isCompleted = false,
                    notes = "Ask LifeOS AI Assistant to summarize pending items."
                )
            )
            dao.insertPlannerItem(
                PlannerItem(
                    title = "Evening Reflection & Digital Vault Backup",
                    category = "Habit",
                    priority = "Low",
                    timeLabel = "08:00 PM",
                    isCompleted = false,
                    notes = "Log day reflection note in LifeOS Documents."
                )
            )

            // Seed Goals
            dao.insertGoal(
                GoalItem(
                    title = "Read 12 Non-Fiction Books",
                    category = "Learning",
                    currentProgress = 8,
                    targetDays = 12,
                    deadline = "Dec 2026"
                )
            )
            dao.insertGoal(
                GoalItem(
                    title = "30-Day Morning Cardio Streak",
                    category = "Fitness",
                    currentProgress = 19,
                    targetDays = 30,
                    deadline = "Aug 2026"
                )
            )
            dao.insertGoal(
                GoalItem(
                    title = "Master Kotlin & Jetpack Compose",
                    category = "Skill",
                    currentProgress = 24,
                    targetDays = 30,
                    deadline = "Ongoing"
                )
            )

            // Seed Documents
            dao.insertDocument(
                DocumentItem(
                    title = "Personal Emergency Protocol & ICE Notes",
                    category = "Medical",
                    content = "Blood Type: O+. Primary Contact: Sarah Connor (Sister). Allergies: Penicillin. Health Insurance Policy ID: POL-99281-US.",
                    isEncrypted = true,
                    tags = "medical,ice,vault",
                    dateUpdated = "Jul 2026"
                )
            )
            dao.insertDocument(
                DocumentItem(
                    title = "LifeOS AI Operating System Vision & Notes",
                    category = "Notes",
                    content = "LifeOS AI unifies daily schedule planning, AI assistance, long-term goals tracking, encrypted document storage, and immediate emergency response into one personal OS.",
                    isEncrypted = false,
                    tags = "lifeos,vision,notes",
                    dateUpdated = "Jul 2026"
                )
            )

            // Seed Emergency Contacts
            dao.insertEmergencyContact(
                EmergencyContact(
                    name = "Sarah Connor",
                    relation = "Sister (Primary ICE)",
                    phone = "+1 (555) 019-2834",
                    bloodType = "O+",
                    isPrimary = true,
                    medicalNotes = "Allergic to Penicillin. Primary ICE contact."
                )
            )
            dao.insertEmergencyContact(
                EmergencyContact(
                    name = "Dr. Robert Vance",
                    relation = "Primary Physician",
                    phone = "+1 (555) 948-1102",
                    bloodType = "O+",
                    isPrimary = false,
                    medicalNotes = "City General Hospital Clinic, Suite 402."
                )
            )

            // Seed Chat Messages
            dao.insertChatMessage(
                ChatMessage(
                    sender = "ai",
                    text = "Hello Alex! I am your LifeOS AI Assistant. I can help organize your daily schedule, track goals, break down complex tasks, and summarize notes. How can I assist you today?"
                )
            )
        }
    }

    // --- Planner Actions ---
    suspend fun insertPlannerItem(item: PlannerItem) = dao.insertPlannerItem(item)
    suspend fun updatePlannerItem(item: PlannerItem) = dao.updatePlannerItem(item)
    suspend fun deletePlannerItem(id: Int) = dao.deletePlannerItem(id)

    // --- Goal Actions ---
    suspend fun insertGoal(goal: GoalItem) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalItem) = dao.updateGoal(goal)
    suspend fun deleteGoal(id: Int) = dao.deleteGoal(id)

    // --- Document Actions ---
    suspend fun insertDocument(doc: DocumentItem) = dao.insertDocument(doc)
    suspend fun updateDocument(doc: DocumentItem) = dao.updateDocument(doc)
    suspend fun deleteDocument(id: Int) = dao.deleteDocument(id)

    // --- Emergency Actions ---
    suspend fun insertEmergencyContact(contact: EmergencyContact) = dao.insertEmergencyContact(contact)
    suspend fun deleteEmergencyContact(id: Int) = dao.deleteEmergencyContact(id)

    // --- Chat Actions ---
    suspend fun insertChatMessage(msg: ChatMessage) = dao.insertChatMessage(msg)
    suspend fun clearChatHistory() = dao.clearChatHistory()
}
