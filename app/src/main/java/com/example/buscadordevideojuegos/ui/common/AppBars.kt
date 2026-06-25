package com.example.buscadordevideojuegos.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.buscadordevideojuegos.R

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchWord by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchWord,
            onValueChange = {
                searchWord = it
                onSearch(it)
            },
            placeholder = {
                Text(text = stringResource(R.string.search_game_label))
            },
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                .fillMaxWidth()
        )
    }
}

@Composable
fun AppBarGame(
    navigateBack: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .statusBarsPadding()
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
            overflow = TextOverflow.Ellipsis
        )
    }
}