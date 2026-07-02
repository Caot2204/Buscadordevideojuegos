package com.example.buscadordevideojuegos.data.persistance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val context: Context
) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorite_games")

    object FavoriteKeys {
        val FAVORITE_GAMES = stringSetPreferencesKey("favorite_games_ids")
    }

    val favoriteGamesIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FavoriteKeys.FAVORITE_GAMES] ?: emptySet()
    }

    suspend fun saveFavoriteGameId(gameId: Int) {
        val ids = favoriteGamesIds.first().toMutableSet()
        ids.add(gameId.toString())
        context.dataStore.edit { preferences ->
            preferences[FavoriteKeys.FAVORITE_GAMES] = ids
        }
    }

    suspend fun deleteFavoriteGameId(gameId: Int) {
        val ids = favoriteGamesIds.first().toMutableSet()
        ids.remove(gameId.toString())
        context.dataStore.edit { preferences ->
            preferences[FavoriteKeys.FAVORITE_GAMES] = ids
        }
    }

}