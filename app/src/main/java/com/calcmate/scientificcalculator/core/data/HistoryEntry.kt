package com.calcmate.scientificcalculator.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expression: String,
    val result: String,
    val displayFormat: String, // "decimal", "fraction", "scientific"
    val timestamp: Long = System.currentTimeMillis(),
)
