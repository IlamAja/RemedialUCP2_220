package com.example.remedialucp2.database

import androidx.room.*
import com.example.remedialucp2.models.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM books WHERE isDeleted = 0")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: String): Book?

    @Query("SELECT * FROM books WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun getBooksByCategory(categoryId: String): List<Book>

    @Query("SELECT * FROM books WHERE status = 'BORROWED' AND categoryId = :categoryId AND isDeleted = 0")
    suspend fun getBorrowedBooksByCategory(categoryId: String): List<Book>

    @Update
    suspend fun update(book: Book)

    @Query("UPDATE books SET isDeleted = 1 WHERE id = :bookId")
    suspend fun softDelete(bookId: String)

    @Query("UPDATE books SET categoryId = NULL WHERE categoryId = :categoryId")
    suspend fun removeCategoryFromBooks(categoryId: String)
}