package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MultiplicationViewModel
import com.example.ui.components.DotMatrixCanvas
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary

@Composable
fun VisualArrayScreen(
    viewModel: MultiplicationViewModel,
    modifier: Modifier = Modifier
) {
    val visualState by viewModel.visualState.collectAsState()
    val rows = visualState.rows
    val cols = visualState.cols
    val total = rows * cols
    val isSquare = rows == cols

    val presets = listOf(
        Pair(3, 4),
        Pair(5, 5),
        Pair(6, 7),
        Pair(7, 8),
        Pair(8, 8),
        Pair(9, 6),
        Pair(11, 11),
        Pair(12, 12)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("visual_array_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Visual Area & Array Model",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "See how multiplication creates real geometric areas!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Presets
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(presets) { (r, c) ->
                    val isSelected = r == rows && c == cols
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.updateVisualDimensions(r, c) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$r × $c",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Dot Matrix Visualizer
        item {
            DotMatrixCanvas(
                rows = rows,
                cols = cols,
                splitRowsAt = visualState.splitAt
            )
        }

        // Dimension Controls (Rows and Columns steppers / sliders)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row Controller
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rows: $rows",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateVisualDimensions(rows - 1, cols) },
                                    enabled = rows > 1
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease rows")
                                }
                                IconButton(
                                    onClick = { viewModel.updateVisualDimensions(rows + 1, cols) },
                                    enabled = rows < 12
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase rows")
                                }
                            }
                        }
                        Slider(
                            value = rows.toFloat(),
                            onValueChange = { viewModel.updateVisualDimensions(it.toInt(), cols) },
                            valueRange = 1f..12f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = IndigoPrimary, activeTrackColor = IndigoPrimary)
                        )
                    }

                    // Column Controller
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Columns: $cols",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateVisualDimensions(rows, cols - 1) },
                                    enabled = cols > 1
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease columns")
                                }
                                IconButton(
                                    onClick = { viewModel.updateVisualDimensions(rows, cols + 1) },
                                    enabled = cols < 12
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase columns")
                                }
                            }
                        }
                        Slider(
                            value = cols.toFloat(),
                            onValueChange = { viewModel.updateVisualDimensions(rows, it.toInt()) },
                            valueRange = 1f..12f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = VioletSecondary, activeTrackColor = VioletSecondary)
                        )
                    }
                }
            }
        }

        // Math Concepts: Commutative & Distributive
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Explore Mathematical Properties",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Commutative Property Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Commutative Property",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$rows × $cols = $cols × $rows = $total",
                                    fontSize = 13.sp,
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            FilledTonalButton(
                                onClick = { viewModel.swapVisualRowsCols() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Rotate Grid", fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Turning a $rows×$cols rectangle sideways gives a $cols×$rows rectangle. The total count stays exactly the same!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Distributive / Break-Apart Card
                if (rows > 2) {
                    val split = if (rows > 5) 5 else (rows / 2).coerceAtLeast(1)
                    val topCount = split * cols
                    val bottomCount = (rows - split) * cols

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Distributive Property (Break Apart)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "($split × $cols) + (${rows - split} × $cols) = $topCount + $bottomCount = $total",
                                        fontSize = 13.sp,
                                        color = AmberTertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                FilledTonalButton(
                                    onClick = { viewModel.toggleVisualSplit(split) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (visualState.splitAt > 0) "Merge" else "Split", fontSize = 12.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Break a hard multiplication ($rows×$cols) into two friendly parts: $split×$cols and ${rows - split}×$cols!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Square Number Special Alert
                if (isSquare) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⬛",
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Perfect Square Number!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = "$rows × $cols makes a perfect geometrical square with area $total!",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
