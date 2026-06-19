package com.quickthought.orio.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val OrioShapes = Shapes(
    small = RoundedCornerShape(4.dp),      // sm: 0.25rem
    medium = RoundedCornerShape(12.dp),    // md: 0.75rem (DEFAULT/md mix)
    large = RoundedCornerShape(16.dp),     // lg: 1rem
    extraLarge = RoundedCornerShape(24.dp) // xl: 1.5rem
)
