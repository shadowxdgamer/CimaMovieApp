package com.shadowxdgamer.movieapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowxdgamer.movieapp.ui.screens.ExoPlayerScreen
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
            PlayerScreen(videoUrl,
                onServerClick = { serverUrl ->
                    // This is now the correct place to navigate from
                    val encodedUrl = URLEncoder.encode(serverUrl, "UTF-8")
                    navController.navigate("video/$encodedUrl")
                })
        }
        composable(
            route = "video/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = URLDecoder.decode(backStackEntry.arguments?.getString("url")!!, "UTF-8")
            ExoPlayerScreen(url)
        }
    }
}