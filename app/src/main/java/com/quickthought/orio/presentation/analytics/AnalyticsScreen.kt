package com.quickthought.orio.presentation.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.presentation.analytics.components.AnalyticsContent
import com.quickthought.orio.presentation.analytics.components.PremiumLockScreen
import com.quickthought.orio.presentation.util.OrioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OrioTopAppBar(title = "Orio - Analytics")
        }
    ) { padding ->
        if (!state.isPremium) {
            PremiumLockScreen(modifier = Modifier.padding(padding))
        } else {
            AnalyticsContent(
                categorySpending = state.categorySpending,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

