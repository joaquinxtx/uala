package com.joaquin.uala.data.local

import com.joaquin.uala.data.local.entity.CityEntity

interface CitiesLocalDataSource {
    suspend fun getFavoriteCities(): List<CityEntity>
    suspend fun insertFavorite(city: CityEntity)
    suspend fun deleteFavorite(id: Long)
}