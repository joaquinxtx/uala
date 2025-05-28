package com.joaquin.uala.presentation.cities.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joaquin.uala.presentation.cities.components.BackFloatingButton
import com.joaquin.uala.presentation.cities.list.components.CityListSection
import com.joaquin.uala.presentation.cities.list.components.SearchBar
import com.joaquin.uala.ui.theme.Primary
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
                BackFloatingButton(
                    onBack = { onClose() },
                )
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
        TabRow(selectedTabIndex = selectedTabIndex, indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Primary
            )
        }) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        viewModel.updateSelectedTab(index)
                        viewModel.updateSearchQuery(searchQuery, index == 1)
                    },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Primary else Color.Gray
                        )
                    }
                )
            }
        }
        when {
            searchQuery.isNotEmpty() -> {
                CityListSection(
                    resource = searchState,
                    onItemClick = { viewModel.selectCity(it); onClose() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    emptyMessage = "No se encontraron ciudades",
                    emptyIcon = Icons.Default.Edit
                )
            }

            selectedTabIndex == 0 -> {
                CityListSection(
                    resource = citiesState,
                    onItemClick = { viewModel.selectCity(it); onClose() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onLoadMore = viewModel::loadNextPage,
                    emptyMessage = "No hay ciudades disponibles",
                    emptyIcon = Icons.Default.Refresh
                )
            }

            selectedTabIndex == 1 -> {
                CityListSection(
                    resource = Resource.Success(favoriteCities),
                    onItemClick = { viewModel.selectCity(it); onClose() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    emptyMessage = "Sin ciudades en favoritos",
                    emptyIcon = Icons.Default.FavoriteBorder
                )
            }
        }

    }
}
















