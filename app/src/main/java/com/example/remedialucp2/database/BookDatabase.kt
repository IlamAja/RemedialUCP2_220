package com.example.remedialucp2.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.remedialucp2.models.Book
import com.example.remedialucp2.models.Author
import com.example.remedialucp2.models.Category
import com.example.remedialucp2.models.AuditLog
import com.example.remedialucp2.models.BookAuthor

@Database(
    entities = [Book::class, Author::class, Category::class,
        BookAuthor::class, AuditLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun categoryDao(): CategoryDao
    abstract fun authorDao(): AuthorDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}