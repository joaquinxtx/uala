package com.joaquin.uala.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joaquin.uala.presentation.cities.detail.CityDetailScreen
import com.joaquin.uala.presentation.cities.list.CitiesScreen
import com.joaquin.uala.presentation.cities.map.CityMapScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.CityList.route
    ) {
        composable(Screen.CityList.route) {
            CitiesScreen(navController)
        }

        composable(
            route = Screen.CityDetail.route,
            arguments = listOf(navArgument("cityId") { type = NavType.LongType })
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
            CityDetailScreen(navController, cityId)
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: return@composable
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: return@composable
            CityMapScreen(navController, lat, lon)
        }
    }
}
