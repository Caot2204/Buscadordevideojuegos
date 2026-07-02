package com.example.buscadordevideojuegos.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.buscadordevideojuegos.R
import com.example.buscadordevideojuegos.ui.common.AppBarGame
import com.example.buscadordevideojuegos.ui.common.ErrorIndicator
import com.example.buscadordevideojuegos.ui.common.IconWithText
import com.example.buscadordevideojuegos.ui.common.LoadingIndicator
import com.example.buscadordevideojuegos.ui.navigation.NavigationDestination

object GameDetailsScreenDestination : NavigationDestination {
    override val route: String = "game_details"
    override val titleRes: Int = R.string.game_details_title
    val gameIdArg = "gameId"
    val routeWithArgs = "$route/{$gameIdArg}"
}

@Composable
fun GameDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    gameDetailsScreenViewModel: GameDetailsScreenViewModel = hiltViewModel()
) {
    val uiState = gameDetailsScreenViewModel.uiState.collectAsState().value

    BackHandler {
        navigateBack()
    }

    Scaffold(
        topBar = {
            AppBarGame(
                isFavorite = if (uiState is GameDetailUiState.Success) {
                    uiState.isFavorite
                } else {
                    false
                },
                onFavoriteClick = {
                    if (uiState is GameDetailUiState.Success) {
                        if (uiState.isFavorite) {
                            gameDetailsScreenViewModel.desmarkFavorite()
                        } else {
                            gameDetailsScreenViewModel.markFavorite()
                        }
                    }
                },
                navigateBack = navigateBack,
                title = if (uiState is GameDetailUiState.Success) {
                    uiState.gameDetails.title
                } else {
                    stringResource(R.string.game_details_title)
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when (uiState) {
            is GameDetailUiState.Success ->
                GameDetailsUi(
                    gameDetails = uiState.gameDetails,
                    modifier = Modifier.padding(paddingValues)
                )
            is GameDetailUiState.Loading ->
                LoadingIndicator(
                    modifier = Modifier.padding(paddingValues)
                )
            is GameDetailUiState.Error ->
                ErrorIndicator(
                    onTryAgain = {
                        gameDetailsScreenViewModel.loadGameDetails()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
        }
    }
}

@Composable
fun GameDetailsUi(
    gameDetails: GameDetails,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                AsyncImage(
                    model = gameDetails.thumbnail,
                    contentDescription = gameDetails.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = gameDetails.genre,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = gameDetails.release_date
                        )
                    }
                    IconWithText(
                        icon = painterResource(R.drawable.videogame_asset),
                        text = gameDetails.platform
                    )
                    IconWithText(
                        icon = painterResource(R.drawable.web),
                        text = gameDetails.game_url
                    )
                    Text(
                        text = stringResource(R.string.publisher_label, gameDetails.publisher)
                    )
                    Text(
                        text = stringResource(R.string.developer_label, gameDetails.developer)
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.description_label),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
        )
        Text(
            text = gameDetails.description
        )
        gameDetails.minimum_system_requirements?.let { requirements ->
            Text(
                text = stringResource(R.string.system_requirements_label),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimensionResource(R.dimen.padding_medium))
            ) {
                with(requirements) {
                    Text(
                        text = stringResource(R.string.os_label, os ?: "")
                    )
                    Text(
                        text = stringResource(R.string.processor_label, processor ?: "")
                    )
                    Text(
                        text = stringResource(R.string.memory_label, memory ?: "")
                    )
                    Text(
                        text = stringResource(R.string.graphics_label, graphics ?: "")
                    )
                    Text(
                        text = stringResource(R.string.storage_label, storage ?: "")
                    )
                }
            }
        }
        if (gameDetails.screenshots.isNotEmpty()) {
            Text(
                text = stringResource(R.string.screenshots_label),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
            )
            CarrouselScreenshots(
                screenshots = gameDetails.screenshots
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrouselScreenshots(
    screenshots: List<Screenshot>,
    modifier: Modifier = Modifier
) {
    HorizontalUncontainedCarousel(
        state = rememberCarouselState { screenshots.count() },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = dimensionResource(R.dimen.padding_small)),
        itemWidth = dimensionResource(R.dimen.width_image_carrousel),
        itemSpacing = dimensionResource(R.dimen.padding_small),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_small))
    ) { i ->
        val item = screenshots[i]
        AsyncImage(
            modifier = Modifier
                .height(550.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.image)
                .placeholder(R.drawable.image)
                .crossfade(true)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}