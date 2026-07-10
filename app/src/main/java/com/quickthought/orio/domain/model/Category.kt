package com.quickthought.orio.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import com.quickthought.orio.data.local.entity.CategoryData

// domain/model/Category.kt
data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

fun Category.toCategoryData(): CategoryData {
    return CategoryData(
        id = id,
        name = name,
        iconName = getIconName(icon),
        colorHex = String.format("#%06X", 0xFFFFFF and color.toArgb())
    )
}

fun CategoryData.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        icon = getIconByName(iconName),
        color = Color(android.graphics.Color.parseColor(colorHex))
    )
}

val availableIcons = mapOf(
    "Restaurant" to Icons.Default.Restaurant,
    "HealthAndSafety" to Icons.Default.HealthAndSafety,
    "Payments" to Icons.Default.Payments,
    "DirectionsBus" to Icons.Default.DirectionsBus,
    "ShoppingBag" to Icons.Default.ShoppingBag,
    "MusicVideo" to Icons.Default.MusicVideo,
    "School" to Icons.Default.School,
    "Category" to Icons.Default.Category
)

fun getIconByName(name: String): ImageVector {
    return availableIcons[name] ?: Icons.Default.Category
}

fun getIconName(icon: ImageVector): String {
    return availableIcons.entries.find { it.value == icon }?.key ?: "Category"
}

// Define a static list of categories for now
val transactionCategories = listOf(
    Category("food", "Food", Icons.Default.Restaurant, Color(0xFFFF9800)),
    Category("health", "Health", Icons.Default.HealthAndSafety, Color(0xFFFFC107)),
    Category("salary", "Salary", Icons.Default.Payments, Color(0xFF4CAF50)),
    Category("transport", "Transport", Icons.Default.DirectionsBus, Color(0xFF2196F3)),
    Category("shopping", "Shopping", Icons.Default.ShoppingBag, Color(0xFFE91E63)),
    Category("entertainment", "Entertainment", Icons.Default.MusicVideo, Color(0xFF673AB7)),
    Category("education", "Education", Icons.Default.School, Color(0xFF8BC34A)),
    Category("other", "Other", Icons.Default.Category, Color(0xFF9E9E9E))
)
