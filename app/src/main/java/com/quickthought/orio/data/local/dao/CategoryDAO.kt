package com.quickthought.orio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickthought.orio.data.local.entity.CategoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryData>)

    @Update
    suspend fun updateCategory(category: CategoryData)

    @Delete
    suspend fun deleteCategory(category: CategoryData)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryData?
}
