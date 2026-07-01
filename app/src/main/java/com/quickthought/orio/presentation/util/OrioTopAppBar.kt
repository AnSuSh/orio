package com.quickthought.orio.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.quickthought.orio.BuildConfig
import com.quickthought.orio.ui.theme.OrioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrioTopAppBar(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (BuildConfig.DEBUG) {
                    Text(
                        text = "v${BuildConfig.VERSION_NAME} (Dev build)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun OrioTopAppBarPreview() {
    OrioTheme {
        OrioTopAppBar("Orio Dashboard")
    }
}

@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
private fun OrioTopAppBarLargeFontPreview() {
    OrioTheme {
        OrioTopAppBar("Orio Transactions")
    }
}
