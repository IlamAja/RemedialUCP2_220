package com.example.remedialucp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?
)

@Entity(tableName = "book_authors", primaryKeys = ["bookId", "authorId"])
data class BookAuthor(
    val bookId: String,
    val authorId: String
)
