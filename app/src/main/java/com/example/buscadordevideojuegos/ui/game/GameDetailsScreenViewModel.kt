package com.example.buscadordevideojuegos.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscadordevideojuegos.data.network.VideoGameCatalogApi
import com.example.buscadordevideojuegos.data.persistance.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoGameCatalogApi: VideoGameCatalogApi,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val gameId: Int = try {
        checkNotNull(savedStateHandle[GameDetailsScreenDestination.gameIdArg])
    } catch (_: IllegalStateException) {
        -1
    }

    private val _uiState: MutableStateFlow<GameDetailUiState> = MutableStateFlow(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    init {
        loadGameDetails()
    }

    fun loadGameDetails() {
        if (gameId != -1 ) {
            viewModelScope.launch {
                _uiState.value = GameDetailUiState.Loading
                try {
                    _uiState.value = GameDetailUiState.Success(
                        gameDetails = videoGameCatalogApi.getGameDetails(gameId),
                        isFavorite = favoritesRepository.favoriteGamesIds.first().contains(gameId.toString())
                    )
                } catch (e: Exception) {
                    _uiState.value = GameDetailUiState.Error
                }
            }
        }
    }

    fun markFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentState ->
                if (currentState is GameDetailUiState.Success) {
                    favoritesRepository.saveFavoriteGameId(gameId)
                    currentState.copy(
                        isFavorite = true
                    )
                } else {
                    currentState
                }
            }
        }
    }

    fun desmarkFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentState ->
                if (currentState is GameDetailUiState.Success) {
                    favoritesRepository.deleteFavoriteGameId(gameId)
                    currentState.copy(
                        isFavorite = false
                    )
                } else {
                    currentState
                }
            }
        }
    }
}