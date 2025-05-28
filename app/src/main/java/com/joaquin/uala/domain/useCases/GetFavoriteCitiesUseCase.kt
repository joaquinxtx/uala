package com.joaquin.uala.domain.useCases

import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteCitiesUseCase(
    private val repository: CityRepository
) {
    operator fun invoke(): Flow<List<CityModel>> {
        return repository.getFavoriteCities()
    }
}
