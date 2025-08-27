package com.shadowxdgamer.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shadowxdgamer.movieapp.model.Author
import com.shadowxdgamer.movieapp.model.ChatMessage
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


private enum class ConversationState {
    AWAITING_GENRE, AWAITING_CONFIRMATION
}

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

    // --- Chatbot ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private var conversationState = ConversationState.AWAITING_GENRE
    private var selectedGenre: Genre? = null

    fun startChat() {
        // Clear previous conversation and start a new one
        conversationState = ConversationState.AWAITING_GENRE
        selectedGenre = null
        _chatMessages.value = listOf(
            ChatMessage("Hi! I can help you find a movie. What genre are you in the mood for?", Author.BOT)
        )
    }

    fun onUserMessageSent(text: String) {
        // Add the user's message to the list
        val userMessage = ChatMessage(text, Author.USER)
        _chatMessages.value = _chatMessages.value + userMessage

        // Bot processes the message and responds
        generateBotResponse(text)
    }

    private fun generateBotResponse(userInput: String) {
        viewModelScope.launch {
            when (conversationState) {
                ConversationState.AWAITING_GENRE -> {
                    // Try to find a genre that matches the user's input
                    val matchedGenre = genres.value.firstOrNull { it.name.equals(userInput, ignoreCase = true) }
                    if (matchedGenre != null) {
                        selectedGenre = matchedGenre
                        addBotMessage("Awesome, ${matchedGenre.name} is a great choice! Should I find a movie for you?")
                        conversationState = ConversationState.AWAITING_CONFIRMATION
                    } else {
                        addBotMessage("Sorry, I don't recognize that genre. Please pick one like Action, Comedy, or Horror.")
                    }
                }
                ConversationState.AWAITING_CONFIRMATION -> {
                    if (userInput.contains("yes", ignoreCase = true) || userInput.contains("ok", ignoreCase = true)) {
                        addBotMessage("Great! Searching for a ${selectedGenre?.name} movie...")
                        try {
                            // Fetch a random movie from the first page of the selected genre
                            val response = TMDBApi.fetchMoviesByGenre(selectedGenre!!.id, 1)
                            val movie = response.results.randomOrNull()
                            if (movie != null) {
                                val recommendation = ChatMessage(
                                    text = "I think you'll like this one:",
                                    author = Author.BOT,
                                    movieRecommendation = movie
                                )
                                _chatMessages.value = _chatMessages.value + recommendation
                            } else {
                                addBotMessage("Sorry, I couldn't find a movie for that genre right now.")
                            }
                        } catch (e: Exception) {
                            addBotMessage("Oops, something went wrong while searching.")
                        }
                    } else {
                        addBotMessage("No problem! Let me know if you change your mind.")
                    }
                    // Reset the conversation
                    conversationState = ConversationState.AWAITING_GENRE
                }
            }
        }
    }

    private fun addBotMessage(text: String) {
        val botMessage = ChatMessage(text, Author.BOT)
        _chatMessages.value = _chatMessages.value + botMessage
    }


}