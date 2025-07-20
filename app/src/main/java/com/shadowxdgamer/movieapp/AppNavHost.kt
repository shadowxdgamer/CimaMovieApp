package com.shadowxdgamer.movieapp

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowxdgamer.movieapp.ui.screens.HomeScreen
import com.shadowxdgamer.movieapp.ui.screens.PlayerScreen
import java.net.URLDecoder


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
//                onMovieClick = { movie ->
//                    //val encodedUrl = URLEncoder.encode(movie.embed_url, "UTF-8")
//                    val encodedUrl = URLEncoder.encode("https://vidsrc.cc/v3/embed/movie/tt5433140?autoPlay=false", "UTF-8")
//                    //val encodedUrl = URLEncoder.encode("https://player.autoembed.cc/embed/movie/tt3359350", "UTF-8")
//                    navController.navigate("player/$encodedUrl")
//                },
                onMovieClick = { movie ->
                    // The URL that will be loaded.
                    // IMPORTANT: Do NOT URL-encode this string.
                    val urlToLoad = "https://vidsrc.cc/v3/embed/movie/tt5433140?autoPlay=false"

                    // Create an Intent to launch the PlayerActivity
                    val intent = Intent(context, PlayerActivity::class.java).apply {
                        // Pass the plain, un-encoded URL string to the new activity.
                        putExtra("EMBED_URL", urlToLoad)
                    }
                    // Start the activity
                    context.startActivity(intent)
                },
                onGenresClick = {"todo"}
            )
        }
        composable(
            "player/{embedUrl}",
            arguments = listOf(navArgument("embedUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val embedUrl = URLDecoder.decode(backStackEntry.arguments?.getString("embedUrl")!!, "UTF-8")
            PlayerScreen(embedUrl, onBackClick = { navController.popBackStack() })
        }


    }
}