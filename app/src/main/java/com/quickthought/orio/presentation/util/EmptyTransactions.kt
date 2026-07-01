package com.quickthought.orio.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun EmptyTransactionsState() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Text("No transactions yet", color = Color.Gray)
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun EmptyTransactionsStatePreview() {
    OrioTheme {
        EmptyTransactionsState()
    }
}

@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
private fun EmptyTransactionsStateLargeFontPreview() {
    OrioTheme {
        EmptyTransactionsState()
    }
}
