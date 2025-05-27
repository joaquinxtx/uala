package com.joaquin.uala.injection

import com.joaquin.uala.domain.repository.CityRepository
import com.joaquin.uala.domain.useCases.CityUseCases
import com.joaquin.uala.domain.useCases.GetAllCitiesUseCase
import com.joaquin.uala.domain.useCases.GetFavoriteCitiesUseCase
import com.joaquin.uala.domain.useCases.SearchCitiesUseCase
import com.joaquin.uala.domain.useCases.ToggleFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCityUseCases(repository: CityRepository): CityUseCases {
        return CityUseCases(
            getFavoriteCities = GetFavoriteCitiesUseCase(repository),
            getAllCities = GetAllCitiesUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            searchCities = SearchCitiesUseCase(repository)
        )
    }
}