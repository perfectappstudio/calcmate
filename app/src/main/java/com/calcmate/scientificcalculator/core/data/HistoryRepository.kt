package com.calcmate.scientificcalculator.core.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val history: Flow<List<HistoryEntry>> = historyDao.getAll()

    suspend fun addEntry(expression: String, result: String, displayFormat: String) {
        historyDao.insert(
            HistoryEntry(
                expression = expression,
                result = result,
                displayFormat = displayFormat,
            )
        )
    }

    suspend fun deleteEntry(entry: HistoryEntry) {
        historyDao.delete(entry)
    }

    suspend fun clearAll() {
        historyDao.deleteAll()
    }
}
