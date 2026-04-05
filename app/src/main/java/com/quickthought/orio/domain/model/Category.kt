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
import androidx.compose.ui.graphics.vector.ImageVector

// domain/model/Category.kt
data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

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
