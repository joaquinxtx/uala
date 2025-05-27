package com.joaquin.uala.data.repositoriesImpl

import com.joaquin.uala.data.local.CitiesLocalDataSource
import com.joaquin.uala.data.mapper.toEntity
import com.joaquin.uala.data.mapper.toModel
import com.joaquin.uala.data.remoto.CitiesRemoteDataSource
import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.repository.CityRepository
import com.joaquin.uala.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CityRepositoryImpl(
    private val remoteDataSource: CitiesRemoteDataSource,
    private val localDataSource: CitiesLocalDataSource
) : CityRepository {

    private var cachedCities: List<CityModel> = emptyList()

    override fun getAllCities(): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Loading)

        try {
            if (cachedCities.isEmpty()) {
                val remote = remoteDataSource.downloadCities()
                val favoriteIds = localDataSource.getFavoriteCities().map { it.id }.toSet()

                cachedCities = remote.map {
                    it.toModel().copy(isFavorite = it._id in favoriteIds)
                }
            }

            emit(Resource.Success(cachedCities))
        } catch (e: Exception) {
            emit(Resource.Error("Error al cargar las ciudades",e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteCities(): Flow<List<CityModel>> = flow {
        val favorites = localDataSource.getFavoriteCities().map { it.toModel() }
        emit(favorites)
    }.flowOn(Dispatchers.IO)

    override suspend fun toggleFavorite(city: CityModel) {
        if (city.isFavorite) {
            localDataSource.insertFavorite(city.toEntity())
        } else {
            localDataSource.deleteFavorite(city.id)
        }

        cachedCities = cachedCities.map {
            if (it.id == city.id) it.copy(isFavorite = city.isFavorite) else it
        }
    }

    override fun searchCities(query: String, onlyFavorites: Boolean): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Loading)

        try {
            val cities = getAllCities().firstOrNull()
            if (cities is Resource.Success) {
                val filtered = cities.data
                    .asSequence()
                    .filter { it.name.lowercase().startsWith(query.lowercase()) }
                    .filter { if (onlyFavorites) it.isFavorite else true }
                    .sortedWith(compareBy({ it.name.lowercase() }, { it.country.lowercase() }))
                    .toList()

                emit(Resource.Success(filtered))
            } else if (cities is Resource.Error) {
                emit(Resource.Error(cities.message, cities.cause))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error al buscar ciudades", e))
        }
    }.flowOn(Dispatchers.Default)
}

