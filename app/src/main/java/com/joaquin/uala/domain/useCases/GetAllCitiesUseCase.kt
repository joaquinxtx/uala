package com.joaquin.uala.domain.useCases

import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.repository.CityRepository
import com.joaquin.uala.utils.Resource
import kotlinx.coroutines.flow.Flow

class GetAllCitiesUseCase(
    private val repository: CityRepository
) {
    operator fun invoke(): Flow<Resource<List<CityModel>>> {
        return repository.getAllCities()
    }
}
