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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.FactMasteryEntity
import com.example.model.MathTricksData
import com.example.ui.MultiplicationViewModel
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary

@Composable
fun LearnScreen(
    viewModel: MultiplicationViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTable by viewModel.selectedTable.collectAsState()
    val facts by viewModel.factsForCurrentTable.collectAsState()
    var showRepeatedAddition by remember { mutableStateOf(false) }

    val trick = MathTricksData.tricks.find { it.tableNumber == selectedTable }
    val masteredInTable = facts.count { it.masteryLevel >= 3 }
    val tableProgress = if (facts.isNotEmpty()) masteredInTable.toFloat() / facts.size.toFloat() else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("learn_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Table Selector Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Table",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items((1..12).toList()) { tableNum ->
                        val isSelected = tableNum == selectedTable
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.setSelectedTable(tableNum) }
                                .testTag("table_chip_$tableNum"),
                            shape = CircleShape,
                            color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = if (isSelected) 6.dp else 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tableNum.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Table Hero Overview Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
                                text = "Table of $selectedTable",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$masteredInTable / 12 Facts Mastered",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        FilledTonalButton(
                            onClick = { viewModel.quickPracticeFact(selectedTable, 1) },
                            modifier = Modifier.testTag("practice_table_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Practice")
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { tableProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldSuccess,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )

                    // Pro-Tip or Shortcut
                    if (trick != null) {
                        Spacer(Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = AmberTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = trick.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = trick.rule,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Toggles (Repeated addition)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Table Facts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedButton(
                    onClick = { showRepeatedAddition = !showRepeatedAddition },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (showRepeatedAddition) "Hide Addition" else "Show Addition", fontSize = 12.sp)
                }
            }
        }

        // List of Facts
        items(facts, key = { it.id }) { fact ->
            FactRowCard(
                fact = fact,
                showAddition = showRepeatedAddition,
                onViewArray = { viewModel.showInVisualizer(fact.factorA, fact.factorB) },
                onPractice = { viewModel.quickPracticeFact(fact.factorA, fact.factorB) }
            )
        }
    }
}

@Composable
private fun FactRowCard(
    fact: FactMasteryEntity,
    showAddition: Boolean,
    onViewArray: () -> Unit,
    onPractice: () -> Unit
) {
    val result = fact.factorA * fact.factorB
    val isMastered = fact.masteryLevel >= 3

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mastery Star
                    Surface(
                        shape = CircleShape,
                        color = when (fact.masteryLevel) {
                            3 -> EmeraldSuccess.copy(alpha = 0.2f)
                            2 -> AmberTertiary.copy(alpha = 0.2f)
                            1 -> IndigoPrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Mastery Level ${fact.masteryLevel}",
                                tint = when (fact.masteryLevel) {
                                    3 -> EmeraldSuccess
                                    2 -> AmberTertiary
                                    1 -> IndigoPrimary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "${fact.factorA} × ${fact.factorB} = ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = result.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = IndigoPrimary
                    )
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onViewArray,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = "Visual Dot Array",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onPractice,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Practice Fact",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showAddition) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = List(fact.factorA) { fact.factorB }.joinToString(" + ") + " = $result",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
