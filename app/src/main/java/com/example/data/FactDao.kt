package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FactDao {
    @Query("SELECT * FROM fact_mastery")
    fun getAllFactsFlow(): Flow<List<FactMasteryEntity>>

    @Query("SELECT * FROM fact_mastery WHERE factorA = :tableNumber ORDER BY factorB ASC")
    fun getFactsForTableFlow(tableNumber: Int): Flow<List<FactMasteryEntity>>

    @Query("SELECT * FROM fact_mastery WHERE id = :id LIMIT 1")
    suspend fun getFactById(id: String): FactMasteryEntity?

    @Query("SELECT COUNT(*) FROM fact_mastery")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facts: List<FactMasteryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(fact: FactMasteryEntity)

    @Update
    suspend fun update(fact: FactMasteryEntity)

    @Query("SELECT * FROM fact_mastery WHERE attemptCount > 0 AND (correctCount * 1.0 / attemptCount) < 0.75 ORDER BY (correctCount * 1.0 / attemptCount) ASC LIMIT 10")
    fun getTrickyFactsFlow(): Flow<List<FactMasteryEntity>>

    @Query("SELECT COUNT(*) FROM fact_mastery WHERE masteryLevel = 3")
    fun getMasteredCountFlow(): Flow<Int>

    // Quiz History
    @Insert
    suspend fun insertQuizRecord(record: QuizHistoryEntity)

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentQuizHistoryFlow(): Flow<List<QuizHistoryEntity>>

    @Query("SELECT MAX(score) FROM quiz_history WHERE mode = :mode")
    fun getHighScoreFlow(mode: String): Flow<Int?>
}
