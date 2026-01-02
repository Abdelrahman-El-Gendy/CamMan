package com.gndy.camman.presentation.screens.nearby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.UserLocation

@Composable
actual fun NearbyMapView(
    modifier: Modifier,
    userLocation: UserLocation?,
    photographers: List<NearbyPhotographer>,
    selectedPhotographer: NearbyPhotographer?,
    onPhotographerSelected: (NearbyPhotographer?) -> Unit,
    onPhotographerClick: (NearbyPhotographer) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Map integration for iOS coming soon",
            color = Color.White
        )
    }
}
