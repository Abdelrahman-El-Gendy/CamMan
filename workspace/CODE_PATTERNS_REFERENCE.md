# CamMan - Code Patterns & Implementation Reference

A practical guide with copy-paste ready examples for implementing the "Nearby Photographers" feature.

---

## 1. ViewModel Pattern (StateFlow + SharedFlow)

### Template Structure

```kotlin
// File: presentation/screens/nearby/NearbyPhotographersViewModel.kt

package com.gndy.camman.presentation.screens.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.repository.LocationRepository
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State - Immutable data class
data class NearbyPhotographersUiState(
    val isLoading: Boolean = true,
    val photographers: List<Photographer> = emptyList(),
    val userLocation: UserLocation? = null,
    val selectedDistance: Int = 10, // kilometers
    val error: String? = null,
    val isLocationEnabled: Boolean = false
)

// Data class for user location
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)

// UI Events - One-time events
sealed class NearbyPhotographersUiEvent {
    data class NavigateToPhotographer(val photographerId: String) : NearbyPhotographersUiEvent()
    data class ShowError(val message: String) : NearbyPhotographersUiEvent()
    data object RequestLocationPermission : NearbyPhotographersUiEvent()
    data object LocationPermissionDenied : NearbyPhotographersUiEvent()
}

// ViewModel
class NearbyPhotographersViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyPhotographersUiState())
    val uiState: StateFlow<NearbyPhotographersUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<NearbyPhotographersUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        initializeLocationTracking()
    }

    private fun initializeLocationTracking() {
        viewModelScope.launch {
            locationRepository.observeUserLocation().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        resource.data?.let { location ->
                            _uiState.update { it.copy(userLocation = location, isLocationEnabled = true) }
                            loadNearbyPhotographers(location)
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message,
                                isLocationEnabled = false
                            )
                        }
                        _uiEvents.emit(
                            NearbyPhotographersUiEvent.ShowError(
                                resource.message ?: "Failed to get location"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadNearbyPhotographers(location: UserLocation) {
        viewModelScope.launch {
            locationRepository.getNearbyPhotographers(
                latitude = location.latitude,
                longitude = location.longitude,
                radiusKm = _uiState.value.selectedDistance
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                photographers = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                        _uiEvents.emit(
                            NearbyPhotographersUiEvent.ShowError(
                                resource.message ?: "Failed to load nearby photographers"
                            )
                        )
                    }
                }
            }
        }
    }

    fun onPhotographerClick(photographerId: String) {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.NavigateToPhotographer(photographerId))
        }
    }

    fun onDistanceChanged(distance: Int) {
        _uiState.update { it.copy(selectedDistance = distance) }
        _uiState.value.userLocation?.let { location ->
            loadNearbyPhotographers(location)
        }
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.RequestLocationPermission)
        }
    }

    fun onLocationPermissionGranted() {
        initializeLocationTracking()
    }

    fun onLocationPermissionDenied() {
        viewModelScope.launch {
            _uiEvents.emit(NearbyPhotographersUiEvent.LocationPermissionDenied)
        }
    }

    fun refresh() {
        initializeLocationTracking()
    }
}
```

---

## 2. Repository Pattern (Domain & Data Layers)

### Step 1: Domain Repository Interface

```kotlin
// File: domain/repository/LocationRepository.kt

package com.gndy.camman.domain.repository

import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.util.Resource
import com.gndy.camman.presentation.screens.nearby.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    /**
     * Observe user's current location (continuous updates)
     */
    fun observeUserLocation(): Flow<Resource<UserLocation?>>

    /**
     * Get photographers near the user's location
     */
    fun getNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int = 10
    ): Flow<Resource<List<Photographer>>>

    /**
     * Request location permissions
     */
    suspend fun requestLocationPermission(): Boolean

    /**
     * Check if location permissions are granted
     */
    suspend fun hasLocationPermission(): Boolean

    /**
     * Get last known location (cached)
     */
    suspend fun getLastKnownLocation(): UserLocation?
}
```

### Step 2: Data Repository Implementation

```kotlin
// File: data/repository/LocationRepositoryImpl.kt

package com.gndy.camman.data.repository

import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.domain.repository.LocationRepository
import com.gndy.camman.domain.util.Resource
import com.gndy.camman.presentation.screens.nearby.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch

class LocationRepositoryImpl(
    private val apiService: CamManApiService,
    private val locationService: LocationService  // Platform-specific
) : LocationRepository {

    private var lastKnownLocation: UserLocation? = null

    override fun observeUserLocation(): Flow<Resource<UserLocation?>> = flow {
        locationService.observeLocation().collect { location ->
            lastKnownLocation = location
            emit(Resource.Success(location))
        }
    }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message ?: "Failed to get location"))
        }

    override fun getNearbyPhotographers(
        latitude: Double,
        longitude: Double,
        radiusKm: Int
    ): Flow<Resource<List<Photographer>>> = flow {
        try {
            val response = apiService.getNearbyPhotographers(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm
            )
            val photographers = response.photographers.map { it.toPhotographer() }
            emit(Resource.Success(photographers))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch nearby photographers"))
        }
    }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            emit(Resource.Error(e.message ?: "Network error"))
        }

    override suspend fun requestLocationPermission(): Boolean {
        return locationService.requestPermission()
    }

    override suspend fun hasLocationPermission(): Boolean {
        return locationService.hasPermission()
    }

    override suspend fun getLastKnownLocation(): UserLocation? {
        return lastKnownLocation
    }

    private fun LocationDto.toPhotographer(): Photographer {
        return Photographer(
            id = id,
            name = name,
            profileImageUrl = profileImageUrl,
            coverImageUrl = coverImageUrl,
            bio = bio,
            specialties = specialties,
            location = location,
            rating = rating,
            reviewCount = reviewCount,
            startingPrice = startingPrice,
            currency = currency,
            isAvailable = isAvailable,
            portfolioPreviewUrls = portfolioUrls
        )
    }
}
```

