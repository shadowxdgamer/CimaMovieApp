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

@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("vote_average") val voteAverage: Float,
    val genres: List<Genre>,
    val runtime: Int?,
    @SerialName("production_countries") val productionCountries: List<ProductionCountry> = emptyList(),
    @SerialName("spoken_languages") val spokenLanguages: List<SpokenLanguage> = emptyList()
)

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1") val isoCode: String,
    val name: String
)

@Serializable
data class SpokenLanguage(
    @SerialName("english_name") val englishName: String,
    val name: String
)
