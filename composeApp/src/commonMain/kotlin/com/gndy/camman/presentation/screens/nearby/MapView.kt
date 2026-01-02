package com.gndy.camman.presentation.screens.nearby

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.UserLocation

@Composable
expect fun NearbyMapView(
    modifier: Modifier = Modifier,
    userLocation: UserLocation?,
    photographers: List<NearbyPhotographer>,
    selectedPhotographer: NearbyPhotographer?,
    onPhotographerSelected: (NearbyPhotographer?) -> Unit,
    onPhotographerClick: (NearbyPhotographer) -> Unit
)
