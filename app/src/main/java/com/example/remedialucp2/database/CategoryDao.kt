package com.example.remedialucp2.database

import androidx.room.*
import com.example.remedialucp2.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories WHERE isDeleted = 0")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): Category?

    @Query("SELECT * FROM categories WHERE parentId = :parentId AND isDeleted = 0")
    suspend fun getChildCategories(parentId: String): List<Category>

    @Query("SELECT * FROM categories WHERE parentId IS NULL AND isDeleted = 0")
    suspend fun getRootCategories(): List<Category>

    @Query("UPDATE categories SET isDeleted = 1 WHERE id = :categoryId")
    suspend fun softDelete(categoryId: String)

    @Query("SELECT COUNT(*) FROM books WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun countBooksInCategory(categoryId: String): Int
}