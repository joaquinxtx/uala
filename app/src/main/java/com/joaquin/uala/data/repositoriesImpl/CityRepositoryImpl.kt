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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CityRepositoryImpl(
    private val remoteDataSource: CitiesRemoteDataSource,
    private val localDataSource: CitiesLocalDataSource
) : CityRepository {

    override fun getAllCities(): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Loading)

        try {
            val remote = remoteDataSource.downloadCities()
            val favoriteIds = localDataSource.getFavoriteCities()
                .first()
                .map { it.id }
                .toSet()
            val merged = remote.map { response ->
                response.toModel().copy(isFavorite = response.id in favoriteIds)
            }

            emit(Resource.Success(merged))
        } catch (e: Exception) {
            emit(Resource.Error("Error al cargar las ciudades", e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteCities(): Flow<List<CityModel>> =
        localDataSource.getFavoriteCities().map { list ->
            list.map { it.toModel() }
        }

    override suspend fun toggleFavorite(city: CityModel) {
        if (city.isFavorite) {
            localDataSource.insertFavorite(city.toEntity())
        } else {
            localDataSource.deleteFavorite(city.id)
        }
    }

    override fun searchCities(query: String, onlyFavorites: Boolean): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Loading)

        try {
            val remoteCities = remoteDataSource.downloadCities()
                .map { it.toModel() }

            val favoriteIds = localDataSource.getFavoriteCities()
                .first()
                .map { it.id }
                .toSet()

            val merged = remoteCities.map {
                it.copy(isFavorite = it.id in favoriteIds)
            }

            val filtered = merged.filter { it.name.contains(query, ignoreCase = true) }

            emit(
                Resource.Success(
                    if (onlyFavorites) filtered.filter { it.isFavorite } else filtered
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error("Error en la búsqueda", e))
        }
    }.flowOn(Dispatchers.IO)
}


