package com.joaquin.uala.data.remoto

import com.joaquin.uala.data.remoto.response.CityResponse

interface CitiesRemoteDataSource {
    suspend fun downloadCities(): List<CityResponse>
}
