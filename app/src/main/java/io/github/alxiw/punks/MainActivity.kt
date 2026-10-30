package io.github.alxiw.punks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.alxiw.punks.presentation.beers.BeersScreen
import io.github.alxiw.punks.presentation.detail.BeerDetailScreen
import io.github.alxiw.punks.presentation.favorites.FavoritesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SharedTransitionLayout {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "beers"
                        ) {
                            composable("beers") {
                                BeersScreen(
                                    animatedVisibilityScope = this@composable,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    onBeerClick = { beerId -> navController.navigate("detail/$beerId") },
                                    onFavoritesClick = { navController.navigate("favorites") }
                                )
                            }
                            composable("favorites") {
                                FavoritesScreen(
                                    animatedVisibilityScope = this@composable,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    onBeerClick = { beerId -> navController.navigate("detail/$beerId") },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "detail/{beerId}",
                                arguments = listOf(navArgument("beerId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val beerId = backStackEntry.arguments?.getInt("beerId") ?: 0
                                BeerDetailScreen(
                                    animatedVisibilityScope = this@composable,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
