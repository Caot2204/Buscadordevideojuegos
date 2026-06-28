package com.example.buscadordevideojuegos.ui.home

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
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val videoGameCatalogApi: VideoGameCatalogApi
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeScreenUiState> = MutableStateFlow(HomeScreenUiState.Loading)
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private var videoGamesList: List<VideoGameItem> = emptyList()

    init {
        loadVideogames()
    }

    fun loadVideogames() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeScreenUiState.Loading
            try {
                videoGamesList = videoGameCatalogApi.getGames()
                val categories = mutableListOf("All")
                categories.addAll(
                    videoGamesList.map { videogame ->
                        videogame.genre
                    }.distinct().shuffled()
                )
                _uiState.value = HomeScreenUiState.Success(
                    videoGames = videoGamesList.shuffled(),
                    categories = categories
                )
            } catch (e: Exception) {
                _uiState.value = HomeScreenUiState.Error
            }
        }
    }

    fun searchVideoGame(searchWord: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentState ->
                if (currentState is HomeScreenUiState.Success) {
                    currentState.copy(
                        categorySelected = "All",
                        videoGames = if (searchWord.isNotEmpty()) {
                            videoGamesList.filter { videogame ->
                                videogame.title.lowercase().contains(searchWord.lowercase())
                            }
                        } else {
                            videoGamesList.shuffled()
                        }
                    )
                } else {
                    currentState
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentState ->
                if (currentState is HomeScreenUiState.Success) {
                    currentState.copy(
                        videoGames = if (category == "All") {
                            videoGamesList.shuffled()
                        } else {
                            videoGamesList.filter { videogame ->
                                videogame.genre == category
                            }
                        },
                        categorySelected = category
                    )
                } else {
                    currentState
                }
            }
        }
    }
}

sealed interface HomeScreenUiState {
    data class Success(
        val categorySelected: String = "All",
        val videoGames: List<VideoGameItem> = emptyList(),
        val categories: List<String> = emptyList()
    ) : HomeScreenUiState
    object Error : HomeScreenUiState
    object Loading : HomeScreenUiState
}
