package com.joaquin.uala.data.local

import com.joaquin.uala.data.local.dao.LocationFavDao
import com.joaquin.uala.data.local.entity.CityEntity

class CitiesLocalDataSourceImpl (
    private val cityDao: LocationFavDao
) : CitiesLocalDataSource {

    override suspend fun getFavoriteCities(): List<CityEntity> {
        return cityDao.getAllFavorites()
    }

    override suspend fun insertFavorite(city: CityEntity) {
        cityDao.insert(city)
    }

    override suspend fun deleteFavorite(id: Long) {
        cityDao.deleteById(id)
    }
}