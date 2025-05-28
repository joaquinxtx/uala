package com.joaquin.uala.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joaquin.uala.presentation.cities.detail.CityDetailScreen
import com.joaquin.uala.presentation.cities.list.CitiesViewModel
import com.joaquin.uala.presentation.cities.map.CityMapScreen

@Composable
fun AppNavGraph(navController: NavHostController) {


    val sharedViewModel: CitiesViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.CityList.route
    ) {
        composable(Screen.CityList.route) {
            CityMapScreen(navController,sharedViewModel)
        }

        composable(
            route = Screen.CityDetail.route,
        ) { backStackEntry ->
            CityDetailScreen(navController,sharedViewModel)
        }


    }
}
