package com.shadowxdgamer.movieapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopBar(
    showSearch: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onGenresClick: () -> Unit
) {
    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = showSearch,
                enter = androidx.compose.animation.fadeIn(tween(150)),
                exit = androidx.compose.animation.fadeOut(tween(150))
            ) {
                SearchField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = "Search movies…"
                )
            }

            if (!showSearch) {
                Text("Movies")
            }
        },
        actions = {
            if (!showSearch) {
                IconButton(onClick = { /* open a full search screen later */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
            TextButton(onClick = onGenresClick) {
                Text("Genres")
            }
        }
    )
}
