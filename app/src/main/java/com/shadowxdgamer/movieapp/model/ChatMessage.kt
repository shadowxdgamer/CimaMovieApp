package com.shadowxdgamer.movieapp.model

data class ChatMessage(
    val text: String,
    val author: Author,
    val movieRecommendation: TmdbMovie? = null // Optional: Attach a movie to the message
)

enum class Author {
    USER, BOT
}