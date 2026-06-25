package com.example.buscadordevideojuegos.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscadordevideojuegos.data.network.VideoGameCatalogApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val videoGameCatalogApi: VideoGameCatalogApi
) : ViewModel() {

    private val gameId: Int = try {
        checkNotNull(savedStateHandle[GameDetailsScreenDestination.gameIdArg])
    } catch (_: IllegalStateException) {
        -1
    }

    private val _uiState = MutableStateFlow(GameDetailsScreenUiState())
    val uiState: StateFlow<GameDetailsScreenUiState> = _uiState.asStateFlow()

    init {
        if (gameId != -1 ) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(gameDetails = videoGameCatalogApi.getGameDetails(gameId))
                }
            }
        }
    }
}

data class GameDetailsScreenUiState(
    val gameDetails: GameDetails? = null
)