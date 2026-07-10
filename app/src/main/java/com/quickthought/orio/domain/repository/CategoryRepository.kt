package com.quickthought.orio.domain.repository

import com.quickthought.orio.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun insertCategories(categories: List<Category>)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun getCategoryById(id: String): Category?
}
