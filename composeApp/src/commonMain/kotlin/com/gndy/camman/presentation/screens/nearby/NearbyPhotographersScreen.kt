package com.gndy.camman.presentation.screens.nearby

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.AvailabilityStatus
import com.gndy.camman.domain.model.DistanceRadius
import com.gndy.camman.domain.model.NearbyPhotographer
import com.gndy.camman.domain.model.PhotographyType
import com.gndy.camman.domain.model.PriceRange
import com.gndy.camman.presentation.theme.Gold
import com.gndy.camman.presentation.theme.Success
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPhotographersScreen(
    onNavigateToPhotographer: (String) -> Unit,
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToBooking: (String) -> Unit = {},
    viewModel: NearbyPhotographersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is NearbyPhotographersUiEvent.NavigateToPhotographer -> {
                    onNavigateToPhotographer(event.photographerId)
                }
                is NearbyPhotographersUiEvent.NavigateToChat -> {
                    onNavigateToChat(event.photographerId)
                }
                is NearbyPhotographersUiEvent.NavigateToBooking -> {
                    onNavigateToBooking(event.photographerId)
                }
                is NearbyPhotographersUiEvent.NavigateToQuoteRequest -> {
                    // Handle quote request navigation
                }
                is NearbyPhotographersUiEvent.RequestLocationPermission -> {
                    // Permission request is handled at the screen level
                }
                is NearbyPhotographersUiEvent.LocationPermissionDenied -> {
                    // Show denied state
                }
                is NearbyPhotographersUiEvent.OpenLocationSettings -> {
                    // Open location settings
                }
                is NearbyPhotographersUiEvent.ShowError -> {
                    // Show error snackbar
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nearby Photographers",
                            fontWeight = FontWeight.Bold
                        )
                        uiState.userLocation?.let {
                            Text(
                                text = "Within ${uiState.selectedDistanceRadius.radiusKm} km",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    // View mode toggle
                    IconButton(onClick = {
                        viewModel.onViewModeChanged(
                            if (uiState.viewMode == ViewMode.LIST) ViewMode.MAP else ViewMode.LIST
                        )
                    }) {
                        Icon(
                            imageVector = if (uiState.viewMode == ViewMode.LIST) Icons.Default.Map else Icons.Default.List,
                            contentDescription = "Toggle view",
                            tint = Gold
                        )
                    }
                    
                    // Filter button
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = if (hasActiveFilters(uiState)) Gold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Location permission not granted
            if (!uiState.hasLocationPermission) {
                LocationPermissionRequired(
                    onRequestPermission = { viewModel.requestLocationPermission() }
                )
            }
            // Location services disabled
            else if (!uiState.isLocationEnabled) {
                LocationServicesDisabled(
                    onOpenSettings = { viewModel.openLocationSettings() }
                )
            }
            // Loading state
            else if (uiState.isLoading && uiState.photographers.isEmpty()) {
                LoadingState()
            }
            // Error state
            else if (uiState.error != null && uiState.photographers.isEmpty()) {
                ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.retry() }
                )
            }
            // Content
            else {
                // Quick filters row
                QuickFiltersRow(
                    selectedType = uiState.selectedPhotographyType,
                    onTypeSelected = { viewModel.onPhotographyTypeChanged(it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Distance slider
                DistanceSlider(
                    selectedRadius = uiState.selectedDistanceRadius,
                    onRadiusChanged = { viewModel.onDistanceRadiusChanged(it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Results count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.photographers.size} photographer${if (uiState.photographers.size != 1) "s" else ""} found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    // Available only toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = uiState.showAvailableOnly,
                            onCheckedChange = { viewModel.onAvailableOnlyChanged(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Gold,
                                checkedTrackColor = Gold.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Content based on view mode
                when (uiState.viewMode) {
                    ViewMode.LIST -> {
                        PhotographersListView(
                            photographers = uiState.photographers,
                            onPhotographerClick = { viewModel.onPhotographerClick(it.id) },
                            onChatClick = { viewModel.onChatClick(it.id) },
                            onBookClick = { viewModel.onBookClick(it.id) },
                            onQuoteClick = { viewModel.onRequestQuoteClick(it.id) }
                        )
                    }
                    ViewMode.MAP -> {
                        MapViewPlaceholder(
                            photographers = uiState.photographers,
                            selectedPhotographer = uiState.selectedPhotographer,
                            onPhotographerSelected = { viewModel.onPhotographerSelected(it) },
                            onPhotographerClick = { viewModel.onPhotographerClick(it.id) }
                        )
                    }
                }
            }
        }
        
        // Filter bottom sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState
            ) {
                FilterBottomSheet(
                    selectedType = uiState.selectedPhotographyType,
                    selectedPriceRange = uiState.selectedPriceRange,
                    selectedRadius = uiState.selectedDistanceRadius,
                    showAvailableOnly = uiState.showAvailableOnly,
                    onTypeSelected = { viewModel.onPhotographyTypeChanged(it) },
                    onPriceRangeSelected = { viewModel.onPriceRangeChanged(it) },
                    onRadiusSelected = { viewModel.onDistanceRadiusChanged(it) },
                    onAvailableOnlyChanged = { viewModel.onAvailableOnlyChanged(it) },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}

private fun hasActiveFilters(state: NearbyPhotographersUiState): Boolean {
    return state.selectedPhotographyType != PhotographyType.ALL ||
           state.selectedPriceRange != PriceRange.ANY ||
           state.showAvailableOnly
}

@Composable
private fun LocationPermissionRequired(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Gold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location Access Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We need access to your location to find photographers near you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Location", color = Color.Black)
            }
        }
    }
}

@Composable
private fun LocationServicesDisabled(
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location Services Disabled",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enable location services in your device settings to find nearby photographers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Text("Open Settings", color = Color.Black)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Gold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Finding photographers near you...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Text("Try Again", color = Color.Black)
            }
        }
    }
}

@Composable
private fun QuickFiltersRow(
    selectedType: PhotographyType,
    onTypeSelected: (PhotographyType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(PhotographyType.entries) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(type.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Gold,
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun DistanceSlider(
    selectedRadius: DistanceRadius,
    onRadiusChanged: (DistanceRadius) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search Radius",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = selectedRadius.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
        }
        
        Slider(
            value = DistanceRadius.entries.indexOf(selectedRadius).toFloat(),
            onValueChange = { value ->
                val index = value.toInt().coerceIn(0, DistanceRadius.entries.size - 1)
                onRadiusChanged(DistanceRadius.entries[index])
            },
            valueRange = 0f..(DistanceRadius.entries.size - 1).toFloat(),
            steps = DistanceRadius.entries.size - 2,
            colors = SliderDefaults.colors(
                thumbColor = Gold,
                activeTrackColor = Gold,
                inactiveTrackColor = Gold.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun PhotographersListView(
    photographers: List<NearbyPhotographer>,
    onPhotographerClick: (NearbyPhotographer) -> Unit,
    onChatClick: (NearbyPhotographer) -> Unit,
    onBookClick: (NearbyPhotographer) -> Unit,
    onQuoteClick: (NearbyPhotographer) -> Unit
) {
    if (photographers.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No photographers found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Try increasing the search radius or adjusting filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(photographers) { photographer ->
                NearbyPhotographerCard(
                    photographer = photographer,
                    onClick = { onPhotographerClick(photographer) },
                    onChatClick = { onChatClick(photographer) },
                    onBookClick = { onBookClick(photographer) },
                    onQuoteClick = { onQuoteClick(photographer) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
            }
        }
    }
}

@Composable
private fun NearbyPhotographerCard(
    photographer: NearbyPhotographer,
    onClick: () -> Unit,
    onChatClick: () -> Unit,
    onBookClick: () -> Unit,
    onQuoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Cover Image with Profile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Cover Image
                AsyncImage(
                    model = photographer.coverImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                
                // Distance Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Gold
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${photographer.distanceKm} km",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                
                // Availability Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = when (photographer.availabilityStatus) {
                        AvailabilityStatus.AVAILABLE -> Success
                        AvailabilityStatus.BUSY -> Color(0xFFFFA726)
                        AvailabilityStatus.AWAY -> Color.Gray
                        AvailabilityStatus.OFFLINE -> Color.DarkGray
                    }
                ) {
                    Text(
                        text = when (photographer.availabilityStatus) {
                            AvailabilityStatus.AVAILABLE -> "Available"
                            AvailabilityStatus.BUSY -> "Busy"
                            AvailabilityStatus.AWAY -> "Away"
                            AvailabilityStatus.OFFLINE -> "Offline"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                
                // Profile Image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp)
                        .offset(y = 30.dp)
                ) {
                    AsyncImage(
                        model = photographer.profileImageUrl,
                        contentDescription = photographer.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 12.dp)
            ) {
                // Name and Rating Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photographer.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${photographer.rating}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${photographer.reviewCount})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Location and Travel Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = photographer.location ?: "Location not specified",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    photographer.estimatedTravelTime?.let { time ->
                        Text(
                            text = " • $time away",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bio
                photographer.bio?.let { bio ->
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Specialties
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photographer.specialties.take(4)) { specialty ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Gold.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = specialty,
                                style = MaterialTheme.typography.labelMedium,
                                color = Gold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Price and Response Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    photographer.startingPrice?.let { price ->
                        Text(
                            text = "From $${price.toInt()} ${photographer.currency}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                    
                    photographer.responseTime?.let { time ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // View Profile
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Gold
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Profile", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    // Chat
                    OutlinedButton(
                        onClick = onChatClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat", style = MaterialTheme.typography.labelMedium)
                    }
                    
                    // Book
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        enabled = photographer.isAvailable
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun MapViewPlaceholder(
    photographers: List<NearbyPhotographer>,
    selectedPhotographer: NearbyPhotographer?,
    onPhotographerSelected: (NearbyPhotographer?) -> Unit,
    onPhotographerClick: (NearbyPhotographer) -> Unit
) {
    // Placeholder for map view - in a real implementation, you would integrate
    // Google Maps for Android or MapKit for iOS using expect/actual
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Gold.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Map View",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${photographers.size} photographers in this area",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Show list of photographers with distances for map view
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(photographers.take(5)) { photographer ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPhotographerClick(photographer) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedPhotographer == photographer) 
                            Gold.copy(alpha = 0.2f) 
                        else 
                            MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = photographer.profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = photographer.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${photographer.distanceKm} km • ${photographer.estimatedTravelTime ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Gold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    selectedType: PhotographyType,
    selectedPriceRange: PriceRange,
    selectedRadius: DistanceRadius,
    showAvailableOnly: Boolean,
    onTypeSelected: (PhotographyType) -> Unit,
    onPriceRangeSelected: (PriceRange) -> Unit,
    onRadiusSelected: (DistanceRadius) -> Unit,
    onAvailableOnlyChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filters",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Photography Type
        Text(
            text = "Photography Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PhotographyType.entries) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Price Range
        Text(
            text = "Price Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PriceRange.entries) { range ->
                FilterChip(
                    selected = selectedPriceRange == range,
                    onClick = { onPriceRangeSelected(range) },
                    label = { Text(range.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Distance Radius
        Text(
            text = "Distance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DistanceRadius.entries) { radius ->
                FilterChip(
                    selected = selectedRadius == radius,
                    onClick = { onRadiusSelected(radius) },
                    label = { Text(radius.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Available Only
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Available Only",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Show only photographers who are currently available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = showAvailableOnly,
                onCheckedChange = onAvailableOnlyChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Gold,
                    checkedTrackColor = Gold.copy(alpha = 0.5f)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Apply Button
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold)
        ) {
            Text("Apply Filters", color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
