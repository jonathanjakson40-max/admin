package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FactMasteryEntity::class, QuizHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MultiplyDatabase : RoomDatabase() {
    abstract fun factDao(): FactDao

    companion object {
        @Volatile
        private var INSTANCE: MultiplyDatabase? = null

        fun getDatabase(context: Context): MultiplyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MultiplyDatabase::class.java,
                    "multiply_master_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
