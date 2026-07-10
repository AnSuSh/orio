package com.quickthought.orio.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickthought.orio.domain.model.Category
import com.quickthought.orio.domain.use_case.category.CategoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases
) : ViewModel() {

    val categories = categoryUseCases.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCategory(category: Category) {
        viewModelScope.launch {
            categoryUseCases.addCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryUseCases.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryUseCases.deleteCategory(category)
        }
    }
}
