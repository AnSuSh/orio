package com.quickthought.orio.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_settings")
    private val budgetKey = doublePreferencesKey("monthly_budget")
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    // Read the budget (returns a Flow)
    val monthlyBudget: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[budgetKey] ?: 5000.0 // Default value
        }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[darkModeKey] ?: false // Default value
        }

    // Save the budget
    suspend fun saveBudget(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[budgetKey] = amount
        }
    }

    suspend fun saveDarkModeSetting(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[darkModeKey] = isDarkMode
        }
    }
}