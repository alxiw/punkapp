package io.github.alxiw.punkapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BeersLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val favoriteBeersKey = stringSetPreferencesKey("favorites")

    fun getFavoriteIdsFlow(): Flow<List<Int>> = dataStore.data.map { preferences ->
        preferences[favoriteBeersKey]?.map { it.toInt() } ?: emptyList()
    }

    suspend fun getFavoriteIds(): List<Int> {
        val preferences = dataStore.data.first()
        return preferences[favoriteBeersKey]?.map { it.toInt() } ?: emptyList()
    }

    suspend fun isFavorite(id: Int): Boolean {
        val preferences = dataStore.data.first()
        return preferences[favoriteBeersKey]?.contains(id.toString()) ?: false
    }

    suspend fun addFavorite(id: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteBeersKey] ?: emptySet()
            preferences[favoriteBeersKey] = currentFavorites + id.toString()
        }
    }

    suspend fun removeFavorite(id: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteBeersKey] ?: emptySet()
            preferences[favoriteBeersKey] = currentFavorites - id.toString()
        }
    }
}
