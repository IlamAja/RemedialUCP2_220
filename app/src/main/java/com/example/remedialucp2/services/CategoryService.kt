package com.example.remedialucp2.services

import com.example.remedialucp2.models.Category
import com.example.remedialucp2.database.BookDatabase
import com.example.remedialucp2.utils.DataValidator
import com.example.remedialucp2.utils.AuditLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryService(private val database: BookDatabase) {

    private val auditLogger = AuditLogger(database)

    suspend fun addCategory(category: Category): Result<Boolean> {
        val validation = DataValidator.validateCategory(category)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.message))
        }

        return withContext(Dispatchers.IO) {
            try {
                // Check for cyclic reference if parentId is provided
                if (category.parentId != null) {
                    if (hasCyclicReference(category.id, category.parentId)) {
                        return@withContext Result.failure(IllegalArgumentException("Terdeteksi cyclic reference pada struktur kategori"))
                    }
                }

                database.categoryDao().insert(category)
                auditLogger.logChange("categories", category.id, "INSERT", newData = category.toString())
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateCategory(category: Category): Result<Boolean> {
        val validation = DataValidator.validateCategory(category)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.message))
        }

        return withContext(Dispatchers.IO) {
            try {
                val oldCategory = database.categoryDao().getCategoryById(category.id)
                
                // Check for cyclic reference
                if (category.parentId != null) {
                    if (hasCyclicReference(category.id, category.parentId)) {
                        return@withContext Result.failure(IllegalArgumentException("Terdeteksi cyclic reference pada struktur kategori"))
                    }
                }

                database.categoryDao().insert(category) // Using insert with REPLACE strategy
                auditLogger.logChange("categories", category.id, "UPDATE", oldData = oldCategory?.toString(), newData = category.toString())
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun hasCyclicReference(categoryId: String, parentId: String): Boolean {
        if (categoryId == parentId) return true
        
        var currentParentId: String? = parentId
        val visited = mutableSetOf<String>()
        visited.add(categoryId)

        while (currentParentId != null) {
            if (visited.contains(currentParentId)) return true
            visited.add(currentParentId)
            val parent = database.categoryDao().getCategoryById(currentParentId)
            currentParentId = parent?.parentId
        }
        return false
    }

    suspend fun getAllSubCategoryIdsRecursive(parentId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val result = mutableListOf<String>()
            collectSubCategoryIds(parentId, result)
            result
        }
    }

    private suspend fun collectSubCategoryIds(parentId: String, result: MutableList<String>) {
        val children = database.categoryDao().getChildCategories(parentId)
        for (child in children) {
            result.add(child.id)
            collectSubCategoryIds(child.id, result)
        }
    }

    suspend fun getCategoryById(id: String): Category? {
        return withContext(Dispatchers.IO) {
            database.categoryDao().getCategoryById(id)
        }
    }
}
