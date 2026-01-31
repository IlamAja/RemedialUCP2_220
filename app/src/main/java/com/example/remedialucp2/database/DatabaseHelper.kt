package com.example.remedialucp2.database

import androidx.room.withTransaction
import com.example.remedialucp2.models.Book
import com.example.remedialucp2.models.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(private val database: BookDatabase) {

    suspend fun insertBook(book: Book) {
        withContext(Dispatchers.IO) {
            database.bookDao().insert(book)
        }
    }

    suspend fun getCategoryById(categoryId: String): Category? {
        return withContext(Dispatchers.IO) {
            database.categoryDao().getCategoryById(categoryId)
        }
    }

    suspend fun getChildCategories(parentId: String): List<Category> {
        return withContext(Dispatchers.IO) {
            database.categoryDao().getChildCategories(parentId)
        }
    }

    suspend fun getBooksByCategory(categoryId: String): List<Book> {
        return withContext(Dispatchers.IO) {
            database.bookDao().getBooksByCategory(categoryId)
        }
    }

    suspend fun getBorrowedBooksByCategory(categoryId: String): List<Book> {
        return withContext(Dispatchers.IO) {
            database.bookDao().getBorrowedBooksByCategory(categoryId)
        }
    }

    suspend fun deleteCategoryWithTransaction(categoryId: String, deleteBooks: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                database.withTransaction {
                    val borrowedBooks = database.bookDao().getBorrowedBooksByCategory(categoryId)

                    if (borrowedBooks.isNotEmpty()) {
                        throw IllegalStateException("Tidak bisa menghapus kategori karena ada buku yang sedang dipinjam")
                    }

                    if (deleteBooks) {
                        val books = database.bookDao().getBooksByCategory(categoryId)
                        books.forEach { book ->
                            database.bookDao().softDelete(book.id)
                        }
                    } else {
                        database.bookDao().removeCategoryFromBooks(categoryId)
                    }

                    database.categoryDao().softDelete(categoryId)
                    true
                }
            } catch (e: Exception) {
                false
            }
        }
    }
}