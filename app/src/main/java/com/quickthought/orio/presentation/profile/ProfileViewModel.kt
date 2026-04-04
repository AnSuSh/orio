package com.quickthought.orio.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.data.local.PreferenceManager
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
        observeDarkModeSetting()
    }

    private fun observeBudget() {
        viewModelScope.launch {
            preferenceManager.monthlyBudget.collect { savedBudget ->
                _state.update { it.copy(monthlyBudget = savedBudget) }
            }
        }
    }

    private fun observeDarkModeSetting(){
        viewModelScope.launch {
            preferenceManager.isDarkMode.collect { isDarkMode ->
                _state.update { it.copy(isDarkMode = isDarkMode) }
            }
        }
    }

    fun saveMonthlyBudget(newAmount: Double) {
        viewModelScope.launch {
            // This saves it to local storage permanently
            preferenceManager.saveBudget(newAmount)
        }
    }

    fun saveDarkModeSetting(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferenceManager.saveDarkModeSetting(isDarkMode)
        }
    }
}