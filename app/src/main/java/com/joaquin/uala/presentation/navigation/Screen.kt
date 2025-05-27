package com.joaquin.uala.presentation.navigation

sealed class Screen(val route: String) {

    object CityList : Screen("city_list")

    object CityDetail : Screen("city_detail/{cityId}") {
        fun createRoute(cityId: Long): String = "city_detail/$cityId"
    }

    object Map : Screen("map/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String = "map/$lat/$lon"
    }
}
