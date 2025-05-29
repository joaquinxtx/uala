package com.joaquin.uala.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.joaquin.uala.presentation.navigation.AppNavGraph
import com.joaquin.uala.ui.theme.UalaTheme

@Composable
fun MainAppContent() {
   UalaTheme{
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AppNavGraph(navController = navController)
            }
        }
    }
}