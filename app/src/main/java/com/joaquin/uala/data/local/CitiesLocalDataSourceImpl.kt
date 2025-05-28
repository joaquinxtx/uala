package com.joaquin.uala.data.local

import com.joaquin.uala.data.local.dao.LocationFavDao
import com.joaquin.uala.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

class CitiesLocalDataSourceImpl (
    private val cityDao: LocationFavDao
) : CitiesLocalDataSource {

    override  fun getFavoriteCities(): Flow<List<CityEntity>> {
        return cityDao.getFavoriteCities()
    }

    override suspend fun insertFavorite(city: CityEntity) {
        cityDao.insert(city)
    }

    override suspend fun deleteFavorite(id: Long) {
        cityDao.deleteById(id)
    }
}