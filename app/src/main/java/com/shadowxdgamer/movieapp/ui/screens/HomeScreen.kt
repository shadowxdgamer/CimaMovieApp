package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shadowxdgamer.movieapp.model.VidsrcMovie
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel : MovieViewModel = viewModel(), onMovieClick : (VidsrcMovie) -> Unit) {
    val uiState = viewModel.uiState
    // Load movies only once
    LaunchedEffect(Unit) {
        if (uiState.movies.isEmpty()) {
            viewModel.loadMovies() // Defaults to page 1, which is correct for a fresh start
        }
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
            onPageClick = { page -> viewModel.loadMovies(page) },
            onMovieClick = onMovieClick
        )
    }
}

@Composable
fun MovieListScreen(modifier: Modifier = Modifier,
                    movies: List<VidsrcMovie>,
                    isLoading: Boolean = false,
                    currentPage: Int,
                    totalPages: Int,
                    onPageClick: (Int) -> Unit,
                    onMovieClick : (VidsrcMovie) -> Unit

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
                        MovieCard(movie,onClick = { onMovieClick(movie) })
                    }
                }
                PaginationControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPageChange = onPageClick
                )
            }
        }
    }
}

@Composable
fun MovieCard(movie: VidsrcMovie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = movie.title)
            Text(text = "Quality: ${movie.quality}")
        }
    }
}
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, contentDescription = "Previous")
        }

        TextButton(onClick = { showDialog = true }) {
            Text("Page $currentPage of $totalPages")
            Icon(Icons.Default.ArrowDropDown,contentDescription = "Arrow Drop Down")
        }

        Button(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, contentDescription = "Next")
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            title = { Text("Jump to Page") },
            text = {
                Column {
                    var inputPage by remember { mutableStateOf(currentPage.toString()) }

                    OutlinedTextField(
                        value = inputPage,
                        onValueChange = { inputPage = it.filter { c -> c.isDigit() } },
                        label = { Text("Page number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val target = inputPage.toIntOrNull()
                            if (target != null && target in 1..totalPages) {
                                onPageChange(target)
                                showDialog = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Go")
                    }
                }
            }
        )
    }
}