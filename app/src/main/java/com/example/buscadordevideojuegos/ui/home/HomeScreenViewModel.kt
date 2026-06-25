package com.example.buscadordevideojuegos.ui.home

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscadordevideojuegos.data.network.VideoGameCatalogApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Locale.getDefault
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val videoGameCatalogApi: VideoGameCatalogApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    lateinit var videoGamesList: List<VideoGameItem>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            videoGamesList = videoGameCatalogApi.getGames()
            _uiState.update {
                it.copy(videoGames = videoGamesList.shuffled())
            }
        }
    }

    fun searchVideoGame(searchWord: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    videoGames = if (searchWord.isNotEmpty()) {
                        videoGamesList.filter { videogame ->
                            videogame.title.lowercase().contains(searchWord.lowercase())
                        }
                    } else {
                        videoGamesList.shuffled()
                    }
                )
            }
        }
    }
}

data class HomeScreenUiState(
    val videoGames: List<VideoGameItem> = emptyList()
)