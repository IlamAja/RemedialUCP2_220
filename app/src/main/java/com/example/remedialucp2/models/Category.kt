package com.example.remedialucp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val parentId: String? = null,
    val isDeleted: Boolean = false
)