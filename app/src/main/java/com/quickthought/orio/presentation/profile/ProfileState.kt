package com.quickthought.orio.presentation.profile

data class ProfileState(
    val monthlyBudget: Double = 5000.0,
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
