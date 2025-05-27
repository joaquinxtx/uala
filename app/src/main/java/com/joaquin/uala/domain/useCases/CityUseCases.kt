package com.joaquin.uala.domain.useCases

data class CityUseCases(
    val getAllCities: GetAllCitiesUseCase,
    val getFavoriteCities: GetFavoriteCitiesUseCase,
    val toggleFavorite: ToggleFavoriteUseCase,
    val searchCities: SearchCitiesUseCase
)
