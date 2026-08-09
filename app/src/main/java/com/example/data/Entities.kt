package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_items")
data class PlannerItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Personal", // Work, Health, Personal, Habit, Fitness
    val priority: String = "Medium", // High, Medium, Low
    val timeLabel: String = "09:00 AM",
    val isCompleted: Boolean = false,
    val date: String = "Today",
    val notes: String = ""
)

@Entity(tableName = "goal_items")
data class GoalItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Life",
    val currentProgress: Int = 0,
    val targetDays: Int = 30,
    val deadline: String = "30 Days",
    val isCompleted: Boolean = false
)

@Entity(tableName = "document_items")
data class DocumentItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "General", // Medical, Finance, Notes, Vault, Work
    val content: String,
    val isEncrypted: Boolean = false,
    val tags: String = "note,lifeos",
    val dateUpdated: String = "Jul 2026"
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val relation: String,
    val phone: String,
    val bloodType: String = "O+",
    val isPrimary: Boolean = false,
    val medicalNotes: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isThinking: Boolean = false
)
