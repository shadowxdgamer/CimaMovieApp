package com.shadowxdgamer.movieapp.service

import com.shadowxdgamer.movieapp.model.TmdbMovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object TMDBApi {
    private const val API_KEY = "68781d3be10663ac0eaba2aeb5e4fe74"
    private const val BASE_URL = "https://api.themoviedb.org/3"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun fetchPopularMovies(page: Int = 1): TmdbMovieResponse {
        return client.get("$BASE_URL/movie/popular") {
            parameter("api_key", API_KEY)
            parameter("language", "en-US")
            parameter("page", page)
        }.body()
    }

    suspend fun fetchNowPlaying(page: Int = 1): TmdbMovieResponse {
        return client.get("$BASE_URL/movie/now_playing") {
            parameter("api_key", API_KEY)
            parameter("language", "en-US")
            parameter("page", page)
        }.body()
    }

    suspend fun fetchTrendingMovies(page: Int = 1): TmdbMovieResponse {
        return client.get("$BASE_URL/trending/movie/week") {
            parameter("api_key", API_KEY)
            parameter("language", "en-US")
            parameter("page", page)
        }.body()
    }
    suspend fun fetchMoviesByGenre(genreId: Int, page: Int = 1): TmdbMovieResponse {
        return client.get("$BASE_URL/discover/movie") {
            parameter("api_key", API_KEY)
            parameter("with_genres", genreId)
            parameter("page", page)
            parameter("language", "en-US")
        }.body()
    }
    suspend fun searchMovies(query: String, page: Int): TmdbMovieResponse {
        return client.get("$BASE_URL/search/movie") {
            parameter("api_key", API_KEY)
            parameter("query", query)
            parameter("page", page)
        }.body()
    }
}
