package com.quickthought.orio.data.repository

import com.quickthought.orio.data.local.dao.CategoryDAO
import com.quickthought.orio.domain.model.Category
import com.quickthought.orio.domain.model.toCategory
import com.quickthought.orio.domain.model.toCategoryData
import com.quickthought.orio.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDAO
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { list ->
            list.map { it.toCategory() }
        }
    }

    override suspend fun insertCategory(category: Category) {
        dao.insertCategory(category.toCategoryData())
    }

    override suspend fun insertCategories(categories: List<Category>) {
        dao.insertCategories(categories.map { it.toCategoryData() })
    }

    override suspend fun updateCategory(category: Category) {
        dao.updateCategory(category.toCategoryData())
    }

    override suspend fun deleteCategory(category: Category) {
        dao.deleteCategory(category.toCategoryData())
    }

    override suspend fun getCategoryById(id: String): Category? {
        return dao.getCategoryById(id)?.toCategory()
    }
}
