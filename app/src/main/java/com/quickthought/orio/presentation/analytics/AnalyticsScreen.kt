package com.quickthought.orio.presentation.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.presentation.analytics.components.AnalyticsContent
import com.quickthought.orio.presentation.analytics.components.PremiumLockScreen
import com.quickthought.orio.presentation.util.OrioTopAppBar
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AnalyticsScreenContent(
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(
    state: AnalyticsState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            OrioTopAppBar(title = "Orio - Analytics")
        }
    ) { padding ->
        if (!state.isPremium) {
            PremiumLockScreen(modifier = Modifier.padding(padding))
        } else {
            AnalyticsContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun AnalyticsScreenContentUnlockedPreview() {
    OrioTheme {
        AnalyticsScreenContent(
            state = AnalyticsState(
                isPremium = true,
                categorySpending = mapOf(
                    "Food" to 1500.0,
                    "Transport" to 800.0,
                    "Shopping" to 2000.0
                )
            )
        )
    }
}

@Preview(name = "Locked - Large Font", fontScale = 1.5f)
@Preview(name = "Locked - Dynamic Color", showBackground = true)
@Composable
private fun AnalyticsScreenContentLockedPreview() {
    OrioTheme {
        AnalyticsScreenContent(
            state = AnalyticsState(
                isPremium = false
            )
        )
    }
}
