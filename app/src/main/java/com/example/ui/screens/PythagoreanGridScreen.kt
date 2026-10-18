package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FactMasteryEntity
import com.example.ui.MultiplicationViewModel
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary

@Composable
fun PythagoreanGridScreen(
    viewModel: MultiplicationViewModel,
    modifier: Modifier = Modifier
) {
    val allFacts by viewModel.allFacts.collectAsState()
    val selectedCell by viewModel.selectedGridCell.collectAsState()

    val factMap = allFacts.associateBy { it.id }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("pythagoras_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pythagorean Table (12 × 12)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Interactive mastery heat map. Tap any cell to inspect or practice!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = EmeraldSuccess, label = "Mastered")
                LegendItem(color = IndigoPrimary, label = "Practiced")
                LegendItem(color = VioletSecondary, label = "Learning")
                LegendItem(color = MaterialTheme.colorScheme.surfaceVariant, label = "New")
            }
        }

        // Selected Cell Info Card
        if (selectedCell != null) {
            val (r, c) = selectedCell!!
            val fact = factMap["${r}x${c}"]
            SelectedCellBanner(
                r = r,
                c = c,
                fact = fact,
                onPractice = { viewModel.quickPracticeFact(r, c) },
                onViewArray = { viewModel.showInVisualizer(r, c) }
            )
        }

        // Scrollable 12x12 Grid Matrix
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    Column {
                        // Top Header Row: 0..12
                        Row {
                            // Top-left corner
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(IndigoPrimary, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "×",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }

                            // Column numbers 1..12
                            for (col in 1..12) {
                                val isHighlightedCol = selectedCell?.second == col
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .padding(2.dp)
                                        .background(
                                            if (isHighlightedCol) IndigoPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = col.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isHighlightedCol) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Grid Rows 1..12
                        for (row in 1..12) {
                            val isHighlightedRow = selectedCell?.first == row
                            Row {
                                // Row Header
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .padding(2.dp)
                                        .background(
                                            if (isHighlightedRow) IndigoPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = row.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isHighlightedRow) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Cells (row, col)
                                for (col in 1..12) {
                                    val isSelected = selectedCell == Pair(row, col)
                                    val isHighlighted = isHighlightedRow || selectedCell?.second == col
                                    val isDiagonal = row == col
                                    val fact = factMap["${row}x${col}"]
                                    val mastery = fact?.masteryLevel ?: 0
                                    val product = row * col

                                    val cellBg = when {
                                        isSelected -> AmberTertiary
                                        mastery >= 3 -> EmeraldSuccess.copy(alpha = 0.85f)
                                        mastery == 2 -> IndigoPrimary.copy(alpha = 0.65f)
                                        mastery == 1 -> VioletSecondary.copy(alpha = 0.45f)
                                        isHighlighted -> IndigoPrimary.copy(alpha = 0.1f)
                                        isDiagonal -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                        else -> MaterialTheme.colorScheme.surface
                                    }

                                    val textColor = when {
                                        isSelected -> Color.White
                                        mastery >= 2 -> Color.White
                                        mastery == 1 -> Color.White
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(cellBg)
                                            .then(
                                                if (isDiagonal && !isSelected) {
                                                    Modifier.border(1.dp, AmberTertiary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                                } else Modifier
                                            )
                                            .clickable { viewModel.selectGridCell(row, col) }
                                            .testTag("cell_${row}x${col}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = product.toString(),
                                            fontWeight = if (isDiagonal || isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = textColor,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SelectedCellBanner(
    r: Int,
    c: Int,
    fact: FactMasteryEntity?,
    onPractice: () -> Unit,
    onViewArray: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
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
                    text = "$r × $c = ${r * c}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (fact != null && fact.attemptCount > 0) {
                        "Accuracy: ${fact.accuracy.toInt()}% (${fact.correctCount}/${fact.attemptCount})"
                    } else {
                        "Not practiced yet"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                    onClick = onViewArray,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Array", fontSize = 12.sp)
                }

                FilledTonalButton(
                    onClick = onPractice,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Practice", fontSize = 12.sp)
                }
            }
        }
    }
}
