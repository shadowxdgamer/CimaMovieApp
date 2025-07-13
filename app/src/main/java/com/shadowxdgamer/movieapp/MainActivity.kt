package com.shadowxdgamer.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.shadowxdgamer.movieapp.ui.screens.HomeScreen

@Composable
fun App() {
    HomeScreen() // uses sample movies
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}