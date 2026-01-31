package com.example.remedialucp2.utils

import com.example.remedialucp2.models.Book
import com.example.remedialucp2.models.Category

object DataValidator {

    fun validateBook(book: Book): ValidationResult {
        return when {
            book.id.isBlank() -> ValidationResult(false, "ID buku tidak boleh kosong")
            book.title.isBlank() -> ValidationResult(false, "Judul buku tidak boleh kosong")
            book.isbn.isBlank() -> ValidationResult(false, "ISBN tidak boleh kosong")
            book.physicalCopyId.isBlank() -> ValidationResult(false, "ID fisik buku tidak boleh kosong")
            else -> ValidationResult(true, "Valid")
        }
    }

    fun validateCategory(category: Category): ValidationResult {
        return when {
            category.id.isBlank() -> ValidationResult(false, "ID kategori tidak boleh kosong")
            category.name.isBlank() -> ValidationResult(false, "Nama kategori tidak boleh kosong")
            else -> ValidationResult(true, "Valid")
        }
    }
}