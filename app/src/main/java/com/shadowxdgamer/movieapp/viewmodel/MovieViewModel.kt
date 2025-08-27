package com.shadowxdgamer.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shadowxdgamer.movieapp.model.Genre
import com.shadowxdgamer.movieapp.model.MovieDetails
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.model.TmdbMovieResponse
import com.shadowxdgamer.movieapp.repository.GenreRepository
import com.shadowxdgamer.movieapp.service.MoviePagingSource
import com.shadowxdgamer.movieapp.service.TMDBApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MovieViewModel : ViewModel() {

    // --- Paging for Home Screen and Category Screen ---
    val popularMovies: Flow<PagingData<TmdbMovie>> = createMoviePager { page -> TMDBApi.fetchPopularMovies(page) }
    val trendingMovies: Flow<PagingData<TmdbMovie>> = createMoviePager { page -> TMDBApi.fetchTrendingMovies(page) }
    val nowPlayingMovies: Flow<PagingData<TmdbMovie>> = createMoviePager { page -> TMDBApi.fetchNowPlaying(page) }

    private fun createMoviePager(apiCall: suspend (page: Int) -> TmdbMovieResponse): Flow<PagingData<TmdbMovie>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MoviePagingSource(apiCall) }
        ).flow.cachedIn(viewModelScope)
    }

    // --- Genres ---
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    init {
        _genres.value = GenreRepository.genres
    }

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<TmdbMovie>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                MoviePagingSource { page -> TMDBApi.fetchMoviesByGenre(genreId, page) }
            }
        ).flow.cachedIn(viewModelScope)
    }

    // --- Search ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: Flow<PagingData<TmdbMovie>> = _searchQuery
        .debounce(500L) // Wait for 500ms of no new text
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                Pager(
                    config = PagingConfig(pageSize = 20),
                    pagingSourceFactory = { MoviePagingSource { page -> TMDBApi.searchMovies(query, page) } }
                ).flow
            } else {
                flowOf(PagingData.empty()) // Return empty results if query is blank
            }
        }.cachedIn(viewModelScope)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // --- Movie Details ---
    private val _movieDetails = MutableStateFlow<MovieDetails?>(null)
    val movieDetails = _movieDetails.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            // Set to null first to show a loading state
            _movieDetails.value = null
            try {
                _movieDetails.value = TMDBApi.fetchMovieDetails(movieId)
            } catch (e: Exception) {
                // Handle error, maybe with a separate error state flow
                e.printStackTrace()
            }
        }
    }


}