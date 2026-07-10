package com.quickthought.orio.domain.use_case.category

import com.quickthought.orio.domain.model.Category
import com.quickthought.orio.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryUseCases @Inject constructor(
    val getAllCategories: GetAllCategoriesUseCase,
    val addCategory: AddCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val getCategoryById: GetCategoryByIdUseCase,
    val prePopulateCategories: PrePopulateCategoriesUseCase
)

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getAllCategories()
    }
}

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.insertCategory(category)
    }
}

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.updateCategory(category)
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String): Category? {
        return repository.getCategoryById(id)
    }
}

class PrePopulateCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categories: List<Category>) {
        repository.insertCategories(categories)
    }
}
