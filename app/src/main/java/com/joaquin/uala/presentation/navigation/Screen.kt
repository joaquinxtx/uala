package com.joaquin.uala.presentation.navigation

sealed class Screen(val route: String) {
    object CityList : Screen("city_list")
    object CityDetail : Screen("city_detail")
}
