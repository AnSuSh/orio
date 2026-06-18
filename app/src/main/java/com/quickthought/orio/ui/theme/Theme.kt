package com.quickthought.orio.ui.theme

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
    primary = OrioPrimaryDark,
    secondary = OrioSecondary,
    background = OrioBackgroundDark,
    surface = OrioSurfaceDark,
    onPrimary = OrioOnPrimaryDark,
    onBackground = OrioOnSurfaceDark,
    onSurface = OrioOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrioPrimary,
    secondary = OrioSecondary,
    background = OrioBackground,
    surface = OrioSurface,
    onPrimary = OrioOnPrimary,
    onBackground = OrioOnSurface,
    onSurface = OrioOnSurface
)

@Composable
fun OrioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamicColor false if you want Orio's specific brand colors to shine
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
        typography = OrioTypography,
        content = content
    )
}