package com.joaquin.uala.viewmodel

import com.google.common.truth.Truth.assertThat
import com.joaquin.uala.data.FakeCityRepository
import com.joaquin.uala.domain.useCases.CityUseCases
import com.joaquin.uala.domain.useCases.GetAllCitiesUseCase
import com.joaquin.uala.domain.useCases.GetFavoriteCitiesUseCase
import com.joaquin.uala.domain.useCases.SearchCitiesUseCase
import com.joaquin.uala.domain.useCases.ToggleFavoriteUseCase
import com.joaquin.uala.presentation.cities.list.CitiesViewModel
import com.joaquin.uala.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CitiesViewModelIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeCityRepository
    private lateinit var useCases: CityUseCases
    private lateinit var viewModel: CitiesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeCityRepository()
        useCases = CityUseCases(
            getAllCities = GetAllCitiesUseCase(fakeRepository),
            getFavoriteCities = GetFavoriteCitiesUseCase(fakeRepository),
            toggleFavorite = ToggleFavoriteUseCase(fakeRepository),
            searchCities = SearchCitiesUseCase(fakeRepository)
        )
        viewModel = CitiesViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load cities should emit sorted city list`() = runTest {
        advanceUntilIdle()
        val result = viewModel.citiesState.value
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data.map { it.name }).containsExactly("Alpha", "Zeta")
    }

    @Test
    fun `toggleFavorite should update isFavorite`() = runTest {
        val city = fakeRepository.allCities.first()
        viewModel.toggleFavorite(city)
        advanceUntilIdle()

        val favorites = viewModel.favoriteCitiesState.value
        assertThat(favorites).contains(city.copy(isFavorite = true))
    }



    @Test
    fun `selectCity sets selectedCity`() = runTest {
        val city = fakeRepository.allCities.first()
        viewModel.selectCity(city)
        assertThat(viewModel.selectedCity.value).isEqualTo(city)
    }

    @Test
    fun `clearSelectedCity sets selectedCity to null`() = runTest {
        val city = fakeRepository.allCities.first()
        viewModel.selectCity(city)
        viewModel.clearSelectedCity()
        assertThat(viewModel.selectedCity.value).isNull()
    }
}






