package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MultiplyRepository(private val factDao: FactDao) {

    val allFacts: Flow<List<FactMasteryEntity>> = factDao.getAllFactsFlow()
    val trickyFacts: Flow<List<FactMasteryEntity>> = factDao.getTrickyFactsFlow()
    val recentQuizHistory: Flow<List<QuizHistoryEntity>> = factDao.getRecentQuizHistoryFlow()
    val masteredCount: Flow<Int> = factDao.getMasteredCountFlow()

    fun getFactsForTable(tableNumber: Int): Flow<List<FactMasteryEntity>> {
        return factDao.getFactsForTableFlow(tableNumber)
    }

    fun getHighScore(mode: String): Flow<Int?> {
        return factDao.getHighScoreFlow(mode)
    }

    suspend fun ensureInitialized() {
        val count = factDao.getCount()
        if (count == 0) {
            val initialFacts = mutableListOf<FactMasteryEntity>()
            for (a in 1..12) {
                for (b in 1..12) {
                    initialFacts.add(
                        FactMasteryEntity(
                            id = "${a}x${b}",
                            factorA = a,
                            factorB = b,
                            correctCount = 0,
                            attemptCount = 0,
                            currentStreak = 0,
                            lastPracticedTime = 0L,
                            masteryLevel = 0
                        )
                    )
                }
            }
            factDao.insertAll(initialFacts)
        }
    }

    suspend fun recordAttempt(factorA: Int, factorB: Int, isCorrect: Boolean) {
        val id = "${factorA}x${factorB}"
        val existing = factDao.getFactById(id) ?: FactMasteryEntity(
            id = id,
            factorA = factorA,
            factorB = factorB
        )

        val newAttempts = existing.attemptCount + 1
        val newCorrect = if (isCorrect) existing.correctCount + 1 else existing.correctCount
        val newStreak = if (isCorrect) existing.currentStreak + 1 else 0
        val acc = (newCorrect.toFloat() / newAttempts.toFloat())

        val newLevel = when {
            newAttempts >= 4 && newStreak >= 3 && acc >= 0.8f -> 3 // Mastered
            newAttempts >= 2 && acc >= 0.6f -> 2 // Practiced
            newAttempts >= 1 -> 1 // Learning
            else -> 0
        }

        val updated = existing.copy(
            attemptCount = newAttempts,
            correctCount = newCorrect,
            currentStreak = newStreak,
            lastPracticedTime = System.currentTimeMillis(),
            masteryLevel = newLevel
        )
        factDao.insertOrUpdate(updated)
    }

    suspend fun saveQuizRecord(record: QuizHistoryEntity) {
        factDao.insertQuizRecord(record)
    }

    suspend fun resetAllProgress() {
        val initialFacts = mutableListOf<FactMasteryEntity>()
        for (a in 1..12) {
            for (b in 1..12) {
                initialFacts.add(
                    FactMasteryEntity(
                        id = "${a}x${b}",
                        factorA = a,
                        factorB = b,
                        correctCount = 0,
                        attemptCount = 0,
                        currentStreak = 0,
                        lastPracticedTime = 0L,
                        masteryLevel = 0
                    )
                )
            }
        }
        factDao.insertAll(initialFacts)
    }
}
