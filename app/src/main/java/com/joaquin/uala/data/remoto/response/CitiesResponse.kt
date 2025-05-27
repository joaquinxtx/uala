package com.joaquin.uala.data.remoto.response

data class CityResponse(
    val country: String,
    val name: String,
    val _id: Long,
    val coord: CoordResponse
)

data class CoordResponse(
    val lon: Double,
    val lat: Double
)

