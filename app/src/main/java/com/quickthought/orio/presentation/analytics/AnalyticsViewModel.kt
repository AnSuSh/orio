package com.quickthought.orio.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.use_case.analytics.GetCategorySpendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    getCategorySpendingUseCase: GetCategorySpendingUseCase,
    preferenceManager: PreferenceManager
) : ViewModel() {

    val state: StateFlow<AnalyticsState> = combine(
        getCategorySpendingUseCase(),
        preferenceManager.isPremium
    ) { categorySpending, isPremium ->
        AnalyticsState(
            categorySpending = categorySpending,
            isPremium = isPremium
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsState()
    )
}

data class AnalyticsState(
    val categorySpending: Map<String, Double> = emptyMap(),
    val isPremium: Boolean = false
)
