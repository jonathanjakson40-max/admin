package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fact_mastery")
data class FactMasteryEntity(
    @PrimaryKey
    val id: String, // e.g. "7x8"
    val factorA: Int,
    val factorB: Int,
    val correctCount: Int = 0,
    val attemptCount: Int = 0,
    val currentStreak: Int = 0,
    val lastPracticedTime: Long = 0L,
    val masteryLevel: Int = 0 // 0 = New, 1 = Learning, 2 = Practiced, 3 = Mastered
) {
    val accuracy: Float
        get() = if (attemptCount > 0) (correctCount.toFloat() / attemptCount.toFloat()) * 100f else 0f
}

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mode: String, // "SPRINT", "TIME_ATTACK", "MISSING_FACTOR"
    val score: Int,
    val totalQuestions: Int,
    val accuracy: Float,
    val durationSeconds: Int,
    val bestStreak: Int,
    val timestamp: Long = System.currentTimeMillis()
)
