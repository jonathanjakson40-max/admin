package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InputMode
import com.example.model.QuizMode
import com.example.ui.MultiplicationViewModel
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.NumberPad
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary

@Composable
fun PracticeScreen(
    viewModel: MultiplicationViewModel,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()
    val selectedTables by viewModel.selectedTablesForQuiz.collectAsState()

    BackHandler(enabled = quizState.isActive || quizState.isFinished) {
        viewModel.exitQuiz()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            quizState.isActive && quizState.mode == QuizMode.DUEL -> {
                DuelGameView(viewModel = viewModel)
            }
            quizState.isActive -> {
                ActiveQuizView(viewModel = viewModel)
            }
            quizState.isFinished -> {
                QuizFinishedView(viewModel = viewModel)
            }
            else -> {
                QuizSetupView(viewModel = viewModel, selectedTables = selectedTables)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuizSetupView(
    viewModel: MultiplicationViewModel,
    selectedTables: Set<Int>
) {
    val quizState by viewModel.quizState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("practice_setup_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero title
        item {
            Text(
                text = "Multiplication Arena",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Train your brain with speed sprints, missing factors & duels!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Mode Selection Cards
        item {
            Text(
                text = "Choose Game Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(QuizMode.entries.size) { index ->
            val mode = QuizMode.entries[index]
            ElevatedCard(
                onClick = { viewModel.startQuiz(mode) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mode_card_${mode.name}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = when (mode) {
                            QuizMode.SPRINT -> IndigoPrimary.copy(alpha = 0.15f)
                            QuizMode.MISSING_FACTOR -> VioletSecondary.copy(alpha = 0.15f)
                            QuizMode.TIME_ATTACK -> AmberTertiary.copy(alpha = 0.15f)
                            QuizMode.DUEL -> EmeraldSuccess.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (mode) {
                                    QuizMode.SPRINT -> Icons.Default.Bolt
                                    QuizMode.MISSING_FACTOR -> Icons.Default.HelpOutline
                                    QuizMode.TIME_ATTACK -> Icons.Default.Timer
                                    QuizMode.DUEL -> Icons.Default.EmojiEvents
                                },
                                contentDescription = null,
                                tint = when (mode) {
                                    QuizMode.SPRINT -> IndigoPrimary
                                    QuizMode.MISSING_FACTOR -> VioletSecondary
                                    QuizMode.TIME_ATTACK -> AmberTertiary
                                    QuizMode.DUEL -> EmeraldSuccess
                                },
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mode.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = mode.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Input Mode toggle
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Answer Input Style",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (quizState.inputMode == InputMode.MULTIPLE_CHOICE) IndigoPrimary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setQuizInputMode(InputMode.MULTIPLE_CHOICE) }
                                .padding(vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = if (quizState.inputMode == InputMode.MULTIPLE_CHOICE) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Multiple Choice",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (quizState.inputMode == InputMode.MULTIPLE_CHOICE) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (quizState.inputMode == InputMode.NUMPAD) IndigoPrimary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setQuizInputMode(InputMode.NUMPAD) }
                                .padding(vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Keyboard,
                                    contentDescription = null,
                                    tint = if (quizState.inputMode == InputMode.NUMPAD) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Number Pad",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (quizState.inputMode == InputMode.NUMPAD) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Table Filter Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Include Tables (${selectedTables.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedButton(
                        onClick = { viewModel.selectAllQuizTables() },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("All 1-12", fontSize = 12.sp)
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (t in 1..12) {
                        val isSelected = selectedTables.contains(t)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleQuizTable(t) },
                            label = { Text("×$t", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndigoPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_table_$t")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveQuizView(
    viewModel: MultiplicationViewModel
) {
    val quizState by viewModel.quizState.collectAsState()
    val q = quizState.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Exit button, Mode, Streak, Score
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.exitQuiz() },
                    modifier = Modifier.testTag("quiz_close_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit Quiz")
                }

                // Streak pill
                if (quizState.streak > 1) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AmberTertiary.copy(alpha = 0.15f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AmberTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${quizState.streak} Streak!",
                                color = AmberTertiary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Score pill
                Box(
                    modifier = Modifier
                        .background(IndigoPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${quizState.score} pts",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Progress or Timer
            if (quizState.mode == QuizMode.TIME_ATTACK) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time Left",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${quizState.timeRemaining}s",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (quizState.timeRemaining <= 10) RoseError else IndigoPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { quizState.timeRemaining / 60f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (quizState.timeRemaining <= 10) RoseError else IndigoPrimary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${quizState.questionNumber} of ${quizState.totalQuestions}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LinearProgressIndicator(
                    progress = { quizState.questionNumber.toFloat() / quizState.totalQuestions.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IndigoPrimary
                )
            }
        }

        // Center: Equation Card
        ElevatedCard(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = when (quizState.lastAnswerCorrect) {
                    true -> EmeraldSuccess.copy(alpha = 0.15f)
                    false -> RoseError.copy(alpha = 0.15f)
                    null -> MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = q.displayEquation,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                if (quizState.inputMode == InputMode.NUMPAD) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = quizState.currentInput.ifEmpty { "?" },
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = IndigoPrimary
                    )
                }

                // Feedback message
                if (quizState.lastAnswerCorrect == false) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Correct answer was ${quizState.lastCorrectAnswerValue}!",
                        color = RoseError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Bottom: Answer Options (Multiple Choice OR Numpad)
        if (quizState.inputMode == InputMode.MULTIPLE_CHOICE) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val chunks = q.options.chunked(2)
                for (row in chunks) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (opt in row) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 3.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { viewModel.submitAnswer(opt) }
                                    .testTag("quiz_option_$opt")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = opt.toString(),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            NumberPad(
                onDigitClick = { viewModel.appendNumpadDigit(it) },
                onBackspace = { viewModel.backspaceNumpad() },
                onSubmit = { viewModel.submitNumpadAnswer() },
                submitEnabled = quizState.currentInput.isNotEmpty()
            )
        }
    }
}

@Composable
private fun DuelGameView(
    viewModel: MultiplicationViewModel
) {
    val quizState by viewModel.quizState.collectAsState()
    val q = quizState.currentQuestion ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        // Player 1 Area (Rotated 180° for the person sitting opposite)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(VioletSecondary.copy(alpha = 0.1f))
                .graphicsLayer(rotationZ = 180f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PLAYER 1",
                        fontWeight = FontWeight.ExtraBold,
                        color = VioletSecondary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Score: ${quizState.duelP1Score}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = VioletSecondary
                    )
                }

                Text(
                    text = q.displayEquation,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (opt in q.options) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.submitAnswer(opt, forDuelPlayer = 1) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = opt.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(IndigoPrimary)
        )

        // Player 2 Area (Normal orientation)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(IndigoPrimary.copy(alpha = 0.08f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PLAYER 2",
                        fontWeight = FontWeight.ExtraBold,
                        color = IndigoPrimary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Score: ${quizState.duelP2Score}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = IndigoPrimary
                    )
                }

                Text(
                    text = q.displayEquation,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (opt in q.options) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.submitAnswer(opt, forDuelPlayer = 2) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = opt.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizFinishedView(
    viewModel: MultiplicationViewModel
) {
    val quizState by viewModel.quizState.collectAsState()
    val total = if (quizState.mode == QuizMode.TIME_ATTACK) quizState.questionNumber else quizState.totalQuestions
    val accuracy = if (total > 0) (quizState.correctCount.toFloat() / total.toFloat()) * 100f else 0f

    Box(modifier = Modifier.fillMaxSize()) {
        ConfettiEffect()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("quiz_results_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = AmberTertiary.copy(alpha = 0.2f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmberTertiary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (accuracy >= 80f) "Outstanding Job!" else "Great Practice!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Score: ${quizState.score} points",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = IndigoPrimary
            )

            Spacer(Modifier.height(24.dp))

            // Stats Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${quizState.correctCount} / $total",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Correct",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${accuracy.toInt()}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = EmeraldSuccess
                        )
                        Text(
                            text = "Accuracy",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${quizState.bestStreak}x",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AmberTertiary
                        )
                        Text(
                            text = "Best Streak",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.startQuiz(quizState.mode) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("play_again_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.exitQuiz() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("done_quiz_button")
            ) {
                Text("Back to Practice Menu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
