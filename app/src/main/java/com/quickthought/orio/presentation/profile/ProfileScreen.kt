package com.quickthought.orio.presentation.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.R
import com.quickthought.orio.domain.model.AppTheme
import com.quickthought.orio.presentation.util.OrioTopAppBar
import com.quickthought.orio.ui.components.AdaptiveWrapper
import com.quickthought.orio.ui.theme.OrioTheme
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    ProfileContent(
        modifier = modifier,
        state = state,
        onThemeChange = viewModel::saveThemeSetting,
        onPremiumChange = viewModel::savePremiumStatus,
        onAutoTrackingChange = viewModel::saveAutoTrackingStatus,
        onSaveBudget = { budgetValue ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.onSaveBudget(budgetValue)
            Toast.makeText(context, "Budget saved", Toast.LENGTH_SHORT).show()
            focusManager.clearFocus()
            val amount = budgetValue.toDoubleOrNull() ?: state.monthlyBudget
            if (amount > 0) viewModel.saveMonthlyBudget(amount)
        },
        onSendFeedback = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(
                    Intent.EXTRA_EMAIL, arrayOf(
                        "\n" +
                                "smanjithayas@gmail.com"
                    )
                )
                putExtra(Intent.EXTRA_SUBJECT, "Orio App Feedback")
            }
            try {
                context.startActivity(Intent.createChooser(intent, "Send Feedback"))
            } catch (_: Exception) {
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onThemeChange: (AppTheme) -> Unit,
    onPremiumChange: (Boolean) -> Unit,
    onAutoTrackingChange: (Boolean) -> Unit,
    onSaveBudget: (String) -> Unit,
    onSendFeedback: () -> Unit
) {
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.RECEIVE_SMS] == true ||
                permissions[Manifest.permission.READ_SMS] == true
        if (granted) {
            onAutoTrackingChange(true)
        } else {
            Toast.makeText(context, "Permissions required for auto tracking", Toast.LENGTH_SHORT)
                .show()
            onAutoTrackingChange(false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OrioTopAppBar(
                        title = "Orio - Profile",
                    )
                }
            )
        },
    ) { innerPadding ->
        AdaptiveWrapper(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ThemeSelector(
                    currentTheme = state.appTheme,
                    onThemeChange = onThemeChange
                )

                HorizontalDivider()

                ExperimentalSection(
                    isPremium = state.isPremium,
                    onPremiumChange = onPremiumChange,
                    isAutoTrackingEnabled = state.isAutoTrackingEnabled,
                    onAutoTrackingToggle = { enabled ->
                        if (enabled) {
                            showPrivacyDialog = true
                        } else {
                            onAutoTrackingChange(false)
                        }
                    }
                )

                HorizontalDivider()

                BudgetInput(
                    currentBudget = state.monthlyBudget,
                    onSaveBudget = onSaveBudget
                )

                HorizontalDivider()

                FeedbackSection(
                    onSendFeedback = onSendFeedback
                )
            }
        }

        if (showPrivacyDialog) {
            PrivacyDisclosureDialog(
                onDismiss = { showPrivacyDialog = false },
                onConfirm = {
                    showPrivacyDialog = false
                    val hasReceiveSms = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.RECEIVE_SMS
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasReadSms = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_SMS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasReceiveSms || hasReadSms) {
                        onAutoTrackingChange(true)
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                            )
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Select the App's Theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val themeOptions = AppTheme.entries
            val themeLabels = listOf("System", "Light", "Dark")
            themeOptions.forEachIndexed { index, theme ->
                val isSelected = currentTheme == theme
                val label = themeLabels[index]
                SegmentedButton(
                    selected = isSelected,
                    onClick = { onThemeChange(theme) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = themeOptions.size
                    ),
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
private fun ExperimentalSection(
    isPremium: Boolean,
    onPremiumChange: (Boolean) -> Unit,
    isAutoTrackingEnabled: Boolean,
    onAutoTrackingToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row {
            Icon(
                // Experiments icon
                Icons.Default.Flag,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Experimental Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Orio Premium",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Try premium features for free until stable!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isPremium,
                onCheckedChange = onPremiumChange
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Auto Message Tracking",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Automatically track transactions from SMS.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isAutoTrackingEnabled,
                onCheckedChange = onAutoTrackingToggle
            )
        }
    }
}

@Composable
private fun PrivacyDisclosureDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Privacy Information") },
        text = {
            Text(
                "Auto Message Tracking requires access to your SMS to identify transaction alerts. " +
                        "This data is used ONLY to track your funds and help you manage your budget. " +
                        "No personal messages or other sensitive information are accessed or stored."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Agree & Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BudgetInput(
    currentBudget: Double,
    onSaveBudget: (String) -> Unit
) {
    var budgetValue by remember(currentBudget) {
        mutableStateOf(currentBudget.roundToInt().toString())
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Financial Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = budgetValue,
                onValueChange = { if (it.all { char -> char.isDigit() }) budgetValue = it },
                label = { Text("Monthly Budget (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            ElevatedButton(
                onClick = { onSaveBudget(budgetValue) },
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.save_action))
            }
        }
    }
}

@Composable
private fun FeedbackSection(
    onSendFeedback: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Help us improve",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Have a feature request or found a bug? We'd love to hear from you!",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(
                onClick = onSendFeedback,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Send Feedback")
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun ProfileContentPreview() {
    OrioTheme {
        ProfileContent(
            state = ProfileState(
                monthlyBudget = 10000.0,
                appTheme = AppTheme.DARK,
                isPremium = true,
                isAutoTrackingEnabled = false
            ),
            onThemeChange = {},
            onPremiumChange = {},
            onAutoTrackingChange = {},
            onSaveBudget = {},
            onSendFeedback = {}
        )
    }
}

@Preview(name = "Large Font", fontScale = 1.5f)
@Preview(name = "Dynamic Color", showBackground = true)
@Composable
private fun ProfileContentVariantsPreview() {
    OrioTheme {
        ProfileContent(
            state = ProfileState(
                monthlyBudget = 5000.0,
                appTheme = AppTheme.LIGHT,
                isPremium = false,
                isAutoTrackingEnabled = true
            ),
            onThemeChange = {},
            onPremiumChange = {},
            onAutoTrackingChange = {},
            onSaveBudget = {},
            onSendFeedback = {}
        )
    }
}
