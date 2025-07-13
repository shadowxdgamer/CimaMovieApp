package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.shadowxdgamer.movieapp.model.Movie
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel

// 👇 Static list of example movies
//val sampleMovies = listOf(
//    Movie("Hunting Grounds 2025", "https://w12.my-cima.net/uploads/thumbs/5e36fc1a4-1.jpg", "1:29:25"),
//    Movie("Godzilla x Kong", "https://w12.my-cima.net/uploads/thumbs/example.jpg", "2:03:00"),
//)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel : MovieViewModel = viewModel()) {
    val uiState = viewModel.uiState
    // Load movies only once
    LaunchedEffect(Unit) {
        viewModel.loadMovies()
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cima Movie App") })
        }
    ) { padding ->
        MovieListScreen(
            modifier = Modifier.padding(padding), // 👈 apply it here
            movies = uiState.movies,
            isLoading = uiState.isLoading,
            currentPage = uiState.currentPage,
            totalPages = uiState.totalPages,
            onPageClick = { page -> viewModel.loadMovies(page) }
        )
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(movie.thumbnail),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Duration: ${movie.duration}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MovieListScreen(modifier: Modifier = Modifier,
                    movies: List<Movie>,
                    isLoading: Boolean = false,
                    currentPage: Int,
                    totalPages: Int,
                    onPageClick: (Int) -> Unit

) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        movies.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No movies found.")
            }
        }
        else -> {
            Column(modifier = modifier) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie)
                }
            }
            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageClick = onPageClick
            )
        }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        for (page in currentPage..minOf(currentPage + 4, totalPages)) {
            Button(
                onClick = { onPageClick(page) },
                colors = if (page == currentPage)
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                else
                    ButtonDefaults.buttonColors()
            ) {
                Text(page.toString())
            }
        }
    }
}