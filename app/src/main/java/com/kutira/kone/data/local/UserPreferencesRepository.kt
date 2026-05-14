package com.kutira.kone.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kutira.kone.models.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "kutira_user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ROLE = stringPreferencesKey("user_role")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val roleFlow: Flow<UserRole?> = context.userDataStore.data.map { prefs ->
        UserRole.fromRaw(prefs[Keys.ROLE])
    }

    val onboardingDoneFlow: Flow<Boolean> = context.userDataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_DONE] ?: false
    }

    suspend fun setRole(role: UserRole) {
        context.userDataStore.edit { it[Keys.ROLE] = role.name }
    }

    suspend fun clearRole() {
        context.userDataStore.edit { it.remove(Keys.ROLE) }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.userDataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }
}
