package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeOSDao {
    // --- Planner Queries ---
    @Query("SELECT * FROM planner_items ORDER BY isCompleted ASC, id DESC")
    fun getAllPlannerItems(): Flow<List<PlannerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannerItem(item: PlannerItem): Long

    @Update
    suspend fun updatePlannerItem(item: PlannerItem)

    @Query("DELETE FROM planner_items WHERE id = :id")
    suspend fun deletePlannerItem(id: Int)

    // --- Goal Queries ---
    @Query("SELECT * FROM goal_items ORDER BY id DESC")
    fun getAllGoals(): Flow<List<GoalItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalItem): Long

    @Update
    suspend fun updateGoal(goal: GoalItem)

    @Query("DELETE FROM goal_items WHERE id = :id")
    suspend fun deleteGoal(id: Int)

    // --- Document Queries ---
    @Query("SELECT * FROM document_items ORDER BY id DESC")
    fun getAllDocuments(): Flow<List<DocumentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentItem): Long

    @Update
    suspend fun updateDocument(doc: DocumentItem)

    @Query("DELETE FROM document_items WHERE id = :id")
    suspend fun deleteDocument(id: Int)

    // --- Emergency Contacts ---
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, id ASC")
    fun getAllEmergencyContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyContact(contact: EmergencyContact): Long

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteEmergencyContact(id: Int)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
