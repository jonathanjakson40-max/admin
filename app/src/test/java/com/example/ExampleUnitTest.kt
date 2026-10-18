package com.example

import com.example.model.MathTricksData
import com.example.model.QuizQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun multiplication_isCorrect() {
        assertEquals(56, 7 * 8)
        assertEquals(81, 9 * 9)
        assertEquals(144, 12 * 12)
    }

    @Test
    fun quizQuestion_returnsCorrectAnswer() {
        val q = QuizQuestion(
            factorA = 6,
            factorB = 7,
            options = listOf(42, 36, 48, 40)
        )
        assertEquals(42, q.correctAnswer)
        assertTrue(q.options.contains(42))
    }

    @Test
    fun mathTricks_containKeyTables() {
        val tablesWithTricks = MathTricksData.tricks.map { it.tableNumber }
        assertTrue(tablesWithTricks.contains(0))
        assertTrue(tablesWithTricks.contains(5))
        assertTrue(tablesWithTricks.contains(7))
        assertTrue(tablesWithTricks.contains(9))
        assertTrue(tablesWithTricks.contains(12))
    }
}
