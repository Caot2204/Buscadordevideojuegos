package com.example.buscadordevideojuegos.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.buscadordevideojuegos.ui.game.GameDetailsScreen
import com.example.buscadordevideojuegos.ui.game.GameDetailsScreenDestination
import com.example.buscadordevideojuegos.ui.home.HomeScreen
import com.example.buscadordevideojuegos.ui.home.HomeScreenDestination

@Composable
fun AppNavHost(
    navHostController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navHostController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = HomeScreenDestination.route) {
            HomeScreen(
                navigateToGameDetails = {
                    navHostController.navigate("${GameDetailsScreenDestination.route}/$it")
                }
            )
        }
        composable(
            route = GameDetailsScreenDestination.routeWithArgs,
            arguments = listOf(
                navArgument(GameDetailsScreenDestination.gameIdArg) { type = NavType.IntType }
            )
        ) {
            GameDetailsScreen(
                navigateBack = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}