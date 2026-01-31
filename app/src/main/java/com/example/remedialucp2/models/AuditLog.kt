package com.example.remedialucp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tableName: String,
    val recordId: String,
    val action: String,
    val oldData: String?,
    val newData: String?,
    val userId: String,
    val timestamp: Date = Date()
)