package com.shadowxdgamer.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shadowxdgamer.movieapp.ui.theme.CimaMovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //splash screen
        installSplashScreen()
        setContent {
            CimaMovieAppTheme{
            MainScreenView()
            }
        }
    }
}