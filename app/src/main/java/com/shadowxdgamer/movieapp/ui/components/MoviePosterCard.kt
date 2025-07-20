package com.shadowxdgamer.movieapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.ui.extensions.posterUrl

@Composable
fun MoviePosterCard(
    movie: TmdbMovie,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
            .padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = movie.posterUrl(),
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2
        )

        Spacer(Modifier.height(2.dp))

        // Show release year + rating (★)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            movie.releaseDate.takeIf { it.isNotBlank() }?.let { date ->
                val year = date.take(4)
                Text(text = year, style = MaterialTheme.typography.labelSmall)
            }

            Text(
                text = "★ %.1f".format(movie.voteAverage),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
