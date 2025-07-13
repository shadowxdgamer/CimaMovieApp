package com.shadowxdgamer.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.shadowxdgamer.movieapp.data.MovieScraper
import com.shadowxdgamer.movieapp.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieViewModel(private val state: SavedStateHandle
) : ViewModel() {

    // ✅ State holder class
    data class UiState(
        val isLoading: Boolean = true,
        val movies: List<Movie> = emptyList(),
        val currentPage: Int = 1,
        val totalPages: Int = 525 // or dynamically fetched later
    )

    // ✅ State variable (Compose will observe this) && added SavedStateHandle
    var uiState by mutableStateOf(
        UiState(
            currentPage = state["currentPage"] ?: 1,
            movies = state["movies"] ?: emptyList()
        )
    )
        private set

    // ✅ Simulate loading movies
    fun loadMovies(page: Int = 1) {
        uiState = uiState.copy(isLoading = true)


        viewModelScope.launch {
            val movies = withContext(Dispatchers.IO) {
                try {
                    MovieScraper.fetchMovies(page)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

        // Simulate a loading delay later using coroutines
        uiState = UiState(
            isLoading = false,
            movies = movies,
            currentPage = page,
            totalPages = 525
        )

        // Save to SavedStateHandle
        state["currentPage"] = page
        state["movies"] = movies
        }
    }
}