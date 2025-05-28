package com.joaquin.uala.presentation.cities.list

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val citiesState by viewModel.citiesState.collectAsState()
    val favoriteCities by viewModel.favoriteCitiesState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

    val searchState by viewModel.searchState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val tabs = listOf("Todos", "Favoritos")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = if (isLandscape) 4.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLandscape) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Cerrar búsqueda",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            SearchBar(
                query = searchQuery,
                onQueryChanged = { query ->
                    val isOnlyFavorites = selectedTabIndex == 1
                    viewModel.updateSearchQuery(query, isOnlyFavorites)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (isLandscape) 0.dp else 8.dp)
            )
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        viewModel.updateSelectedTab(index)
                        viewModel.updateSearchQuery(searchQuery, index == 1)
                    },
                    text = { Text(title) }
                )
            }
        }

        when {
            // 🔍 Resultados de búsqueda
            searchQuery.isNotEmpty() -> {
                when (searchState) {
                    is Resource.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is Resource.Error -> Text("Error: ${(searchState as Resource.Error).message}")

                    is Resource.Success -> {
                        val results = (searchState as Resource.Success).data
                        if (results.isEmpty()) {
                            EmptyState(
                                message = "No se encontraron ciudades",
                                icon = Icons.Default.Edit
                            )
                        } else {
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
            }

            // 🌍 Todos
            selectedTabIndex == 0 -> {
                when (citiesState) {
                    is Resource.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is Resource.Error -> EmptyState(
                        message = "Error al cargar las ciudades",
                        icon = Icons.Default.Refresh
                    )

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

            // ⭐ Favoritos
            selectedTabIndex == 1 -> {
                if (favoriteCities.isEmpty()) {
                    EmptyState(
                        message = "Sin ciudades en favoritos",
                        icon = Icons.Default.FavoriteBorder
                    )
                } else {
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
}


@Composable
fun EmptyState(
    message: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
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



