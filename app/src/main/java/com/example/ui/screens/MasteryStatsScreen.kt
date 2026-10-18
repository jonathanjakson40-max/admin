package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MathTrick
import com.example.model.MathTricksData
import com.example.ui.MultiplicationViewModel
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary

@Composable
fun MasteryStatsScreen(
    viewModel: MultiplicationViewModel,
    modifier: Modifier = Modifier
) {
    val allFacts by viewModel.allFacts.collectAsState()
    val trickyFacts by viewModel.trickyFacts.collectAsState()
    val recentHistory by viewModel.recentQuizHistory.collectAsState()
    val masteredCount by viewModel.masteredCount.collectAsState()

    val totalFacts = 144
    val masteryPercent = if (totalFacts > 0) (masteredCount.toFloat() / totalFacts.toFloat()) * 100f else 0f

    val totalAttempts = allFacts.sumOf { it.attemptCount }
    val totalCorrect = allFacts.sumOf { it.correctCount }
    val overallAccuracy = if (totalAttempts > 0) (totalCorrect.toFloat() / totalAttempts.toFloat()) * 100f else 0f

    var expandedTrickId by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("mastery_stats_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Overall Mastery
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Fact Mastery",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$masteredCount of $totalFacts Facts Mastered (${masteryPercent.toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { masteryPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EmeraldSuccess,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )

                    Spacer(Modifier.height(16.dp))

                    // 3-Stat Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill(title = "Total Practiced", value = "$totalAttempts")
                        StatPill(title = "Accuracy", value = "${overallAccuracy.toInt()}%")
                        StatPill(title = "Mastered", value = "$masteredCount")
                    }
                }
            }
        }

        // Tricky Facts Section (if any)
        if (trickyFacts.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Tricky Facts to Review",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (fact in trickyFacts) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${fact.factorA} × ${fact.factorB} = ${fact.factorA * fact.factorB}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${fact.accuracy.toInt()}% acc",
                                            color = RoseError,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        FilledTonalButton(
                                            onClick = { viewModel.quickPracticeFact(fact.factorA, fact.factorB) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Practice", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Multiplication Secrets & Tricks Handbook
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Mental Math Shortcuts & Tricks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        items(MathTricksData.tricks, key = { it.tableNumber }) { trick ->
            TrickExpandableCard(
                trick = trick,
                isExpanded = expandedTrickId == trick.tableNumber,
                onToggle = {
                    expandedTrickId = if (expandedTrickId == trick.tableNumber) null else trick.tableNumber
                }
            )
        }

        // Recent Quiz Sessions
        if (recentHistory.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Quiz Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(recentHistory.take(5)) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.mode.replace("_", " "),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Score: ${item.score} pts • ${item.accuracy.toInt()}% correct",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${item.bestStreak}x streak",
                            color = AmberTertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(title: String, value: String) {
    Column {
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun TrickExpandableCard(
    trick: MathTrick,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = IndigoPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${trick.tableNumber}",
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                fontSize = 15.sp
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = trick.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = trick.rule,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Example: ${trick.example}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IndigoPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Visual: ${trick.visualHint}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
