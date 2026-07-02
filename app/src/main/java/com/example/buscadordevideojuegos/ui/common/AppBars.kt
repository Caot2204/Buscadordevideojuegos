package com.example.buscadordevideojuegos.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.example.buscadordevideojuegos.R

@Composable
fun SearchBar(
    searchWord: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchWord,
            onValueChange = {
                onSearch(it)
            },
            placeholder = {
                Text(text = stringResource(R.string.search_game_label))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboard?.hide()
                }
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                .fillMaxWidth()
        )
    }
}

@Composable
fun AppBarGame(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    navigateBack: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = navigateBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        IconButton(
            onClick = onFavoriteClick
        ) {
            Icon(
                painter = if (isFavorite) {
                    painterResource(R.drawable.favorite_relleno)
                } else {
                    painterResource(R.drawable.favorite)
                },
                contentDescription = stringResource(R.string.mark_desmark_favorite),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}