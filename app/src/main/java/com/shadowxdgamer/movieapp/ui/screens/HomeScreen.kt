package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.ui.components.HomeTopBar
import com.shadowxdgamer.movieapp.ui.components.MoviePosterCard
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel : MovieViewModel = viewModel(),
    onMovieClick: (TmdbMovie) -> Unit,
    onGenresClick: () -> Unit
) {
    val trendingState = viewModel.uiState.trending
    val gridState = rememberLazyGridState()

    // Search text (UI only for now)
    var searchQuery by remember { mutableStateOf("") }

    // Collapsible search visibility
    var showSearch by remember { mutableStateOf(true) }

    // Detect scroll to hide/show search
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collectLatest { idx ->
                showSearch = idx == 0
            }
    }

    // Load first page on first composition
    LaunchedEffect(Unit) {
        if (trendingState.movies.isEmpty()) {
            viewModel.loadTrending() // page 1
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                showSearch = showSearch,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onGenresClick = onGenresClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Movie grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                state = gridState,
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(trendingState.movies, key = { _, m -> m.id }) { index, movie ->
                    MoviePosterCard(movie = movie, onClick = { onMovieClick(movie) })

                    // Trigger load-more when ~10 from end
                    val loadTrigger = trendingState.movies.size - 10
                    if (index == loadTrigger) {
                        viewModel.loadTrending()
                    }
                }

                // Footer loading indicator
                if (trendingState.isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Initial full-screen loading (first load)
            if (trendingState.movies.isEmpty() && trendingState.isLoading) {
                CircularProgressIndicator(Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        }
    }
}
