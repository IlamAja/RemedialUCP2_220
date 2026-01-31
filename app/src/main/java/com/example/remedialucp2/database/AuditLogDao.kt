package com.example.remedialucp2.database

import androidx.room.*
import com.example.remedialucp2.models.AuditLog

@Dao
interface AuditLogDao {
    @Insert
    suspend fun insert(auditLog: AuditLog)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentLogs(): List<AuditLog>

    @Query("SELECT * FROM audit_logs WHERE tableName = :tableName AND recordId = :recordId ORDER BY timestamp DESC")
    suspend fun getLogsForRecord(tableName: String, recordId: String): List<AuditLog>
}