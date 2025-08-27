package com.shadowxdgamer.movieapp.ui.extensions

import com.shadowxdgamer.movieapp.model.MovieDetails
import com.shadowxdgamer.movieapp.model.TmdbMovie

private const val BASE_POSTER_URL = "https://image.tmdb.org/t/p/w500"
private const val BASE_BACKDROP_URL = "https://image.tmdb.org/t/p/w780"
// Build full TMDb poster URL (w500 is a good balance)
fun TmdbMovie.posterUrl(size: String = "w500"): String? =
    posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

fun MovieDetails.backdropUrl(): String {
    return if (backdropPath != null) {
        "$BASE_BACKDROP_URL$backdropPath"
    } else {
        // Fallback to the poster if no backdrop is available
        if (posterPath != null) "$BASE_POSTER_URL$posterPath" else ""
    }
}