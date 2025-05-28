package com.joaquin.uala.presentation.cities.map

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.joaquin.uala.presentation.cities.list.CitiesScreen
import com.joaquin.uala.presentation.cities.list.CitiesViewModel
import com.joaquin.uala.presentation.cities.list.GoogleMapView
import com.joaquin.uala.presentation.navigation.Screen

@Composable
fun CityMapScreen(
    navController: NavHostController,
    viewModel: CitiesViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val selectedCity by viewModel.selectedCity.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    var isSearchVisible by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCity) {
        selectedCity?.let { city ->
            val position = CameraUpdateFactory.newLatLngZoom(
                LatLng(city.lat, city.lon), 10f
            )
            cameraPositionState.move(position)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    CitiesScreen(
                        viewModel = viewModel,
                        onClose = {}
                    )
                }

                Box(modifier = Modifier.weight(0.5f)) {
                    GoogleMapView(
                        cameraPositionState = cameraPositionState,
                        selectedCity = selectedCity,
                    )
                }
            }
        } else {
            GoogleMapView(
                cameraPositionState = cameraPositionState,
                selectedCity = selectedCity
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSearchVisible = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = selectedCity?.name ?: "Buscar ciudad...",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    if (selectedCity != null) {
                        IconButton(
                            onClick = { viewModel.clearSelectedCity() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Borrar selección",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    CitiesScreen(
                        viewModel = viewModel,
                        onClose = { isSearchVisible = false }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = selectedCity != null && !isSearchVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            ElevatedButton(
                onClick = {
                    selectedCity?.let {
                        navController.navigate(Screen.CityDetail.route)
                    }
                }
            ) {
                Text("Ver detalle")
            }
        }
    }
}



