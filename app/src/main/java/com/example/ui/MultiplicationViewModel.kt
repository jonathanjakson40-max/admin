package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FactMasteryEntity
import com.example.data.MultiplyDatabase
import com.example.data.MultiplyRepository
import com.example.data.QuizHistoryEntity
import com.example.model.InputMode
import com.example.model.MissingType
import com.example.model.NavTab
import com.example.model.QuizMode
import com.example.model.QuizQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class QuizState(
    val isActive: Boolean = false,
    val isFinished: Boolean = false,
    val mode: QuizMode = QuizMode.SPRINT,
    val inputMode: InputMode = InputMode.MULTIPLE_CHOICE,
    val currentQuestion: QuizQuestion? = null,
    val questionNumber: Int = 1,
    val totalQuestions: Int = 15,
    val score: Int = 0,
    val correctCount: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val timeRemaining: Int = 60,
    val currentInput: String = "",
    val lastAnswerCorrect: Boolean? = null,
    val lastCorrectAnswerValue: Int = 0,
    val duelP1Score: Int = 0,
    val duelP2Score: Int = 0
)

data class VisualState(
    val rows: Int = 4,
    val cols: Int = 6,
    val splitAt: Int = 0,
    val showCommutative: Boolean = false
)

class MultiplicationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MultiplyRepository
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    init {
        val db = MultiplyDatabase.getDatabase(application)
        repository = MultiplyRepository(db.factDao())
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    // Navigation
    private val _currentTab = MutableStateFlow(NavTab.LEARN)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
    }

    // Learn Screen
    private val _selectedTable = MutableStateFlow(2)
    val selectedTable: StateFlow<Int> = _selectedTable.asStateFlow()

    fun setSelectedTable(table: Int) {
        _selectedTable.value = table
    }

    val factsForCurrentTable: StateFlow<List<FactMasteryEntity>> =
        _selectedTable.combine(repository.allFacts) { table, all ->
            all.filter { it.factorA == table }.sortedBy { it.factorB }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFacts: StateFlow<List<FactMasteryEntity>> =
        repository.allFacts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trickyFacts: StateFlow<List<FactMasteryEntity>> =
        repository.trickyFacts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentQuizHistory: StateFlow<List<QuizHistoryEntity>> =
        repository.recentQuizHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masteredCount: StateFlow<Int> =
        repository.masteredCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Visual Array State
    private val _visualState = MutableStateFlow(VisualState())
    val visualState: StateFlow<VisualState> = _visualState.asStateFlow()

    fun updateVisualDimensions(rows: Int, cols: Int) {
        _visualState.value = _visualState.value.copy(
            rows = rows.coerceIn(1, 12),
            cols = cols.coerceIn(1, 12)
        )
    }

    fun toggleVisualSplit(split: Int) {
        val current = _visualState.value.splitAt
        _visualState.value = _visualState.value.copy(splitAt = if (current == split) 0 else split)
    }

    fun swapVisualRowsCols() {
        val current = _visualState.value
        _visualState.value = current.copy(
            rows = current.cols,
            cols = current.rows,
            showCommutative = !current.showCommutative
        )
    }

    // Pythagorean Grid
    private val _selectedGridCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedGridCell: StateFlow<Pair<Int, Int>?> = _selectedGridCell.asStateFlow()

    fun selectGridCell(row: Int, col: Int) {
        if (_selectedGridCell.value == Pair(row, col)) {
            _selectedGridCell.value = null
        } else {
            _selectedGridCell.value = Pair(row, col)
        }
    }

    // Practice / Quiz State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _selectedTablesForQuiz = MutableStateFlow(setOf(2, 3, 4, 5, 6, 7, 8, 9, 10))
    val selectedTablesForQuiz: StateFlow<Set<Int>> = _selectedTablesForQuiz.asStateFlow()

    fun toggleQuizTable(table: Int) {
        val current = _selectedTablesForQuiz.value.toMutableSet()
        if (current.contains(table)) {
            if (current.size > 1) current.remove(table)
        } else {
            current.add(table)
        }
        _selectedTablesForQuiz.value = current
    }

    fun selectAllQuizTables() {
        _selectedTablesForQuiz.value = (1..12).toSet()
    }

    fun setQuizInputMode(mode: InputMode) {
        _quizState.value = _quizState.value.copy(inputMode = mode)
    }

    private var timerJob: Job? = null

    fun startQuiz(mode: QuizMode) {
        timerJob?.cancel()
        val question = generateQuestion(mode)
        _quizState.value = QuizState(
            isActive = true,
            isFinished = false,
            mode = mode,
            inputMode = _quizState.value.inputMode,
            currentQuestion = question,
            questionNumber = 1,
            totalQuestions = if (mode == QuizMode.TIME_ATTACK) 999 else 15,
            score = 0,
            correctCount = 0,
            streak = 0,
            bestStreak = 0,
            timeRemaining = 60,
            currentInput = "",
            lastAnswerCorrect = null,
            duelP1Score = 0,
            duelP2Score = 0
        )

        if (mode == QuizMode.TIME_ATTACK) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizState.value.timeRemaining > 0 && _quizState.value.isActive && !_quizState.value.isFinished) {
                delay(1000)
                val remaining = _quizState.value.timeRemaining - 1
                _quizState.value = _quizState.value.copy(timeRemaining = remaining)
                if (remaining <= 0) {
                    finishQuiz()
                    break
                }
            }
        }
    }

    fun appendNumpadDigit(digit: Int) {
        val current = _quizState.value.currentInput
        if (current.length < 4) {
            _quizState.value = _quizState.value.copy(currentInput = current + digit)
        }
    }

    fun backspaceNumpad() {
        val current = _quizState.value.currentInput
        if (current.isNotEmpty()) {
            _quizState.value = _quizState.value.copy(currentInput = current.dropLast(1))
        }
    }

    fun submitNumpadAnswer() {
        val text = _quizState.value.currentInput
        if (text.isNotEmpty()) {
            val answer = text.toIntOrNull() ?: 0
            submitAnswer(answer)
        }
    }

    fun submitAnswer(userAnswer: Int, forDuelPlayer: Int = 0) {
        val current = _quizState.value
        val q = current.currentQuestion ?: return
        if (current.lastAnswerCorrect != null) return // debounce during brief animation

        val isCorrect = userAnswer == q.correctAnswer
        val factorA = q.factorA
        val factorB = q.factorB

        // Trigger subtle haptic
        triggerHaptic(isCorrect)

        viewModelScope.launch {
            repository.recordAttempt(factorA, factorB, isCorrect)
        }

        val newStreak = if (isCorrect) current.streak + 1 else 0
        val bestStreak = maxOf(current.bestStreak, newStreak)
        val comboBonus = if (isCorrect) (1 + (newStreak / 3)) * 10 else 0
        val newScore = current.score + comboBonus

        // Update duel scores if in Duel mode
        val newP1 = if (forDuelPlayer == 1 && isCorrect) current.duelP1Score + 1 else current.duelP1Score
        val newP2 = if (forDuelPlayer == 2 && isCorrect) current.duelP2Score + 1 else current.duelP2Score

        // Show brief feedback
        _quizState.value = current.copy(
            lastAnswerCorrect = isCorrect,
            lastCorrectAnswerValue = q.correctAnswer,
            score = newScore,
            correctCount = if (isCorrect) current.correctCount + 1 else current.correctCount,
            streak = newStreak,
            bestStreak = bestStreak,
            duelP1Score = newP1,
            duelP2Score = newP2
        )

        viewModelScope.launch {
            delay(500) // Brief visual feedback
            if (!_quizState.value.isActive) return@launch

            if (current.mode != QuizMode.TIME_ATTACK && current.questionNumber >= current.totalQuestions) {
                finishQuiz()
            } else {
                val nextQ = generateQuestion(current.mode)
                _quizState.value = _quizState.value.copy(
                    currentQuestion = nextQ,
                    questionNumber = current.questionNumber + 1,
                    currentInput = "",
                    lastAnswerCorrect = null
                )
            }
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        val current = _quizState.value
        val total = if (current.mode == QuizMode.TIME_ATTACK) current.questionNumber else current.totalQuestions
        val accuracy = if (total > 0) (current.correctCount.toFloat() / total.toFloat()) * 100f else 0f

        val record = QuizHistoryEntity(
            mode = current.mode.name,
            score = current.score,
            totalQuestions = total,
            accuracy = accuracy,
            durationSeconds = if (current.mode == QuizMode.TIME_ATTACK) 60 else 30,
            bestStreak = current.bestStreak,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveQuizRecord(record)
        }

        _quizState.value = current.copy(
            isActive = false,
            isFinished = true,
            lastAnswerCorrect = null
        )
    }

    fun exitQuiz() {
        timerJob?.cancel()
        _quizState.value = QuizState()
    }

    private fun generateQuestion(mode: QuizMode): QuizQuestion {
        val tables = _selectedTablesForQuiz.value.toList().ifEmpty { listOf(2, 3, 4, 5) }
        val a = tables[Random.nextInt(tables.size)]
        val b = Random.nextInt(1, 13)

        val missingType = when (mode) {
            QuizMode.MISSING_FACTOR -> if (Random.nextBoolean()) MissingType.FACTOR_A else MissingType.FACTOR_B
            else -> MissingType.RESULT
        }

        val correctAnswer = when (missingType) {
            MissingType.RESULT -> a * b
            MissingType.FACTOR_A -> a
            MissingType.FACTOR_B -> b
        }

        // Generate 3 plausible distractors
        val options = mutableSetOf(correctAnswer)
        var tries = 0
        while (options.size < 4 && tries < 30) {
            tries++
            val distractor = when (missingType) {
                MissingType.RESULT -> {
                    val delta = listOf(-a, a, -b, b, -1, 1, -2, 2, 10, -10).random()
                    (correctAnswer + delta).coerceAtLeast(1)
                }
                else -> {
                    (Random.nextInt(1, 13))
                }
            }
            if (distractor > 0) {
                options.add(distractor)
            }
        }

        while (options.size < 4) {
            options.add(options.size + 1)
        }

        return QuizQuestion(
            factorA = a,
            factorB = b,
            missingType = missingType,
            options = options.toList().shuffled()
        )
    }

    private fun triggerHaptic(isSuccess: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (isSuccess) {
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 40), -1)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    fun quickPracticeFact(factorA: Int, factorB: Int) {
        _selectedTablesForQuiz.value = setOf(factorA)
        _currentTab.value = NavTab.PRACTICE
        startQuiz(QuizMode.SPRINT)
    }

    fun showInVisualizer(factorA: Int, factorB: Int) {
        _visualState.value = VisualState(rows = factorA, cols = factorB)
        _currentTab.value = NavTab.VISUAL
    }
}
