package com.joaquin.uala.domain.repository

import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getAllCities(): Flow<Resource<List<CityModel>>>
    fun getFavoriteCities(): Flow<List<CityModel>>
    suspend fun toggleFavorite(city: CityModel)
}
