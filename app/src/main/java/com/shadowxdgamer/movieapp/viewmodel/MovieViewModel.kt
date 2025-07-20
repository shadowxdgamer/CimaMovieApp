package com.shadowxdgamer.movieapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.service.TMDBApi
import kotlinx.coroutines.launch

data class CategoryState(
    val movies: List<TmdbMovie> = emptyList(),
    val page: Int = 1,
    val isLoading: Boolean = false,
    val hasMore: Boolean = true
)

data class UiState(
    val trending: CategoryState = CategoryState(),
    val nowPlaying: CategoryState = CategoryState(),
    val action: CategoryState = CategoryState(),
    val popular: CategoryState = CategoryState()
)

class MovieViewModel : ViewModel() {
    var uiState by mutableStateOf(UiState())
        private set

    fun loadPopularMovies() {
        val current = uiState.popular
        if (current.isLoading || !current.hasMore) return

        uiState = uiState.copy(popular = current.copy(isLoading = true))

        viewModelScope.launch {
            try {
                val response = TMDBApi.fetchPopularMovies(current.page)
                val newList = current.movies + response.results
                val hasMore = current.page < response.totalPages

                uiState = uiState.copy(
                    popular = current.copy(
                        movies = newList,
                        page = current.page + 1,
                        isLoading = false,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(popular = current.copy(isLoading = false))
            }
        }
    }

    fun loadTrending() {
        val current = uiState.trending
        if (current.isLoading || !current.hasMore) return

        uiState = uiState.copy(trending = current.copy(isLoading = true))

        viewModelScope.launch {
            try {
                val response = TMDBApi.fetchTrendingMovies(current.page)
                val newList = current.movies + response.results
                val hasMore = current.page < response.totalPages

                uiState = uiState.copy(
                    trending = current.copy(
                        movies = newList,
                        page = current.page + 1,
                        isLoading = false,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(trending = current.copy(isLoading = false))
            }
        }
    }

    fun loadNowPlaying() {
        val current = uiState.nowPlaying
        if (current.isLoading || !current.hasMore) return

        uiState = uiState.copy(nowPlaying = current.copy(isLoading = true))

        viewModelScope.launch {
            try {
                val response = TMDBApi.fetchNowPlaying(current.page)
                val newList = current.movies + response.results
                val hasMore = current.page < response.totalPages

                uiState = uiState.copy(
                    nowPlaying = current.copy(
                        movies = newList,
                        page = current.page + 1,
                        isLoading = false,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(nowPlaying = current.copy(isLoading = false))
            }
        }
    }

    fun loadActionMovies() {
        val current = uiState.action
        if (current.isLoading || !current.hasMore) return

        uiState = uiState.copy(action = current.copy(isLoading = true))

        viewModelScope.launch {
            try {
                val response = TMDBApi.fetchMoviesByGenre(genreId = 28, page = current.page)
                val newList = current.movies + response.results
                val hasMore = current.page < response.totalPages

                uiState = uiState.copy(
                    action = current.copy(
                        movies = newList,
                        page = current.page + 1,
                        isLoading = false,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(action = current.copy(isLoading = false))
            }
        }
    }
}
