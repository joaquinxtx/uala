package com.joaquin.uala.data.local

import com.joaquin.uala.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

interface CitiesLocalDataSource {
   fun getFavoriteCities(): Flow<List<CityEntity>>
    suspend fun insertFavorite(city: CityEntity)
    suspend fun deleteFavorite(id: Long)
}