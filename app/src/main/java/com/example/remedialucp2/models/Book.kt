package com.example.remedialucp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String,
    val title: String,
    val isbn: String,
    val categoryId: String?,
    val physicalCopyId: String,
    val status: BookStatus = BookStatus.AVAILABLE,
    val isDeleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class BookStatus {
    AVAILABLE, BORROWED, DAMAGED, LOST
}