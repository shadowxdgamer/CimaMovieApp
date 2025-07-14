package com.shadowxdgamer.movieapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowxdgamer.movieapp.Service.VidsrcApi
import com.shadowxdgamer.movieapp.model.VidsrcMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieViewModel(val state: SavedStateHandle
) : ViewModel() {
    private val TAG = "MovieViewModel"
    // ✅ State holder class
    data class UiState(
        val isLoading: Boolean = true,
        val movies: List<VidsrcMovie> = emptyList(),
        val currentPage: Int = 1,
        val totalPages: Int = 1
    )

    // ✅ State variable (Compose will observe this) && added SavedStateHandle
    var uiState by mutableStateOf(
        UiState(
            currentPage = state["currentPage"] ?: 1,
        )
    )
        private set

    // ✅ Simulate loading movies
    fun loadMovies(page: Int = 1) {
        uiState = uiState.copy(isLoading = true)


        viewModelScope.launch {
            val movies = withContext(Dispatchers.IO) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        VidsrcApi.fetchMovies(page)
                    }
                    Log.d(TAG, "Successfully fetched ${response.result.size} movies for page $page")
                    uiState = UiState(
                        isLoading = false,
                        movies = response.result,
                        currentPage = page,
                        totalPages = response.pages
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load movies", e)
                    e.printStackTrace()
                    uiState = uiState.copy(isLoading = false)
                }
            }

        // Save to SavedStateHandle
        state["currentPage"] = page
        }
    }
}