package com.joaquin.uala.presentation.cities.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.joaquin.uala.presentation.cities.components.BackFloatingButton
import com.joaquin.uala.presentation.cities.list.CitiesViewModel
import com.joaquin.uala.ui.theme.Primary

@Composable
fun CityDetailScreen(
    navController: NavHostController,
    viewModel: CitiesViewModel
) {
    val city by viewModel.selectedCity.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        city?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorito",
                    tint = if (it.isFavorite) Primary else Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = it.country,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Latitude: ${it.lat}", style = MaterialTheme.typography.bodyMedium)
                Text("Longitude: ${it.lon}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.toggleFavorite(it) },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(if (it.isFavorite) "Quitar de favoritos" else "Agregar a favoritos")
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        BackFloatingButton(
            onBack = { navController.popBackStack() },
            modifier = Modifier.padding( 8.dp)
        )
    }
}

