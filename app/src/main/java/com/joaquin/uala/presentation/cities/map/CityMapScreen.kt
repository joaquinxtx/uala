package com.joaquin.uala.presentation.cities.map

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.joaquin.uala.presentation.cities.map.components.GoogleMapView
import com.joaquin.uala.presentation.cities.map.components.SearchHeaderBar
import com.joaquin.uala.presentation.navigation.Screen
import com.joaquin.uala.ui.theme.Primary

@Composable
fun CityMapScreen(
    navController: NavHostController,
    viewModel: CitiesViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isSearchVisible by viewModel.isSearchVisible.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

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
                        .weight(0.4f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    CitiesScreen(
                        viewModel = viewModel,
                        onClose = {}
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                ) {
                    GoogleMapView(
                        cameraPositionState = cameraPositionState,
                        selectedCity = selectedCity
                    )

                    androidx.compose.animation.AnimatedVisibility(
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
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(48.dp)
                        ) {
                            Text("Ver detalle")
                        }
                    }
                }
            }
        }
        else {
            GoogleMapView(
                cameraPositionState = cameraPositionState,
                selectedCity = selectedCity
            )

            SearchHeaderBar(
                queryText = selectedCity?.name.orEmpty(),
                onClick = { viewModel.setSearchVisibility(true) },
                onClear = if (selectedCity != null) {
                    { viewModel.clearSelectedCity() }
                } else null,
                modifier = Modifier.align(Alignment.TopCenter)
            )

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
                        onClose = { viewModel.setSearchVisibility(false) }
                    )
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
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFF0D47A1),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                ) {
                    Text("Ver detalle")
                }
            }
        }
    }
}






