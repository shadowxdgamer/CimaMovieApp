package com.shadowxdgamer.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.service.MoviePagingSource
import com.shadowxdgamer.movieapp.service.TMDBApi
import kotlinx.coroutines.flow.Flow


class MovieViewModel : ViewModel() {
    // No longer need UiState or CategoryState for this!

    // The ViewModel now exposes Flows of PagingData, which are memory-efficient
    val popularMovies: Flow<PagingData<TmdbMovie>> = Pager(
        config = PagingConfig(pageSize = 20), // How many items to load per page
        pagingSourceFactory = { MoviePagingSource { page -> TMDBApi.fetchPopularMovies(page) } }
    ).flow.cachedIn(viewModelScope) // cache results in ViewModelScope

    val trendingMovies: Flow<PagingData<TmdbMovie>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { MoviePagingSource { page -> TMDBApi.fetchTrendingMovies(page) } }
    ).flow.cachedIn(viewModelScope)

    val nowPlayingMovies: Flow<PagingData<TmdbMovie>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { MoviePagingSource { page -> TMDBApi.fetchNowPlaying(page) } }
    ).flow.cachedIn(viewModelScope)

    // Add more for other categories as needed...
}