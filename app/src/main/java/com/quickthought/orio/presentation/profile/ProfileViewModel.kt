package com.quickthought.orio.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
import com.quickthought.orio.domain.model.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // Using StateFlow for reactive UI updates
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeBudget()
        observeThemeSetting()
        observePremiumStatus()
    }

    private fun observeBudget() {
        viewModelScope.launch {
            preferenceManager.monthlyBudget.collect { savedBudget ->
                _state.update { it.copy(monthlyBudget = savedBudget) }
            }
        }
    }

    private fun observeThemeSetting() {
        viewModelScope.launch {
            preferenceManager.appTheme.collect { theme ->
                _state.update { it.copy(appTheme = theme) }
            }
        }
    }

    private fun observePremiumStatus() {
        viewModelScope.launch {
            preferenceManager.isPremium.collect { isPremium ->
                _state.update { it.copy(isPremium = isPremium) }
            }
        }
    }

    fun onSaveBudget(input: String) {
        val amount = input.toDoubleOrNull()
        if (amount != null && amount > 0) {
            // Perform saving logic...
            _state.value = _state.value.copy(saveSuccess = true)
        } else {
            _state.value = _state.value.copy(error = "Invalid amount")
        }
    }

    fun saveMonthlyBudget(newAmount: Double) {
        viewModelScope.launch {
            // This saves it to local storage permanently
            preferenceManager.saveBudget(newAmount)
        }
    }

    fun saveThemeSetting(theme: AppTheme) {
        viewModelScope.launch {
            preferenceManager.saveTheme(theme)
        }
    }

    fun savePremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            preferenceManager.savePremiumStatus(isPremium)
        }
    }
}