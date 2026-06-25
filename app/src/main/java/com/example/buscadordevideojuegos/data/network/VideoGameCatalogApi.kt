package com.example.buscadordevideojuegos.data.network

import com.example.buscadordevideojuegos.ui.game.GameDetails
import com.example.buscadordevideojuegos.ui.home.VideoGameItem
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoGameCatalogApi {
    @GET("games")
    suspend fun getGames(): List<VideoGameItem>

    @GET("game")
    suspend fun getGameDetails(@Query("id") gameId: Int): GameDetails
}