package com.shadowxdgamer.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shadowxdgamer.movieapp.ui.screens.PlayerScreen
import com.shadowxdgamer.movieapp.ui.theme.CimaMovieAppTheme // Make sure this matches your theme name

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the URL passed from the previous screen
        val embedUrl = intent.getStringExtra("EMBED_URL") ?: ""

        setContent {
            // Use your app's theme
            CimaMovieAppTheme {
                // The PlayerScreen takes the URL and a lambda to finish the activity on back click
                PlayerScreen(embedUrl = embedUrl, onBackClick = { finish() })
            }
        }
    }
}
