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
                    //val encodedUrl = URLEncoder.encode(movie.embed_url, "UTF-8")
                    val encodedUrl = URLEncoder.encode("https://vidsrc.cc/v3/embed/movie/tt5433140?autoPlay=false" + "\n", "UTF-8")
                    navController.navigate("player/$encodedUrl")
                }
            )
        }
        composable(
            "player/{embedUrl}",
            arguments = listOf(navArgument("embedUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val embedUrl = URLDecoder.decode(backStackEntry.arguments?.getString("embedUrl")!!, "UTF-8")
            PlayerScreen(embedUrl)
            //GeckoPlayerScreen(embedUrl)
        }


    }
}