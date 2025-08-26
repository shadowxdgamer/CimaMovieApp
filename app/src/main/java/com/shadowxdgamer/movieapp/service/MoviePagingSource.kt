package com.shadowxdgamer.movieapp.service

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.model.TmdbMovieResponse

// This PagingSource is reusable for any TMDB endpoint that returns a TmdbResponse
class MoviePagingSource(
    private val apiCall: suspend (page: Int) -> TmdbMovieResponse
) : PagingSource<Int, TmdbMovie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TmdbMovie> {
        return try {
            // Get the page number to load. Start with 1 if it's the first time.
            val page = params.key ?: 1
            val response = apiCall(page)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TmdbMovie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}