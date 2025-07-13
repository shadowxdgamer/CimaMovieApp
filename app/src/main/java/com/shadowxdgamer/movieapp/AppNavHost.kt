package com.shadowxdgamer.movieapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowxdgamer.movieapp.ui.screens.HomeScreen
import com.shadowxdgamer.movieapp.ui.screens.PlayerScreen
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onMovieClick = { movie ->
                    val encodedUrl = URLEncoder.encode(movie.videoUrl, "UTF-8")
                    navController.navigate("player/$encodedUrl")
                }
            )
        }

        composable(
            route = "player/{videoUrl}",
            arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val videoUrl = URLDecoder.decode(backStackEntry.arguments?.getString("videoUrl")!!, "UTF-8")
            PlayerScreen(videoUrl)
        }
    }
}