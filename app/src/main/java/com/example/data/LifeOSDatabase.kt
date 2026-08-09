package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlannerItem::class,
        GoalItem::class,
        DocumentItem::class,
        EmergencyContact::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOSDatabase : RoomDatabase() {
    abstract fun dao(): LifeOSDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOSDatabase? = null

        fun getDatabase(context: Context): LifeOSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOSDatabase::class.java,
                    "lifeos_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
