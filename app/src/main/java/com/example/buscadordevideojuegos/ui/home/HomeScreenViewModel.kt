package com.example.buscadordevideojuegos.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscadordevideojuegos.data.network.VideoGameCatalogApi
import com.example.buscadordevideojuegos.data.persistance.FavoritesRepository
import com.example.buscadordevideojuegos.ui.navigation.Tabs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val videoGameCatalogApi: VideoGameCatalogApi,
    favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _videoGamesFullList = MutableStateFlow<List<VideoGameItem>>(emptyList())
    private val _searchWord = MutableStateFlow("")
    private val _categorySelected = MutableStateFlow("All")
    private val _selectedTab = MutableStateFlow(Tabs.Videogames)
    private val _isLoading = MutableStateFlow(true)
    private val _isError = MutableStateFlow(false)

    private val _filtersFlow = combine(_searchWord, _categorySelected, _selectedTab) { query, category, tab ->
        FilterState(query, category, tab)
    }

    val uiState: StateFlow<HomeScreenUiState> = combine(
        _videoGamesFullList,
        _filtersFlow,
        favoritesRepository.favoriteGamesIds,
        _isLoading,
        _isError
    ) { games, filters, favorites, loading, error ->
        when {
            error -> HomeScreenUiState.Error
            loading -> HomeScreenUiState.Loading
            else -> {
                val filteredGames = games.filter { game ->
                    val matchesQuery = game.title.lowercase().contains(filters.query.lowercase())
                    val matchesCategory = filters.category == "All" || game.genre == filters.category
                    val matchesTab = if (filters.tab == Tabs.Favorites) {
                        game.id.toString() in favorites
                    } else {
                        true
                    }
                    matchesQuery && matchesCategory && matchesTab
                }

                HomeScreenUiState.Success(
                    searchWord = _searchWord.value,
                    categorySelected = filters.category,
                    videoGames = filteredGames,
                    categories = listOf("All") + games.map { it.genre }.distinct().sorted(),
                    selectedTab = filters.tab
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenUiState.Loading
    )

    init {
        loadVideogames()
    }

    fun loadVideogames() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _isError.value = false
            try {
                _videoGamesFullList.value = videoGameCatalogApi.getGames().shuffled()
            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchVideoGame(searchWord: String) {
        _searchWord.value = searchWord
        _categorySelected.value = "All"
    }

    fun filterByCategory(category: String) {
        _categorySelected.value = category
    }

    fun changeTab(tab: Tabs) {
        _selectedTab.value = tab
        _categorySelected.value = "All"
        _searchWord.value = ""
    }

    private data class FilterState(
        val query: String,
        val category: String,
        val tab: Tabs
    )
}

sealed interface HomeScreenUiState {
    data class Success(
        val searchWord: String = "",
        val categorySelected: String = "All",
        val videoGames: List<VideoGameItem> = emptyList(),
        val categories: List<String> = emptyList(),
        val selectedTab: Tabs = Tabs.Videogames
    ) : HomeScreenUiState
    object Error : HomeScreenUiState
    object Loading : HomeScreenUiState
}
