package com.example.buscadordevideojuegos.ui.game

sealed interface GameDetailUiState {
    data class Success(val gameDetails: GameDetails) : GameDetailUiState
    object Error : GameDetailUiState
    object Loading : GameDetailUiState
}

data class GameDetails(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val status: String,
    val short_description: String,
    val description: String,
    val game_url: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    val release_date: String,
    val freetogame_profile_url: String,
    val minimum_system_requirements: MinimumSystemRequirements?,
    val screenshots: List<Screenshot>,
)

data class MinimumSystemRequirements(
    val os: String?,
    val processor: String?,
    val memory: String?,
    val graphics: String?,
    val storage: String?,
)

data class Screenshot(
    val id: Int,
    val image: String,
)
