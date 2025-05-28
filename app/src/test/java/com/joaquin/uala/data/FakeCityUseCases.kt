package com.joaquin.uala.data

import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.repository.CityRepository
import com.joaquin.uala.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow


class FakeCityRepository : CityRepository {

    val allCities = listOf(
        CityModel(1, "Alpha", "AR", 0.0, 0.0, false),
        CityModel(2, "Zeta", "AR", 0.0, 0.0, false)
    )

    private val _favoriteCities = MutableStateFlow<List<CityModel>>(emptyList())

    override fun getAllCities(): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Success(allCities))
    }

    override fun getFavoriteCities(): Flow<List<CityModel>> = _favoriteCities

    override suspend fun toggleFavorite(city: CityModel) {
        val current = _favoriteCities.value.toMutableList()
        val exists = current.any { it.id == city.id }
        _favoriteCities.value = if (exists) {
            current.filterNot { it.id == city.id }
        } else {
            current + city.copy(isFavorite = true)
        }
    }

    override fun searchCities(
        query: String,
        onlyFavorites: Boolean
    ): Flow<Resource<List<CityModel>>> {
        throw UnsupportedOperationException("searchCities() no se usa en este proyecto")
    }
}

