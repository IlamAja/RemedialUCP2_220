package com.example.remedialucp2.services

import com.example.remedialucp2.models.Book
import com.example.remedialucp2.models.BookStatus
import com.example.remedialucp2.database.BookDatabase
import com.example.remedialucp2.utils.DataValidator
import com.example.remedialucp2.utils.AuditLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookService(private val database: BookDatabase) {

    private val auditLogger = AuditLogger(database)
    private val categoryService = CategoryService(database)

    suspend fun addBook(book: Book): Result<Boolean> {
        val validation = DataValidator.validateBook(book)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.message))
        }

        return withContext(Dispatchers.IO) {
            try {
                database.bookDao().insert(book)
                auditLogger.logChange("books", book.id, "INSERT", newData = book.toString())
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBooksByCategoryRecursive(categoryId: String): List<Book> {
        return withContext(Dispatchers.IO) {
            val categoryIds = mutableListOf(categoryId)
            categoryIds.addAll(categoryService.getAllSubCategoryIdsRecursive(categoryId))
            
            val allBooks = mutableListOf<Book>()
            categoryIds.forEach { id ->
                allBooks.addAll(database.bookDao().getBooksByCategory(id))
            }
            allBooks
        }
    }

    suspend fun getBooksByCategory(categoryId: String): List<Book> {
        return withContext(Dispatchers.IO) {
            database.bookDao().getBooksByCategory(categoryId)
        }
    }

    suspend fun deleteBook(bookId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val book = database.bookDao().getBookById(bookId)
                database.bookDao().softDelete(bookId)
                auditLogger.logChange("books", bookId, "SOFT_DELETE", oldData = book?.toString())
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    suspend fun updateBook(book: Book): Result<Boolean> {
        val validation = DataValidator.validateBook(book)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.message))
        }

        return withContext(Dispatchers.IO) {
            try {
                val oldBook = database.bookDao().getBookById(book.id)
                database.bookDao().update(book)
                auditLogger.logChange("books", book.id, "UPDATE", oldData = oldBook?.toString(), newData = book.toString())
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
