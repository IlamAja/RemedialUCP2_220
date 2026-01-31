package com.example.remedialucp2.utils

import com.example.remedialucp2.models.Category
import com.example.remedialucp2.database.DatabaseHelper

class CategoryTreeHelper(private val dbHelper: DatabaseHelper) {

    suspend fun hasCycle(categoryId: String, parentId: String): Boolean {
        if (parentId.isBlank()) return false

        var currentId = parentId
        val visited = mutableSetOf<String>()

        while (currentId.isNotEmpty()) {
            if (currentId == categoryId) return true
            if (visited.contains(currentId)) return true

            visited.add(currentId)
            val category = dbHelper.getCategoryById(currentId)
            currentId = category?.parentId ?: ""
        }

        return false
    }

    suspend fun getAllSubCategories(categoryId: String): List<Category> {
        val allSubCategories = mutableListOf<Category>()
        getSubCategoriesRecursive(categoryId, allSubCategories)
        return allSubCategories
    }

    private suspend fun getSubCategoriesRecursive(parentId: String, result: MutableList<Category>) {
        val children = dbHelper.getChildCategories(parentId)
        result.addAll(children)

        for (child in children) {
            getSubCategoriesRecursive(child.id, result)
        }
    }

    suspend fun getAllBooksInCategoryTree(categoryId: String): List<com.example.remedialucp2.models.Book> {
        val allBooks = mutableListOf<com.example.remedialucp2.models.Book>()
        val categories = getAllSubCategories(categoryId).toMutableList()
        categories.add(Category(categoryId, "", null, false))

        for (category in categories) {
            val books = dbHelper.getBooksByCategory(category.id)
            allBooks.addAll(books)
        }

        return allBooks
    }
}
