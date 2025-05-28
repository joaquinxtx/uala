package com.joaquin.uala.presentation.cities.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.presentation.cities.list.components.CityItem
import com.joaquin.uala.presentation.cities.list.components.SearchBar
import com.joaquin.uala.utils.Resource

@Composable
fun CitiesScreen(
    viewModel: CitiesViewModel = hiltViewModel(),
    onClose: () -> Unit
) {

    val citiesState by viewModel.citiesState.collectAsState()
    val favoriteCities by viewModel.favoriteCitiesState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Todos", "Favoritos")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Cerrar búsqueda",
                    modifier = Modifier.size(32.dp)
                )
            }
            SearchBar(
                query = searchQuery,
                onQueryChanged = { query ->
                    val isOnlyFavorites = selectedTabIndex == 1
                    viewModel.updateSearchQuery(query, isOnlyFavorites)
                },
                modifier = Modifier.weight(1f)
            )

        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        viewModel.updateSearchQuery(searchQuery, index == 1)
                    },
                    text = { Text(title) }
                )
            }
        }

        when {
            searchQuery.isNotEmpty() -> {
                when (searchState) {
                    is Resource.Loading -> CircularProgressIndicator()
                    is Resource.Error -> Text("Error: ${(searchState as Resource.Error).message}")
                    is Resource.Success -> {
                        val results = (searchState as Resource.Success).data
                        LazyColumn {
                            items(results) { city ->
                                CityItem(
                                    city = city,
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onClick = {
                                        viewModel.selectCity(city)
                                        onClose()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            selectedTabIndex == 0 -> {
                when (citiesState) {
                    is Resource.Loading -> Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is Resource.Error -> Text("Error: ${(citiesState as Resource.Error).message}")
                    is Resource.Success -> {
                        val cities = (citiesState as Resource.Success).data
                        LazyColumn {
                            itemsIndexed(cities) { index, city ->
                                CityItem(
                                    city = city,
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onClick = {
                                        viewModel.selectCity(city)
                                        onClose()
                                    }
                                )
                                if (index == cities.lastIndex) {
                                    LaunchedEffect(cities.size) {
                                        viewModel.loadNextPage()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            selectedTabIndex == 1 -> {
                LazyColumn {
                    items(favoriteCities) { city ->
                        CityItem(
                            city = city,
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onClick = {
                                viewModel.selectCity(city)
                                onClose()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapView(
    cameraPositionState: CameraPositionState,
    selectedCity: CityModel?
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        selectedCity?.let { city ->
            Marker(
                state = MarkerState(position = LatLng(city.lat, city.lon)),
                title = city.name,
                snippet = city.country
            )
        }
    }
}



