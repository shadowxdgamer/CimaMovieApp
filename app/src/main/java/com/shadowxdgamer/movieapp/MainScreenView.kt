package com.shadowxdgamer.movieapp

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadowxdgamer.movieapp.ui.screens.CategoryScreen
import com.shadowxdgamer.movieapp.ui.screens.GenreResultScreen
import com.shadowxdgamer.movieapp.ui.screens.GenresScreen
import com.shadowxdgamer.movieapp.ui.screens.HomeScreen
import com.shadowxdgamer.movieapp.ui.screens.SearchScreen
import com.shadowxdgamer.movieapp.ui.screens.SettingsScreen
import com.shadowxdgamer.movieapp.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    val viewModel: MovieViewModel = viewModel()
    val context = LocalContext.current

    val onMovieClick: (com.shadowxdgamer.movieapp.model.TmdbMovie) -> Unit = { movie ->
        val urlToLoad = "https://vidsrc.cc/v3/embed/movie/${movie.id}"
        val intent = Intent(context, PlayerActivity::class.java).apply {
            putExtra("EMBED_URL", urlToLoad)
        }
        context.startActivity(intent)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(Screen.Home, Screen.Genres, Screen.Search, Screen.Settings)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = { categoryTitle -> navController.navigate("category/$categoryTitle") }
                )
            }
            composable(Screen.Genres.route) { GenresScreen(viewModel = viewModel, navController = navController) }
            composable(Screen.Search.route) { SearchScreen(viewModel = viewModel, onMovieClick = onMovieClick) }
            composable(Screen.Settings.route) { SettingsScreen() }

            // Route for the "See All" screen
            composable("category/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Popular"
                CategoryScreen(
                    categoryTitle = title,
                    viewModel = viewModel,
                    onMovieClick = onMovieClick,
                    onBackClick = { navController.popBackStack() }
                )
            }
            //Genre screen navigation
            composable(
                "genre/{genreId}/{genreName}",
                arguments = listOf(
                    navArgument("genreId") { type = NavType.IntType },
                    navArgument("genreName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
                val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
                GenreResultScreen(
                    genreId = genreId,
                    genreName = genreName,
                    viewModel = viewModel,
                    onMovieClick = onMovieClick,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}