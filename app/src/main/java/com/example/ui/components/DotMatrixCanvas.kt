package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import kotlin.math.min

@Composable
fun DotMatrixCanvas(
    rows: Int,
    cols: Int,
    modifier: Modifier = Modifier,
    splitRowsAt: Int = 0, // e.g. 5 for distributive property breakdown
    showCellNumbers: Boolean = false
) {
    val total = rows * cols

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$rows rows × $cols columns",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .background(IndigoPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "= $total dots",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Canvas drawing grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val availableWidth = size.width
                    val availableHeight = size.height

                    val maxDimension = maxOf(rows, cols, 1)
                    val spacing = 4.dp.toPx()

                    // Calculate dot size based on available width and height
                    val dotWidth = (availableWidth - (cols + 1) * spacing) / cols
                    val dotHeight = (availableHeight - (rows + 1) * spacing) / rows
                    val dotSize = min(dotWidth, dotHeight).coerceIn(6f, 32f)

                    val gridWidth = cols * dotSize + (cols - 1) * spacing
                    val gridHeight = rows * dotSize + (rows - 1) * spacing

                    val startX = (availableWidth - gridWidth) / 2f
                    val startY = (availableHeight - gridHeight) / 2f

                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val x = startX + c * (dotSize + spacing)
                            val y = startY + r * (dotSize + spacing)

                            val color = when {
                                splitRowsAt > 0 && r < splitRowsAt -> IndigoPrimary
                                splitRowsAt > 0 -> AmberTertiary
                                (r + c) % 2 == 0 -> IndigoPrimary
                                else -> IndigoPrimary.copy(alpha = 0.75f)
                            }

                            // Draw rounded square or circle
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(x, y),
                                size = Size(dotSize, dotSize),
                                cornerRadius = CornerRadius(dotSize * 0.35f, dotSize * 0.35f)
                            )
                        }
                    }
                }
            }
        }
    }
}
