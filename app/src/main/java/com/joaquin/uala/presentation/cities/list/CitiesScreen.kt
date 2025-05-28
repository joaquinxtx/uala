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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joaquin.uala.R
import com.joaquin.uala.domain.model.CityModel
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

    val tabs = listOf(
        stringResource(R.string.tab_all),
        stringResource(R.string.tab_favorites)
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = if (isLandscape) 4.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLandscape) {
                BackFloatingButton(onBack = onClose)
            }

            SearchBar(
                query = searchQuery,
                onQueryChanged = { query ->
                    viewModel.updateSearchQuery(query, selectedTabIndex == 1)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (isLandscape) 0.dp else 8.dp)
            )
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { viewModel.updateSelectedTab(index) },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Primary else Color.Gray
                        )
                    }
                )
            }
        }

        CityListContent(
            selectedTabIndex = selectedTabIndex,
            searchQuery = searchQuery,
            searchState = searchState,
            citiesState = citiesState,
            favoriteCities = favoriteCities,
            onItemClick = {
                viewModel.selectCity(it)
                onClose()
            },
            onToggleFavorite = viewModel::toggleFavorite,
            onLoadMore = viewModel::loadNextPage
        )
    }
}


@Composable
private fun CityListContent(
    selectedTabIndex: Int,
    searchQuery: String,
    searchState: Resource<List<CityModel>>,
    citiesState: Resource<List<CityModel>>,
    favoriteCities: List<CityModel>,
    onItemClick: (CityModel) -> Unit,
    onToggleFavorite: (CityModel) -> Unit,
    onLoadMore: (() -> Unit)? = null
) {
    when {
        searchQuery.isNotEmpty() && selectedTabIndex == 0 -> {
            CityListSection(
                resource = searchState,
                onItemClick = onItemClick,
                onToggleFavorite = onToggleFavorite,
                emptyMessage = stringResource(R.string.search_no_results),
                emptyIcon = Icons.Default.Edit
            )
        }
        searchQuery.isNotEmpty() && selectedTabIndex == 1 -> {
            val filteredFavorites = favoriteCities.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
            CityListSection(
                resource = Resource.Success(filteredFavorites),
                onItemClick = onItemClick,
                onToggleFavorite = onToggleFavorite,
                emptyMessage = stringResource(R.string.search_no_favorites),
                emptyIcon = Icons.Default.Edit
            )
        }
        selectedTabIndex == 0 -> {
            CityListSection(
                resource = citiesState,
                onItemClick = onItemClick,
                onToggleFavorite = onToggleFavorite,
                onLoadMore = onLoadMore,
                emptyMessage = stringResource(R.string.empty_all_cities),
                emptyIcon = Icons.Default.Refresh
            )
        }
        selectedTabIndex == 1 -> {
            CityListSection(
                resource = Resource.Success(favoriteCities),
                onItemClick = onItemClick,
                onToggleFavorite = onToggleFavorite,
                emptyMessage = stringResource(R.string.empty_favorites),
                emptyIcon = Icons.Default.FavoriteBorder
            )
        }
    }
}


















