package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shadowxdgamer.movieapp.model.MovieDetails
import com.shadowxdgamer.movieapp.ui.extensions.backdropUrl
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    onWatchNowClick: (Int) -> Unit
) {
    val movieDetails by viewModel.movieDetails.collectAsState()

    // Show a loading indicator while details are being fetched
    if (movieDetails == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        movieDetails?.let { movie ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Content is in a LazyColumn to be scrollable
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Backdrop Image
                    item {
                        AsyncImage(
                            model = movie.backdropUrl(),
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Content Section
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = movie.title.uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Button(
                                onClick = { onWatchNowClick(movie.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Watch Now")
                            }
                            Spacer(Modifier.height(16.dp))
                            ExpandableText(
                                text = movie.overview,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            DetailsCard(movie = movie)
                        }
                    }
                }

                // Back Button (layered on top)
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// Helper composable for the details card
@Composable
private fun DetailsCard(movie: MovieDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailItem("Released On:", movie.releaseDate)
            DetailItem("IMDB Rating:", "%.1f".format(movie.voteAverage), hasStar = true)
            DetailItem("Genre:", movie.genres.joinToString { it.name })
            movie.runtime?.let { DetailItem("Runtime:", "$it mins") }
            if (movie.productionCountries.isNotEmpty()) {
                DetailItem("Country:", movie.productionCountries.first().isoCode)
            }
            if (movie.spokenLanguages.isNotEmpty()) {
                DetailItem("Languages:", movie.spokenLanguages.first().englishName)
            }
        }
    }
}

// Helper for each row in the card
@Composable
private fun DetailItem(label: String, value: String, hasStar: Boolean = false) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.bodyMedium)
            if (hasStar) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

// Helper for the expandable "MORE" text
@Composable
private fun ExpandableText(text: String, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (isExpanded) "LESS" else "MORE",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .align(Alignment.End)
        )
    }
}