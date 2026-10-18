package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    val colors = listOf(IndigoPrimary, VioletSecondary, AmberTertiary, EmeraldSuccess, RoseError)

    val particles = remember {
        List(45) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = -0.1f - Random.nextFloat() * 0.2f,
                speedY = 0.8f + Random.nextFloat() * 0.6f,
                speedX = (Random.nextFloat() - 0.5f) * 0.4f,
                size = 12f + Random.nextFloat() * 14f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        for (particle in particles) {
            val currentX = (particle.initialX + particle.speedX * p).coerceIn(0f, 1f) * size.width
            val currentY = (particle.initialY + particle.speedY * p) * size.height
            if (currentY in 0f..size.height) {
                drawRect(
                    color = particle.color.copy(alpha = (1f - p * 0.5f).coerceIn(0f, 1f)),
                    topLeft = Offset(currentX, currentY),
                    size = Size(particle.size, particle.size * 0.6f)
                )
            }
        }
    }
}
