package com.joaquin.uala.presentation.cities.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.joaquin.uala.R
import com.joaquin.uala.domain.model.CityModel
import com.joaquin.uala.presentation.cities.components.EmptyState
import com.joaquin.uala.utils.Resource

@Composable
fun CityListSection(
    resource: Resource<List<CityModel>>,
    onItemClick: (CityModel) -> Unit,
    onToggleFavorite: (CityModel) -> Unit,
    onLoadMore: (() -> Unit)? = null,
    emptyMessage: String = stringResource(R.string.empty_all_cities),
    emptyIcon: ImageVector = Icons.Default.Info
) {
    val defaultErrorMessage = stringResource(id = R.string.unknown_error)

    when (resource) {
        is Resource.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        is Resource.Error -> EmptyState(
            message =  defaultErrorMessage,
            icon = Icons.Default.Refresh
        )

        is Resource.Success -> {
            val cities = resource.data
            if (cities.isEmpty()) {
                EmptyState(message = emptyMessage, icon = emptyIcon)
            } else {
                LazyColumn {
                    itemsIndexed(cities) { index, city ->
                        CityItem(
                            city = city,
                            onToggleFavorite = onToggleFavorite,
                            onClick = { onItemClick(city) }
                        )
                        if (index == cities.lastIndex) {
                            onLoadMore?.let {
                                LaunchedEffect(cities.size) { it() }
                            }
                        }
                    }
                }
            }
        }
    }
}
