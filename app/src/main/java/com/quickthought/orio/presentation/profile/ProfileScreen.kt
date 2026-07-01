package com.quickthought.orio.presentation.profile

import android.content.Intent
import android.widget.Toast
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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickthought.orio.R
import com.quickthought.orio.domain.model.AppTheme
import com.quickthought.orio.presentation.util.OrioTopAppBar
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
    onSaveBudget: (String) -> Unit,
    onSendFeedback: () -> Unit
) {
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            PremiumToggle(
                isPremium = state.isPremium,
                onPremiumChange = onPremiumChange
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
private fun PremiumToggle(
    isPremium: Boolean,
    onPremiumChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Orio Premium",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
                isPremium = true
            ),
            onThemeChange = {},
            onPremiumChange = {},
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
                isPremium = false
            ),
            onThemeChange = {},
            onPremiumChange = {},
            onSaveBudget = {},
            onSendFeedback = {}
        )
    }
}
