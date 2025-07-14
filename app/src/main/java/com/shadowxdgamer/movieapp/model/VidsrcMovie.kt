package com.shadowxdgamer.movieapp.model

@kotlinx.serialization.Serializable
data class VidsrcResponse(
    val result: List<VidsrcMovie>,
    val pages: Int
)

@kotlinx.serialization.Serializable
data class VidsrcMovie(
    val imdb_id: String,
    val tmdb_id: String,
    val title: String,
    val embed_url: String,
    val embed_url_tmdb: String,
    val quality: String
)
