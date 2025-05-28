package com.joaquin.uala.data.remoto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CityResponse(
    val country: String,
    val name: String,
    @Json(name = "_id") val id: Long,
    val coord: Coord
)

@JsonClass(generateAdapter = true)
data class Coord(
    val lon: Double,
    val lat: Double
)

