package com.shadowxdgamer.movieapp.Service

import com.shadowxdgamer.movieapp.model.VidsrcResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object VidsrcApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            // 3. We are explicitly telling Ktor how to handle the mislabeled text/html content
            register(
                ContentType.Text.Html,
                KotlinxSerializationConverter(Json {
                    ignoreUnknownKeys = true
                })
            )
            // It's also good practice to still have the default json handler
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun fetchMovies(page: Int = 1): VidsrcResponse {
        val url = "https://vidsrc.xyz/movies/latest/page-$page.json"
        return client.get(url).body()
    }
}