---

## 3. API Service Extension

```kotlin
// Add to: data/remote/api/CamManApiService.kt

// Add these endpoints to the CamManApiService class:

suspend fun getNearbyPhotographers(
    latitude: Double,
    longitude: Double,
    radiusKm: Int = 10,
    limit: Int = 20
): NearbyPhotographersResponse {
    return httpClient.get("$BASE_URL/photographers/nearby") {
        parameter("lat", latitude)
        parameter("lng", longitude)
        parameter("radius", radiusKm)
        parameter("limit", limit)
    }.body()
}

suspend fun getPhotographerDistance(
    photographerId: String,
    latitude: Double,
    longitude: Double
): DistanceDto {
    return httpClient.get("$BASE_URL/photographers/$photographerId/distance") {
        parameter("lat", latitude)
        parameter("lng", longitude)
    }.body()
}
```

### Add DTOs

```kotlin
// File: data/remote/dto/LocationDto.kt

package com.gndy.camman.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NearbyPhotographersResponse(
    val photographers: List<PhotographerLocationDto>,
    val timestamp: Long
)

@Serializable
data class PhotographerLocationDto(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val bio: String?,
    val specialties: List<String>,
    val location: String?,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val reviewCount: Int,
    val startingPrice: Double?,
    val currency: String = "USD",
    val isAvailable: Boolean = true,
    val portfolioPreviewUrls: List<String> = emptyList(),
    val distanceKm: Double,  // Distance from user
    val estimatedTravelTime: String?  // e.g., "15 mins"
)

@Serializable
data class DistanceDto(
    val photographerId: String,
    val distanceKm: Double,
    val estimatedTravelTime: String
)
```

---

## 4. Platform-Specific Location Service

### Android Implementation

```kotlin
// File: androidMain/kotlin/com/gndy/camman/data/location/LocationService.kt

package com.gndy.camman.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.gndy.camman.presentation.screens.nearby.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

expect interface LocationService {
    fun observeLocation(): Flow<UserLocation?>
    suspend fun requestPermission(): Boolean
    suspend fun hasPermission(): Boolean
}

class AndroidLocationService(
    private val context: Context
) : LocationService {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(): Flow<UserLocation?> = flow {
        if (!hasPermission()) {
            emit(null)
            return@flow
        }

        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                emit(
                    UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy
                    )
                )
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun requestPermission(): Boolean {
        // This should be handled at the screen/ViewModel level
        // using a permission request launcher
        return hasPermission()
    }

    override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}
```

### iOS Implementation

```kotlin
// File: iosMain/kotlin/com/gndy/camman/data/location/LocationService.kt

package com.gndy.camman.data.location

import com.gndy.camman.presentation.screens.nearby.UserLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class LocationService {

    actual fun observeLocation(): Flow<UserLocation?> = flow {
        // iOS implementation using CoreLocation
        // This would use platform interop with Swift
        emit(null)
    }

    actual suspend fun requestPermission(): Boolean {
        // iOS implementation
        return false
    }

    actual suspend fun hasPermission(): Boolean {
        // iOS implementation
        return false
    }
}
```

---

## 5. Screen Implementation with Map

