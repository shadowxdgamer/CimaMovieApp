package com.shadowxdgamer.movieapp.service

import com.google.ai.client.generativeai.GenerativeModel

object GenerativeAIService {

    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyAITV1GHuhjBKqlTyBvmiFgChofKbjAS38"
    )

//    suspend fun getChatbotResponse(prompt: String): String {
//        return try {
//            val response = generativeModel.generateContent(prompt)
//            response.text ?: "Sorry, I couldn't generate a response."
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "Sorry, something went wrong."
//        }
//    }
}