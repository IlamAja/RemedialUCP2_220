package com.example.remedialucp2.database

import androidx.room.*

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(author: com.example.remedialucp2.models.Author)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthor(bookAuthor: com.example.remedialucp2.models.BookAuthor)

    @Query("SELECT * FROM authors WHERE id IN (SELECT authorId FROM book_authors WHERE bookId = :bookId)")
    suspend fun getAuthorsByBookId(bookId: String): List<com.example.remedialucp2.models.Author>
}