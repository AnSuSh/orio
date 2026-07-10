package com.quickthought.orio.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Accounts : Screen("accounts", "Accounts", Icons.Default.AccountBalanceWallet)
    object Transactions : Screen("transactions", "History", Icons.Default.Receipt)
    object Analytics : Screen("analytics", "Insights", Icons.Default.BarChart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object CategoryManagement : Screen("category_management", "Manage Categories", Icons.Default.Settings)
}