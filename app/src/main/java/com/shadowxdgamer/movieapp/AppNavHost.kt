package com.shadowxdgamer.movieapp

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowxdgamer.movieapp.ui.screens.CategoryScreen
import com.shadowxdgamer.movieapp.ui.screens.HomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onMovieClick = { movie ->
                    // Construct the URL dynamically using the movie's TMDB ID.
                    val urlToLoad = "https://vidsrc.cc/v3/embed/movie/${movie.id}"

                    val intent = Intent(context, PlayerActivity::class.java).apply {
                        putExtra("EMBED_URL", urlToLoad)
                    }
                    context.startActivity(intent)
                },
                // NEW: Handle clicks on "See All"
                onSeeAllClick = { categoryTitle ->
                    navController.navigate("category/$categoryTitle")
                },
            )
        }
        composable(
            "category/{title}",
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Popular"
            CategoryScreen(
                categoryTitle = title,
                // viewModel() will provide the shared ViewModel instance
                viewModel = viewModel(),
                onMovieClick = { movie -> // Construct the URL dynamically using the movie's TMDB ID.
                    val urlToLoad = "https://vidsrc.cc/v3/embed/movie/${movie.id}"

                    val intent = Intent(context, PlayerActivity::class.java).apply {
                        putExtra("EMBED_URL", urlToLoad)
                    }
                    context.startActivity(intent)
                               },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}