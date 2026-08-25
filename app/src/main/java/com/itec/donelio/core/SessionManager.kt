package com.itec.donelio.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_session")

interface SessionManager {
    val userName: Flow<String>
    val isLoggedIn: Flow<Boolean>
    suspend fun saveUserName(name: String)
    suspend fun logout()
}

@Singleton
class SessionManagerImpl @Inject constructor(private val context: Context) : SessionManager {
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    override val userName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] ?: "Invitado" }

    override val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_LOGGED_IN_KEY] ?: false }

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_NAME_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }
}
