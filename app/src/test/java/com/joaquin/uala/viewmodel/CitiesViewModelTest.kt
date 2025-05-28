package com.joaquin.uala.viewmodel

import com.google.common.truth.Truth.assertThat
import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.useCases.CityUseCases
import com.joaquin.uala.presentation.cities.list.CitiesViewModel
import com.joaquin.uala.utils.Resource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CitiesViewModelTest {

    private lateinit var viewModel: CitiesViewModel
    private val useCases: CityUseCases = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { useCases.getFavoriteCities() } returns flowOf(emptyList())
        every { useCases.getAllCities() } returns flowOf(Resource.Success(emptyList()))
        viewModel = CitiesViewModel(useCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observeCitiesWithFavorites should emit Resource Success with sorted data`() = runTest {
        val list = listOf(
            CityModel(2, "Zeta", "AR", 0.0, 0.0, false),
            CityModel(1, "Alpha", "AR", 0.0, 0.0, false)
        )
        every { useCases.getAllCities() } returns flowOf(Resource.Success(list))

        viewModel = CitiesViewModel(useCases)
        advanceUntilIdle()

        val result = viewModel.citiesState.value
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data.first().name).isEqualTo("Alpha")
    }

    @Test
    fun `toggleFavorite should update favorite state`() = runTest {
        val city = CityModel(1, "City", "AR", 0.0, 0.0, false)
        every { useCases.getAllCities() } returns flowOf(Resource.Success(listOf(city)))
        coEvery { useCases.toggleFavorite(any()) } just Runs

        viewModel = CitiesViewModel(useCases)
        advanceUntilIdle()

        viewModel.toggleFavorite(city)
        advanceUntilIdle()

        val result = viewModel.citiesState.value
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data.first().isFavorite).isTrue()
    }

    @Test
    fun `searchCities should return filtered cities`() = runTest {
        val city1 = CityModel(1, "Buenos Aires", "AR", 0.0, 0.0, true)
        val city2 = CityModel(2, "Córdoba", "AR", 0.0, 0.0, false)
        every { useCases.getAllCities() } returns flowOf(Resource.Success(listOf(city1, city2)))

        viewModel = CitiesViewModel(useCases)
        advanceUntilIdle()

        viewModel.updateSearchQuery("buenos", onlyFavorites = false)
        val result = viewModel.searchState.value
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data).containsExactly(city1)
    }

    @Test
    fun `updateSearchQuery should trigger search`() = runTest {
        val city = CityModel(1, "Rosario", "AR", 0.0, 0.0, true)
        every { useCases.getAllCities() } returns flowOf(Resource.Success(listOf(city)))

        viewModel = CitiesViewModel(useCases)
        advanceUntilIdle()

        viewModel.updateSearchQuery("ros", onlyFavorites = true)
        val result = viewModel.searchState.value
        assertThat((result as Resource.Success).data).containsExactly(city)
    }

    @Test
    fun `selectCity should update selectedCity state`() = runTest {
        val city = CityModel(42, "La Plata", "AR", 0.0, 0.0, false)
        viewModel.selectCity(city)
        assertThat(viewModel.selectedCity.value).isEqualTo(city)
    }

    @Test
    fun `clearSelectedCity should set selectedCity to null`() = runTest {
        val city = CityModel(10, "Salta", "AR", 0.0, 0.0, false)
        viewModel.selectCity(city)
        viewModel.clearSelectedCity()
        assertThat(viewModel.selectedCity.value).isNull()
    }
}


