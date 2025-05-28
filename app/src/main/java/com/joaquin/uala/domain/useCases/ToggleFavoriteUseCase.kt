package com.joaquin.uala.domain.useCases

import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.repository.CityRepository

class ToggleFavoriteUseCase(
    private val repository: CityRepository
) {
    suspend operator fun invoke(city: CityModel) {
        repository.toggleFavorite(city)
    }
}
