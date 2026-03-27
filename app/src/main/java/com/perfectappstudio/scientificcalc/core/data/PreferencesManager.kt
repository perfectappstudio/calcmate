package com.perfectappstudio.scientificcalc.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calcmate_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DISPLAY_FORMAT = stringPreferencesKey("display_format")
        val ANGLE_UNIT = stringPreferencesKey("angle_unit")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
    }

    val themeMode: Flow<String>
        get() = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }

    val displayFormat: Flow<String>
        get() = context.dataStore.data.map { it[Keys.DISPLAY_FORMAT] ?: "decimal" }

    val angleUnit: Flow<String>
        get() = context.dataStore.data.map { it[Keys.ANGLE_UNIT] ?: "degree" }

    val hapticEnabled: Flow<Boolean>
        get() = context.dataStore.data.map { it[Keys.HAPTIC_ENABLED] ?: true }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setDisplayFormat(format: String) {
        context.dataStore.edit { it[Keys.DISPLAY_FORMAT] = format }
    }

    suspend fun setAngleUnit(unit: String) {
        context.dataStore.edit { it[Keys.ANGLE_UNIT] = unit }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_ENABLED] = enabled }
    }
}
