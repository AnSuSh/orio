package com.quickthought.orio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quickthought.orio.presentation.Screen
import com.quickthought.orio.presentation.analytics.AnalyticsScreen
import com.quickthought.orio.presentation.home.HomeScreen
import com.quickthought.orio.presentation.profile.ProfileScreen
import com.quickthought.orio.presentation.profile.ProfileViewModel
import com.quickthought.orio.presentation.transactions.TransactionsScreen
import com.quickthought.orio.ui.theme.OrioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            val state by profileViewModel.state.collectAsStateWithLifecycle()

            MainAppScreen(state.isDarkMode)
        }
    }
}

@Composable
fun MainAppScreen(
    isDarkMode: Boolean = false
) {
    OrioTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val items = listOf(Screen.Home, Screen.Transactions, Screen.Analytics, Screen.Profile)

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.route) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .consumeWindowInsets(WindowInsets.navigationBars) // Only consume bottom bar insets
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Transactions.route) { TransactionsScreen() }
                composable(Screen.Analytics.route) { AnalyticsScreen() }
                composable(Screen.Profile.route) { ProfileScreen() }
            }
        }
    }
}
