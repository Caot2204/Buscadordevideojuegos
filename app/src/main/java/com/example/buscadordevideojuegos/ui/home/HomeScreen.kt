package com.example.buscadordevideojuegos.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.buscadordevideojuegos.R
import com.example.buscadordevideojuegos.ui.common.ErrorIndicator
import com.example.buscadordevideojuegos.ui.common.LoadingIndicator
import com.example.buscadordevideojuegos.ui.common.SearchBar
import com.example.buscadordevideojuegos.ui.navigation.NavigationDestination
import com.example.buscadordevideojuegos.ui.navigation.Tabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToGameDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState = homeScreenViewModel.uiState.collectAsState().value

    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SearchBar(
                searchWord = if (uiState is HomeScreenUiState.Success) {
                    uiState.searchWord
                } else {
                    ""
                },
                onSearch = {
                    homeScreenViewModel.searchVideoGame(it)
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = if (uiState is HomeScreenUiState.Success) {
                    uiState.selectedTab
                } else {
                    Tabs.Videogames
                },
                onGamesClicked = {
                    homeScreenViewModel.changeTab(Tabs.Videogames)
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                        lazyRowState.animateScrollToItem(0)
                    }
                },
                onFavoritesClicked = {
                    homeScreenViewModel.changeTab(Tabs.Favorites)
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                        lazyRowState.animateScrollToItem(0)
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when (uiState) {
            is HomeScreenUiState.Success ->
                GamesList(
                    uiState = uiState,
                    navigateToGameDetails = navigateToGameDetails,
                    onFilterByCategory = {
                        homeScreenViewModel.filterByCategory(it)
                    },
                    lazyListState = lazyListState,
                    lazyRowState = lazyRowState,
                    scope = scope,
                    modifier = Modifier.padding(paddingValues)
                )
            is HomeScreenUiState.Loading ->
                LoadingIndicator(
                    modifier = Modifier.padding(paddingValues)
                )
            is HomeScreenUiState.Error ->
                ErrorIndicator(
                    onTryAgain = {
                        homeScreenViewModel.loadVideogames()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Tabs,
    onGamesClicked: () -> Unit,
    onFavoritesClicked: () -> Unit
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = selectedTab == Tabs.Videogames,
            onClick = onGamesClicked,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.videogame_asset),
                    contentDescription = stringResource(R.string.videogames_label)
                )
            },
            label = {
                Text(text = stringResource(R.string.videogames_label))
            }
        )
        NavigationBarItem(
            selected = selectedTab == Tabs.Favorites,
            onClick = onFavoritesClicked,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.favorite),
                    contentDescription = stringResource(R.string.favorite_label)
                )
            },
            label = {
                Text(text = stringResource(R.string.favorite_label))
            }
        )
    }
}

@Composable
fun GamesList(
    uiState: HomeScreenUiState.Success,
    navigateToGameDetails: (Int) -> Unit,
    onFilterByCategory: (String) -> Unit,
    lazyListState: LazyListState,
    lazyRowState: LazyListState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyRow(
            state = lazyRowState,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            items(items = uiState.categories, key = { it }) { category ->
                CategoryItemUi(
                    category = category,
                    isSelected = category == uiState.categorySelected,
                    onClick = {
                        onFilterByCategory(it)
                        scope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                )
            }
        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (uiState.videoGames.isNotEmpty()) {
                items(items = uiState.videoGames, key = { it.id }) { videoGameItem ->
                    VideoGameItemUi(
                        videoGameItem = videoGameItem,
                        onClick = {
                            navigateToGameDetails(it)
                        }
                    )
                }
            } else {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.no_games_found),
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItemUi(
    category: String,
    isSelected: Boolean,
    onClick: (String) -> Unit,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        modifier = Modifier
            .clickable(onClick = {
                onClick(category)
            })
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun VideoGameItemUi(
    videoGameItem: VideoGameItem,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable(
                onClick = {
                    onClick(videoGameItem.id)
                }
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(videoGameItem.thumbnail)
                    .placeholder(R.drawable.image)
                    .error(R.drawable.broken_image)
                    .crossfade(true)
                    .build(),
                contentDescription = videoGameItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                        vertical = dimensionResource(R.dimen.padding_small)
                    )
            ) {
                Text(
                    text = videoGameItem.title,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = videoGameItem.genre
                    )
                    Text(
                        text = videoGameItem.release_date,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                Text(
                    text = videoGameItem.short_description
                )
            }
        }
    }
}