package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary

@Composable
fun NumberPad(
    onDigitClick: (Int) -> Unit,
    onBackspace: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    submitEnabled: Boolean = true
) {
    val buttonShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (digit in row) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(buttonShape)
                            .clickable { onDigitClick(digit) }
                            .testTag("numpad_key_$digit"),
                        shape = buttonShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = digit.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Bottom row: Backspace, 0, Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Backspace
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(buttonShape)
                    .clickable { onBackspace() }
                    .testTag("numpad_backspace"),
                shape = buttonShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 0
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(buttonShape)
                    .clickable { onDigitClick(0) }
                    .testTag("numpad_key_0"),
                shape = buttonShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Submit
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(buttonShape)
                    .clickable(enabled = submitEnabled) { onSubmit() }
                    .testTag("numpad_submit"),
                shape = buttonShape,
                color = if (submitEnabled) EmeraldSuccess else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Submit Answer",
                        tint = if (submitEnabled) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
