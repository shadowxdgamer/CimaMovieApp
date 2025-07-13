package com.shadowxdgamer.movieapp.data

import com.shadowxdgamer.movieapp.model.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object MovieScraper {
    private const val BASE_URL = "https://w12.my-cima.net/new-movies-mycimaa.php"

    fun fetchMovies(page: Int = 1): List<Movie> {
        val movies = mutableListOf<Movie>()

        val url = "$BASE_URL?page=$page"
        val doc: Document = Jsoup.connect(url).userAgent("Mozilla/5.0")
            .timeout(10_000)
            .get()

        // Select movie cards (use correct selector!)
        val elements: Elements = doc.select("li.col-xs-6.col-sm-4.col-md-3")
        for (element in elements) {
            val anchor = element.select("a")
                .firstOrNull { it.attr("href").contains("watch.php") } ?: continue
            val title = anchor.attr("title").trim()
            val videoUrl = anchor.absUrl("href")

            val img = element.selectFirst("img") ?: continue
            val imageUrl = img.absUrl("src")

            val durationElement = element.selectFirst(".pm-label-duration")
            val duration = durationElement?.text()?.trim() ?: "Unknown"

            movies.add(Movie(title, imageUrl, duration, videoUrl))
        }
        return movies
    }
}