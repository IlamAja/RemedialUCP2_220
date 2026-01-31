package com.example.remedialucp2.utils

import com.example.remedialucp2.database.BookDatabase
import com.example.remedialucp2.models.AuditLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuditLogger(private val database: BookDatabase) {

    suspend fun logChange(
        tableName: String,
        recordId: String,
        action: String,
        oldData: String? = null,
        newData: String? = null,
        userId: String = "system"
    ) {
        withContext(Dispatchers.IO) {
            val auditLog = AuditLog(
                tableName = tableName,
                recordId = recordId,
                action = action,
                oldData = oldData,
                newData = newData,
                userId = userId
            )
            database.auditLogDao().insert(auditLog)
        }
    }
}