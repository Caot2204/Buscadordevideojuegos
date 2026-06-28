package com.example.buscadordevideojuegos.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
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
    }
}