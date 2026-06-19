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

private val ZenWealthDarkColorScheme = darkColorScheme(
    primary = ZenPrimaryDark,
    onPrimary = ZenOnPrimaryDark,
    primaryContainer = ZenPrimaryContainerDark,
    onPrimaryContainer = ZenOnPrimaryContainerDark,
    secondary = ZenSecondaryDark,
    onSecondary = ZenOnSecondaryDark,
    secondaryContainer = ZenSecondaryContainerDark,
    onSecondaryContainer = ZenOnSecondaryContainerDark,
    tertiary = ZenTertiaryDark,
    onTertiary = ZenOnTertiaryDark,
    tertiaryContainer = ZenTertiaryContainerDark,
    onTertiaryContainer = ZenOnTertiaryContainerDark,
    error = ZenErrorDark,
    onError = ZenOnErrorDark,
    errorContainer = ZenErrorContainerDark,
    onErrorContainer = ZenOnErrorContainerDark,
    background = ZenBackgroundDark,
    onBackground = ZenOnBackgroundDark,
    surface = ZenSurfaceDark,
    onSurface = ZenOnSurfaceDark,
    surfaceVariant = ZenSurfaceVariantDark,
    onSurfaceVariant = ZenOnSurfaceVariantDark,
    outline = ZenOutlineDark,
    inverseSurface = ZenInverseSurfaceDark,
    inverseOnSurface = ZenInverseOnSurfaceDark
)

private val ZenWealthLightColorScheme = lightColorScheme(
    primary = ZenPrimaryLight,
    onPrimary = ZenOnPrimaryLight,
    primaryContainer = ZenPrimaryContainerLight,
    onPrimaryContainer = ZenOnPrimaryContainerLight,
    secondary = ZenSecondaryLight,
    onSecondary = ZenOnSecondaryLight,
    secondaryContainer = ZenSecondaryContainerLight,
    onSecondaryContainer = ZenOnSecondaryContainerLight,
    tertiary = ZenTertiaryLight,
    onTertiary = ZenOnTertiaryLight,
    tertiaryContainer = ZenTertiaryContainerLight,
    onTertiaryContainer = ZenOnTertiaryContainerLight,
    error = ZenErrorLight,
    onError = ZenOnErrorLight,
    errorContainer = ZenErrorContainerLight,
    onErrorContainer = ZenOnErrorContainerLight,
    background = ZenBackgroundLight,
    onBackground = ZenOnBackgroundLight,
    surface = ZenSurfaceLight,
    onSurface = ZenOnSurfaceLight,
    surfaceVariant = ZenSurfaceVariantLight,
    onSurfaceVariant = ZenOnSurfaceVariantLight,
    outline = ZenOutlineLight,
    inverseSurface = ZenInverseSurfaceLight,
    inverseOnSurface = ZenInverseOnSurfaceLight
)

@Composable
fun OrioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to prioritize Zen Wealth identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> ZenWealthDarkColorScheme
        else -> ZenWealthLightColorScheme
    }
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
//        }
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OrioTypography,
        shapes = OrioShapes,
        content = content
    )
}
