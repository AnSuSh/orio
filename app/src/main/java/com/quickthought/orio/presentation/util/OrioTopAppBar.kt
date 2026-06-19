package com.quickthought.orio.presentation.util

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.quickthought.orio.ui.theme.OrioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrioTopAppBar(title: String, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
//                IconButton(onClick = {}) {
//                    Icon(imageVector = Icons.Default.Person, modifier = Modifier
//                        .size(32.dp)
//                        .clip(CircleShape),
//                        tint = OrioGold,
//                        contentDescription = null
//                    )
//                }
//                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun PreviewOrioTopAppBar() {
    OrioTheme {
        OrioTopAppBar("Orio")
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewOrioTopAppBarDark() {
    OrioTheme {
        OrioTopAppBar("Orio")
    }
}