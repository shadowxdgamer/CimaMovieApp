package com.shadowxdgamer.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.content
import com.shadowxdgamer.movieapp.model.Author
import com.shadowxdgamer.movieapp.model.ChatMessage
import com.shadowxdgamer.movieapp.model.Genre
import com.shadowxdgamer.movieapp.model.MovieDetails
import com.shadowxdgamer.movieapp.model.TmdbMovie
import com.shadowxdgamer.movieapp.model.TmdbMovieResponse
import com.shadowxdgamer.movieapp.repository.GenreRepository
import com.shadowxdgamer.movieapp.service.GenerativeAIService
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
import java.util.regex.Pattern

// to delete if i want to revert to old dumb AI
//private enum class ConversationState {
//    AWAITING_GENRE, AWAITING_CONFIRMATION
//}

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

    // to delete if i want to revert to old dumb AI
//    private var conversationState = ConversationState.AWAITING_GENRE
//    private var selectedGenre: Genre? = null
    // A variable to hold the active Gemini chat session
    private var geminiChat: Chat? = null

    fun startChat() {
        // The system prompt that tells the AI its personality and rules
        val systemPrompt = """
            You are MovieSphere Bot, a friendly and helpful chatbot that recommends one movie based on a user's request.
            Your task is to:
            1. Understand the user's request, remembering the context of the conversation (e.g., if they ask for "another one", you know what they mean).
            2. Find ONE suitable movie.
            3. Respond ONLY with the movie's official title and its TMDb ID in this exact format: TITLE (ID: 12345). Example: The Matrix (ID: 603).

            Do not add any other words, explanations, or pleasantries. If you cannot find a movie or the request is unclear, respond with: "Error (ID: 0)".
        """.trimIndent()

        // Initialize the chat session with the system prompt
        geminiChat = GenerativeAIService.generativeModel.startChat(
            history = listOf(
                content(role = "user") { text(systemPrompt) },
                content(role = "model") { text("Understood. I am ready to recommend movies.") }
            )
        )

        // Start the UI with a fresh greeting
        _chatMessages.value = listOf(
            ChatMessage("Hi! I'm MovieSphere Bot. Ask me for a movie recommendation!", Author.BOT)
        )
    }

    fun onUserMessageSent(text: String) {
        val userMessage = ChatMessage(text, Author.USER)
        _chatMessages.value = _chatMessages.value + userMessage
        sendMessageToGemini(text)
    }

    private fun sendMessageToGemini(userInput: String) {
        viewModelScope.launch {
            addBotMessage("Thinking...")

            try {
                // Send the user's message to the ongoing chat session
                val response = geminiChat?.sendMessage(userInput)
                val geminiResponse = response?.text?.trim() ?: ""

                // Remove the "Thinking..." message
                _chatMessages.value = _chatMessages.value.dropLast(1)

                // The rest of the parsing logic is the same!
                val pattern = Pattern.compile("(.+?)\\s*\\([Ii][Dd]\\s*:\\s*(\\d+)\\)")
                val matcher = pattern.matcher(geminiResponse)

                if (matcher.matches()) {
                    val movieId = matcher.group(2)?.toIntOrNull()
                    if (movieId != null && movieId != 0) {
                        val movie = TMDBApi.fetchMovieDetails(movieId)
                        val recommendation = ChatMessage(
                            text = "Based on your request, I think you'll like this:",
                            author = Author.BOT,
                            movieRecommendation = movie.toTmdbMovie()
                        )
                        _chatMessages.value = _chatMessages.value + recommendation
                    } else {
                        addBotMessage("I'm sorry, I couldn't find a movie based on that. Could you try being more specific?")
                    }
                } else {
                    addBotMessage(geminiResponse.ifBlank { "Sorry, I had trouble with that request." })
                }
            } catch (e: Exception) {
                // First, remove the "Thinking..." message
                _chatMessages.value = _chatMessages.value.dropLast(1)

                // Now, add your new error handling logic
                if (e.message?.contains("rate limit", ignoreCase = true) == true) {
                    addBotMessage("The bot is very popular right now! Please try again in a minute. 😅")
                } else {
                    // A fallback for any other type of error
                    addBotMessage("Oops, something went wrong. Please try again.")
                    e.printStackTrace() // Log the error for debugging
                }
            }
        }
    }

    // Helper to convert MovieDetails to TmdbMovie for the poster card
    private fun MovieDetails.toTmdbMovie(): TmdbMovie {
        return TmdbMovie(
            id = this.id,
            title = this.title,
            posterPath = this.posterPath,
            releaseDate = this.releaseDate,
            voteAverage = this.voteAverage
        )
    }

    private fun addBotMessage(text: String) {
        val botMessage = ChatMessage(text, Author.BOT)
        _chatMessages.value = _chatMessages.value + botMessage
    }


}