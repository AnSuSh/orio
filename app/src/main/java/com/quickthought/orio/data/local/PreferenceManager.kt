package com.quickthought.orio.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickthought.orio.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_settings")
    private val budgetKey = doublePreferencesKey("monthly_budget")
    private val themeKey = intPreferencesKey("app_theme")
    private val isPremiumKey = booleanPreferencesKey("is_premium")
    private val isAutoTrackingEnabledKey = booleanPreferencesKey("is_auto_tracking_enabled")
    private val categoriesInitializedKey = booleanPreferencesKey("categories_initialized")

    // Read the budget (returns a Flow)
    val monthlyBudget: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[budgetKey] ?: 5000.0 // Default value
        }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeIndex = preferences[themeKey] ?: AppTheme.FOLLOW_SYSTEM.ordinal
            AppTheme.entries.getOrElse(themeIndex) { AppTheme.FOLLOW_SYSTEM }
        }

    val isPremium: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[isPremiumKey] ?: false // Default value
        }

    val isAutoTrackingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[isAutoTrackingEnabledKey] ?: false // Default value
        }

    val categoriesInitialized: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[categoriesInitializedKey] ?: false
        }

    // Save the budget
    suspend fun saveBudget(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[budgetKey] = amount
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.ordinal
        }
    }

    suspend fun savePremiumStatus(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isPremiumKey] = isPremium
        }
    }

    suspend fun saveAutoTrackingStatus(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isAutoTrackingEnabledKey] = isEnabled
        }
    }

    suspend fun setCategoriesInitialized(initialized: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[categoriesInitializedKey] = initialized
        }
    }
}