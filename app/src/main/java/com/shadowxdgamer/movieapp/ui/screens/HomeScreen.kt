package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.ui.components.CategoryRow
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel = viewModel(),
    onMovieClick: (TmdbMovie) -> Unit,
    onSeeAllClick: (categoryTitle: String) -> Unit
) {
    // Collect the flows from the ViewModel as LazyPagingItems.
    // This is the bridge between the Paging library and the Compose UI.
    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    val trendingMovies = viewModel.trendingMovies.collectAsLazyPagingItems()
    val nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movies") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Space between categories
        ) {
            item {
                CategoryRow(
                    title = "Trending",
                    movies = trendingMovies,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { onSeeAllClick("Trending") }
                )
            }

            item {
                CategoryRow(
                    title = "Popular",
                    movies = popularMovies,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { onSeeAllClick("Popular") }
                )
            }

            item {
                CategoryRow(
                    title = "Now Playing",
                    movies = nowPlayingMovies,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { onSeeAllClick("Now Playing") }
                )
            }

            // Add more categories here as you expand your ViewModel
        }
    }
}