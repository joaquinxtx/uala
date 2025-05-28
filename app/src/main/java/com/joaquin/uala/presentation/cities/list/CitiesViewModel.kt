package com.joaquin.uala.presentation.cities.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.domain.useCases.CityUseCases
import com.joaquin.uala.utils.NetworkConnectivityObserver
import com.joaquin.uala.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val useCases: CityUseCases,
    private val connectivityObserver: NetworkConnectivityObserver

) : ViewModel() {

    private val _citiesState = MutableStateFlow<Resource<List<CityModel>>>(Resource.Loading)
    val citiesState: StateFlow<Resource<List<CityModel>>> = _citiesState
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchState =
        MutableStateFlow<Resource<List<CityModel>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<CityModel>>> = _searchState

    private val _favoriteCitiesState = MutableStateFlow<List<CityModel>>(emptyList())
    val favoriteCitiesState: StateFlow<List<CityModel>> = _favoriteCitiesState

    private val _selectedCity = MutableStateFlow<CityModel?>(null)
    val selectedCity: StateFlow<CityModel?> = _selectedCity

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    private var allCities: List<CityModel> = emptyList()
    private val visibleCities = mutableListOf<CityModel>()
    private var currentPage = 0
    private val pageSize = 1000

    init {
        observeCitiesWithFavorites()
        observeFavorites()
        observeConnectivity()

    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.networkState.collectLatest { isConnected ->
                val shouldReload = _citiesState.value is Resource.Error || allCities.isEmpty()
                if (isConnected && shouldReload) {
                    observeCitiesWithFavorites()
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            useCases.getFavoriteCities().collectLatest {
                _favoriteCitiesState.value = it
            }
        }
    }
    fun updateSelectedTab(index: Int) {
        _selectedTabIndex.value = index
    }

    private fun observeCitiesWithFavorites() {
        viewModelScope.launch {
            useCases.getAllCities()
                .collectLatest { resource ->
                    _citiesState.value = resource

                    if (resource is Resource.Success) {
                        allCities = resource.data.sortedBy { it.name.lowercase() }
                        currentPage = 0
                        visibleCities.clear()
                        loadNextPage()
                    }
                }
        }
    }

    fun loadNextPage() {
        if (currentPage * pageSize >= allCities.size) return

        val nextPage = allCities.drop(currentPage * pageSize).take(pageSize)
        visibleCities.addAll(nextPage)
        _citiesState.value = Resource.Success(visibleCities.toList())
        currentPage++
    }


    fun toggleFavorite(city: CityModel) {
        viewModelScope.launch {
            useCases.toggleFavorite(city.copy(isFavorite = !city.isFavorite))

            allCities = allCities.map {
                if (it.id == city.id) it.copy(isFavorite = !city.isFavorite) else it
            }

            visibleCities.clear()
            currentPage = 0
            loadNextPage()

            _selectedCity.value = city.copy(isFavorite = !city.isFavorite)
        }
    }


    fun searchCities(query: String, onlyFavorites: Boolean) {
        val filtered = allCities.filter {
            it.name.contains(query, ignoreCase = true) &&
                    (!onlyFavorites || it.isFavorite)
        }.sortedBy { it.name }

        _searchState.value = Resource.Success(filtered)
    }

    fun updateSearchQuery(query: String, onlyFavorites: Boolean) {
        _searchQuery.value = query
        searchCities(query, onlyFavorites)
    }

    fun selectCity(city: CityModel) {
        _selectedCity.value = city
    }

    fun clearSelectedCity() {
        _selectedCity.value = null
    }
}