```kotlin
// File: presentation/screens/nearby/NearbyPhotographersScreen.kt

package com.gndy.camman.presentation.screens.nearby

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.domain.model.Photographer
import com.gndy.camman.presentation.theme.Gold
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPhotographersScreen(
    onNavigateToPhotographer: (String) -> Unit,
    viewModel: NearbyPhotographersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is NearbyPhotographersUiEvent.NavigateToPhotographer -> {
                    onNavigateToPhotographer(event.photographerId)
                }

                is NearbyPhotographersUiEvent.RequestLocationPermission -> {
                    // Handle permission request
                    viewModel.requestLocationPermission()
                }

                is NearbyPhotographersUiEvent.LocationPermissionDenied -> {
                    // Show snackbar
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
                    Text(
                        text = "Nearby Photographers",
                        fontWeight = FontWeight.Bold
                    )
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
            if (!uiState.isLocationEnabled) {
                // Permission denied state
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location disabled",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp),
                            tint = Gold
                        )
                        Text(
                            text = "Enable Location",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Allow location access to find photographers near you",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.requestLocationPermission() },
                            colors = ButtonDefaults.buttonColors(containerColor = Gold)
                        ) {
                            Text("Enable Location", color = Color.Black)
                        }
                    }
                }
            } else if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (uiState.photographers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No photographers nearby",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Try increasing the search distance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                // Distance Slider
                Column(modifier = Modifier.padding(16.dp)) {
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
                            text = "${uiState.selectedDistance} km",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = uiState.selectedDistance.toFloat(),
                        onValueChange = { viewModel.onDistanceChanged(it.toInt()) },
                        valueRange = 1f..50f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Photographers List
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.photographers) { photographer ->
                        NearbyPhotographerCard(
                            photographer = photographer,
                            onClick = { viewModel.onPhotographerClick(photographer.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyPhotographerCard(
    photographer: Photographer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Image
            AsyncImage(
                model = photographer.profileImageUrl,
                contentDescription = photographer.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = photographer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = photographer.location ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "★ ${photographer.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold
                    )
                    Text(
                        text = " (${photographer.reviewCount})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Distance (if available)
            // You can add distance display here if included in the response
        }
    }
}
```

---

## 6. Navigation Integration

### Add to Screen.kt

```kotlin
@Serializable
data object NearbyPhotographers : Screen()
```

### Add to NavGraph.kt

```kotlin
composable<Screen.NearbyPhotographers> {
    NearbyPhotographersScreen(
        onNavigateToPhotographer = { photographerId ->
            navController.navigate(Screen.PhotographerDetail(photographerId))
        }
    )
}
```

### Add to Bottom Navigation (BrowsePhotographersScreen.kt)

```kotlin
// In the UserMainScreen, add to BottomNavigationItem:
BottomNavigationItem(
    icon = Icons.Default.LocationOn,
    label = "Nearby",
    selected = currentRoute == Screen.NearbyPhotographers::class.simpleName,
    onClick = {
        navController.navigate(Screen.NearbyPhotographers)
    }
)
```

---

## 7. Dependency Injection Setup

### Add to di/AppModule.kt

```kotlin
// Add to sharedModule:

// Location Repository
single<LocationRepository> { LocationRepositoryImpl(get(), get()) }

// ViewModels
viewModel { NearbyPhotographersViewModel(get()) }
```

### Platform-Specific DI

```kotlin
// androidMain/di/AppModule.android.kt
actual val platformModule = module {
    single { AndroidLocationService(get()) }
}

// iosMain/di/AppModule.ios.kt
actual val platformModule = module {
    single { IosLocationService() }
}
```

---

## 8. AndroidManifest.xml Updates

```xml
<!-- Add to composeApp/src/androidMain/AndroidManifest.xml -->

<!-- Location Permissions -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Optional: For background location tracking -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

---

## 9. Test Data / Mock Data

```kotlin
// Add to LocationRepositoryImpl for testing without real API:

private fun getMockNearbyPhotographers(): List<Photographer> {
    return listOf(
        Photographer(
            id = "nearby_1",
            name = "Alex Rivera",
            profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            coverImageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800",
            bio = "Award-winning photographer 2.5 km away",
            specialties = listOf("Wedding", "Portrait"),
            location = "Downtown, 2.5 km",
            rating = 4.9f,
            reviewCount = 127,
            startingPrice = 199.0,
            isAvailable = true
        ),
        // ... more photographers
    )
}
```

---

## 10. Common Patterns Summary

| Pattern | Example File | Purpose |
|---------|--------------|---------|
| ViewModel | `presentation/screens/nearby/NearbyPhotographersViewModel.kt` | State management with StateFlow/SharedFlow |
| Repository Interface | `domain/repository/LocationRepository.kt` | Abstract location operations |
| Repository Implementation | `data/repository/LocationRepositoryImpl.kt` | Concrete implementation with DTO mapping |
| API Service Extension | Add to `data/remote/api/CamManApiService.kt` | REST API calls |
| DTO Classes | `data/remote/dto/LocationDto.kt` | JSON serialization |
| Screen Composable | `presentation/screens/nearby/NearbyPhotographersScreen.kt` | UI rendering |
| Navigation Route | In `presentation/navigation/Screen.kt` | Type-safe navigation |
| DI Configuration | In `di/AppModule.kt` | Dependency binding |
| Platform Service | `androidMain/...` & `iosMain/...` | Platform-specific code |

---

## 11. Testing Checklist

- [ ] ViewModel state updates correctly
- [ ] Location permission flow works
- [ ] API calls are made with correct parameters
- [ ] DTOs deserialize correctly
- [ ] Repository maps DTOs to domain models
- [ ] Screen displays nearby photographers
- [ ] Navigation works to photographer detail
- [ ] Distance radius slider updates results
- [ ] Error states display correctly
- [ ] Loading states work on all platforms

---

**Ready to implement? Start with Step 1: Create the ViewModel, then follow steps 2-7 in order.**
