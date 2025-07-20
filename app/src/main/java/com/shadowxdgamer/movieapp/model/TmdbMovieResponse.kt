package com.shadowxdgamer.movieapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieResponse(
    val page: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("results") val results: List<TmdbMovie>
)

@Serializable
data class TmdbMovie(
    val id: Int,
    @SerialName("title") val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("overview") val overview: String = "",
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Float = 0f
)