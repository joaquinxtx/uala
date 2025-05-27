package com.joaquin.uala.data.remoto

import com.joaquin.uala.data.remoto.response.CityResponse
import com.joaquin.uala.data.remoto.service.ApiService

class CitiesRemoteDataSourceImpl (private val apiService: ApiService): CitiesRemoteDataSource {
    override suspend fun downloadCities(): List<CityResponse> = apiService.downloadCities()
}