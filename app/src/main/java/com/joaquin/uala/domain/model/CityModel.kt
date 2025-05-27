package com.joaquin.uala.domain.model

data class CityModel(
    val id: Long,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isFavorite: Boolean = false
)

