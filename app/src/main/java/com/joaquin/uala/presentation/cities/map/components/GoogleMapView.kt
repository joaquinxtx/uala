package com.joaquin.uala.presentation.cities.map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.joaquin.uala.domain.model.CityModel

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