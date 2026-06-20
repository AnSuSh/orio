package com.quickthought.orio.presentation.profile

import com.quickthought.orio.domain.model.AppTheme

data class ProfileState(
    val monthlyBudget: Double = 5000.0,
    val appTheme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)
