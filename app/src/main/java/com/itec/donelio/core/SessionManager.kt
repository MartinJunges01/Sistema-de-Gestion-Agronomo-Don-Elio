package com.itec.donelio.core

import android.content.Context
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
    suspend fun saveUserName(name: String)
    suspend fun logout()
}

@Singleton
class SessionManagerImpl @Inject constructor(private val context: Context) : SessionManager {
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    override val userName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] ?: "Invitado" }

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[USER_NAME_KEY] = name }
    }

    override suspend fun logout() {
        context.dataStore.edit { it.remove(USER_NAME_KEY) }
    }
}
