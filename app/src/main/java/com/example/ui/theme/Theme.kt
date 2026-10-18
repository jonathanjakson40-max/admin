package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoOnContainerDark,
    secondary = VioletSecondaryDark,
    secondaryContainer = VioletContainerDark,
    tertiary = AmberTertiaryDark,
    tertiaryContainer = AmberContainerDark,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onBackground = SlateTextPrimaryDark,
    onSurface = SlateTextPrimaryDark,
    onSurfaceVariant = SlateTextSecondaryDark,
    error = RoseError,
    errorContainer = RoseContainer,
    onError = RoseOnPrimaryDark,
    onErrorContainer = RoseOnContainer
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoOnContainer,
    secondary = VioletSecondary,
    secondaryContainer = VioletContainer,
    onSecondaryContainer = VioletOnContainer,
    tertiary = AmberTertiary,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberOnContainer,
    background = SlateBackground,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary,
    error = RoseError,
    errorContainer = RoseContainer,
    onError = IndigoOnPrimary,
    onErrorContainer = RoseOnContainer
)

@Composable
fun MultiplyMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
