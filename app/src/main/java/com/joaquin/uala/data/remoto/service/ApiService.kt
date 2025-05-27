package com.joaquin.uala.data.remoto.service

import com.joaquin.uala.data.remoto.response.CityResponse
import retrofit2.http.GET

interface ApiService {
    @GET("/cities.json")
    suspend fun downloadCities(): List<CityResponse>
}