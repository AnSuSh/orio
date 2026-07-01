package com.quickthought.orio.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.quickthought.orio.ui.theme.OrioTheme

@Composable
fun SmsTrackingNudge(
    onEnableClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Automate Tracking",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Enable SMS tracking to auto-log your bank transactions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onEnableClick) {
                Text("Enable")
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun SmsTrackingNudgePreview() {
    OrioTheme {
        SmsTrackingNudge(onEnableClick = {})
    }
}

@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
private fun SmsTrackingNudgeLargeFontPreview() {
    OrioTheme {
        SmsTrackingNudge(onEnableClick = {})
    }
}
