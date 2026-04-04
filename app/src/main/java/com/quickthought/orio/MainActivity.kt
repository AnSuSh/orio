package com.quickthought.orio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quickthought.orio.presentation.transactions.TransactionsScreen
import com.quickthought.orio.ui.theme.OrioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrioTheme {
                TransactionsScreen()
            }
        }
    }
}
