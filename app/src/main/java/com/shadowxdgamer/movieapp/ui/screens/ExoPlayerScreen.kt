package com.shadowxdgamer.movieapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExoPlayerScreen(videoUrl: String) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Now Playing") })
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            ExoPlayerView(
                videoUrl = videoUrl,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
