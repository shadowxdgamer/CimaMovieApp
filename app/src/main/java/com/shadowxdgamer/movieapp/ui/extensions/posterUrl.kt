package com.shadowxdgamer.movieapp.ui.extensions

import com.shadowxdgamer.movieapp.model.TmdbMovie

// Build full TMDb poster URL (w500 is a good balance)
fun TmdbMovie.posterUrl(size: String = "w500"): String? =
    posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }