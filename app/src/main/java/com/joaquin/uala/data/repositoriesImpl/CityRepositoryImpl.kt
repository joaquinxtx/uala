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


}


