package com.example.remedialucp2.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MigrationService {

    fun migrateOldData(oldData: List<Any>, onProgress: (Int) -> Unit, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val total = oldData.size

            oldData.forEachIndexed { index, data ->
                Thread.sleep(100)
                val progress = ((index + 1) * 100) / total
                onProgress(progress)
            }

            onComplete()
        }
    }
}