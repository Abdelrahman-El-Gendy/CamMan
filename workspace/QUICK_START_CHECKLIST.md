# "Nearby Photographers" Feature - Quick Start Checklist

## 📋 Pre-Implementation Checklist

### Understanding the Codebase
- [x] Review PROJECT_EXPLORATION.md - Architecture overview
- [x] Review CODE_PATTERNS_REFERENCE.md - Implementation templates
- [x] Understand Clean Architecture pattern (Domain → Data → Presentation)
- [x] Understand MVVM with StateFlow + SharedFlow
- [x] Review existing repositories (PhotographerRepositoryImpl is good reference)
- [x] Review existing screens (BrowsePhotographersScreen is good reference)
- [x] Understand Koin DI setup

### Project Structure Knowledge
- [x] Know location of domain models: `composeApp/src/commonMain/kotlin/.../domain/model/`
- [x] Know location of repositories: `composeApp/src/commonMain/kotlin/.../domain/repository/` (interface) and `.../data/repository/` (impl)
- [x] Know location of screens: `composeApp/src/commonMain/kotlin/.../presentation/screens/`
- [x] Know location of DI config: `composeApp/src/commonMain/kotlin/.../di/AppModule.kt`
- [x] Know platform-specific paths: `androidMain/` and `iosMain/`

---

## 🛠️ Step 1: Prepare Domain Layer

### Task 1.1: Create LocationRepository Interface
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/repository/LocationRepository.kt`

**Checklist:**
- [ ] Create interface with methods:
  - [ ] `observeUserLocation(): Flow<Resource<UserLocation?>>`
  - [ ] `getNearbyPhotographers(...): Flow<Resource<List<Photographer>>>`
  - [ ] `requestLocationPermission(): Boolean`
  - [ ] `hasLocationPermission(): Boolean`
  - [ ] `getLastKnownLocation(): UserLocation?`
- [ ] Use `Resource<T>` wrapper for error handling
- [ ] Use `Flow` for reactive updates

**Reference:** `domain/repository/PhotographerRepository.kt`

### Task 1.2: Create UserLocation Data Model
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/model/UserLocation.kt`

**Checklist:**
- [ ] Create data class with:
  - [ ] `latitude: Double`
  - [ ] `longitude: Double`
  - [ ] `accuracy: Float`

---

## 🎯 Step 2: Prepare Data Layer

### Task 2.1: Create Location DTOs
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/dto/LocationDto.kt`

**Checklist:**
- [ ] Create `@Serializable NearbyPhotographersResponse`
- [ ] Create `@Serializable PhotographerLocationDto`
- [ ] Create `@Serializable DistanceDto`
- [ ] Add mapping functions `toPhotographer()`

**Reference:** `data/remote/dto/PhotographerDto.kt`, `data/remote/dto/AlbumDto.kt`

### Task 2.2: Extend CamManApiService
**File to modify:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/api/CamManApiService.kt`

**Checklist:**
- [ ] Add endpoint: `getNearbyPhotographers(lat, lng, radius, limit)`
- [ ] Add endpoint: `getPhotographerDistance(photographerId, lat, lng)`
- [ ] Both use Ktor HttpClient already configured

**Reference:** Lines 21-60 (Album endpoints), 84-113 (Booking endpoints)

### Task 2.3: Create LocationRepositoryImpl
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/repository/LocationRepositoryImpl.kt`

**Checklist:**
- [ ] Implement `LocationRepository` interface
- [ ] Inject `CamManApiService` and `LocationService` (platform-specific)
- [ ] Handle API calls with error wrapping
- [ ] Map DTOs to domain Photographer objects
- [ ] Cache last known location
- [ ] Use `Flow`, `onStart`, `catch` operators

**Reference:** `data/repository/PhotographerRepositoryImpl.kt` (lines 22-174)

### Task 2.4: Create Platform-Specific Location Service

**Android Implementation:**
**File to create:** `composeApp/src/androidMain/kotlin/com/gndy/camman/data/location/LocationService.android.kt`

**Checklist:**
- [ ] Use FusedLocationProviderClient from Google Play Services
- [ ] Implement `observeLocation(): Flow<UserLocation?>`
- [ ] Check permissions before accessing location
- [ ] Return last known location
- [ ] Handle exceptions gracefully

**iOS Implementation:**
**File to create:** `composeApp/src/iosMain/kotlin/com/gndy/camman/data/location/LocationService.ios.kt`

**Checklist:**
- [ ] Use CoreLocation framework
- [ ] Implement same interface as Android
- [ ] Handle iOS permission states

**Add to gradle dependencies:** `"com.google.android.gms:play-services-location"` (Android only)

---

## 🎨 Step 3: Prepare Presentation Layer

### Task 3.1: Create ViewModel
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/nearby/NearbyPhotographersViewModel.kt`

**Checklist:**
- [ ] Create `NearbyPhotographersUiState` data class with fields:
  - [ ] `isLoading: Boolean`
  - [ ] `photographers: List<Photographer>`
  - [ ] `userLocation: UserLocation?`
  - [ ] `selectedDistance: Int` (default 10 km)
  - [ ] `error: String?`
  - [ ] `isLocationEnabled: Boolean`
- [ ] Create `NearbyPhotographersUiEvent` sealed class with events:
  - [ ] `NavigateToPhotographer(photographerId)`
  - [ ] `ShowError(message)`
  - [ ] `RequestLocationPermission`
  - [ ] `LocationPermissionDenied`
- [ ] Create ViewModel with:
  - [ ] `private val _uiState = MutableStateFlow(...)`
  - [ ] `val uiState: StateFlow<...> = _uiState.asStateFlow()`
  - [ ] `private val _uiEvents = MutableSharedFlow<...>()`
  - [ ] `val uiEvents = _uiEvents.asSharedFlow()`
- [ ] Inject `LocationRepository`
- [ ] Initialize location tracking in `init {}`
- [ ] Implement public methods:
  - [ ] `onPhotographerClick(id)`
  - [ ] `onDistanceChanged(distance)`
  - [ ] `requestLocationPermission()`
  - [ ] `onLocationPermissionGranted()`
  - [ ] `onLocationPermissionDenied()`
  - [ ] `refresh()`

**Reference:** `presentation/screens/user/BrowsePhotographersViewModel.kt`

### Task 3.2: Create Screen Composable
**File to create:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/nearby/NearbyPhotographersScreen.kt`

**Checklist:**
- [ ] Create composable function `NearbyPhotographersScreen(...)`
- [ ] Inject ViewModel with `koinViewModel()`
- [ ] Collect UI state with `collectAsState()`
- [ ] Collect UI events in `LaunchedEffect`
- [ ] Create Scaffold with TopAppBar
- [ ] Implement states:
  - [ ] Location disabled → show permission request button
  - [ ] Loading → show CircularProgressIndicator
  - [ ] Empty → show "no photographers found" message
  - [ ] Loaded → show list with distance slider
- [ ] Create `NearbyPhotographerCard` composable showing:
  - [ ] Profile image
  - [ ] Name
  - [ ] Location
  - [ ] Rating and review count
  - [ ] Optional: Distance display
- [ ] Add distance slider (1-50 km)
- [ ] Use theme colors: Gold primary color, Material3 colors
- [ ] Add bottom navigation spacer (80.dp)

**Reference:** `presentation/screens/user/BrowsePhotographersScreen.kt` (lines 60-388)

---

## 📍 Step 4: Add Navigation

### Task 4.1: Add Navigation Route
**File to modify:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/Screen.kt`

**Checklist:**
- [ ] Add route: `data object NearbyPhotographers : Screen()`
- [ ] Must be serializable (all Screen classes are)

**Reference:** Lines 30-50 (existing routes)

### Task 4.2: Add to Navigation Graph
**File to modify:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/NavGraph.kt`

**Checklist:**
- [ ] Add composable in appropriate section (after BrowsePhotographers):
```kotlin
composable<Screen.NearbyPhotographers> {
    NearbyPhotographersScreen(
        onNavigateToPhotographer = { photographerId ->
            navController.navigate(Screen.PhotographerDetail(photographerId))
        }
    )
}
```
- [ ] Import the screen: `import com.gndy.camman.presentation.screens.nearby.NearbyPhotographersScreen`

**Reference:** Lines 131-141 (UserMain example)

### Task 4.3: Add to Bottom Navigation
**File to modify:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/user/BrowsePhotographersScreen.kt` (or UserMainScreen if it has bottom nav)

**Checklist:**
- [ ] Add new tab for "Nearby" in bottom navigation
- [ ] Navigate to `Screen.NearbyPhotographers` on click
- [ ] Use location icon for the tab

---

## 💉 Step 5: Configure Dependency Injection

### Task 5.1: Add to Koin Module
**File to modify:** `composeApp/src/commonMain/kotlin/com/gndy/camman/di/AppModule.kt`

**Checklist:**
- [ ] Add to `sharedModule`:
```kotlin
// Location Repository
single<LocationRepository> { LocationRepositoryImpl(get(), get()) }

// ViewModels
viewModel { NearbyPhotographersViewModel(get()) }
```
- [ ] Place after line 102 (after ReviewRepository)
- [ ] Check that Koin's `get()` will resolve `LocationService` from platform modules

**Reference:** Lines 87-102, 120-142

### Task 5.2: Add Platform-Specific Services
**File to modify:** `composeApp/src/androidMain/kotlin/com/gndy/camman/di/AppModule.android.kt`

**Checklist:**
- [ ] Add to androidMain module:
```kotlin
single { AndroidLocationService(get()) }  // get() = Context
```

**File to modify:** `composeApp/src/iosMain/kotlin/com/gndy/camman/di/AppModule.ios.kt`

**Checklist:**
- [ ] Add to iosMain module:
```kotlin
single { IosLocationService() }
```

---

## 🔐 Step 6: Add Permissions

### Task 6.1: Update Android Manifest
**File to modify:** `composeApp/src/androidMain/AndroidManifest.xml`

**Checklist:**
- [ ] Add before `<application>` tag:
```xml
<!-- Location Permissions -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```
- [ ] Optional (for background location):
```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

**Reference:** Lines 1-4 (INTERNET permission already there)

### Task 6.2: Handle Runtime Permissions (Android)
**File to create:** `composeApp/src/androidMain/kotlin/com/gndy/camman/presentation/screens/nearby/NearbyPhotographersScreen.android.kt`

**Checklist:**
- [ ] Create permission request launcher using Accompanist/modern Permission API
- [ ] Or handle in ViewModel with platform-specific callback
- [ ] Request permissions when "Enable Location" button is clicked
- [ ] Call `viewModel.onLocationPermissionGranted()` on success
- [ ] Call `viewModel.onLocationPermissionDenied()` on failure

---

## 🧪 Step 7: Test Implementation

### Unit Testing Checklist
- [ ] Mock `LocationRepository` in ViewModel tests
- [ ] Test state transitions:
  - [ ] Loading → Success
  - [ ] Loading → Error
  - [ ] Location disabled → enabled
- [ ] Test distance slider updates
- [ ] Test photographer click navigation

### Integration Testing Checklist
- [ ] Test on Android emulator
  - [ ] Mock location in Android Studio emulator settings
  - [ ] Grant location permission
  - [ ] Verify location is fetched
  - [ ] Verify photographers load
  - [ ] Verify slider changes results
- [ ] Test on iOS simulator
  - [ ] Grant location permission
  - [ ] Verify same behavior as Android

### Manual Testing Checklist
- [ ] Launch app
- [ ] Navigate to Nearby Photographers tab
- [ ] [ ] Screen shows permission request initially
- [ ] [ ] Click "Enable Location" button
- [ ] [ ] Grant location permission
- [ ] [ ] App fetches current location
- [ ] [ ] List of nearby photographers loads
- [ ] [ ] Distance slider shows current value
- [ ] [ ] Moving slider updates list
- [ ] [ ] Tapping photographer navigates to detail
- [ ] [ ] Error state displays when API fails
- [ ] [ ] Empty state displays when no photographers found
- [ ] [ ] Loading spinner shows during API call
- [ ] [ ] App works in both light and dark themes

---

## 📚 File Checklist

### Files to Create (9 files)
- [ ] `domain/repository/LocationRepository.kt`
- [ ] `domain/model/UserLocation.kt`
- [ ] `data/remote/dto/LocationDto.kt`
- [ ] `data/repository/LocationRepositoryImpl.kt`
- [ ] `androidMain/data/location/LocationService.android.kt`
- [ ] `iosMain/data/location/LocationService.ios.kt`
- [ ] `presentation/screens/nearby/NearbyPhotographersViewModel.kt`
- [ ] `presentation/screens/nearby/NearbyPhotographersScreen.kt`
- [ ] `androidMain/presentation/screens/nearby/NearbyPhotographersScreen.android.kt` (permissions)

### Files to Modify (5 files)
- [ ] `data/remote/api/CamManApiService.kt` (add 2 endpoints)
- [ ] `presentation/navigation/Screen.kt` (add 1 route)
- [ ] `presentation/navigation/NavGraph.kt` (add 1 composable)
- [ ] `di/AppModule.kt` (add 2 bindings)
- [ ] `androidMain/AndroidManifest.xml` (add 2 permissions)
- [ ] `androidMain/di/AppModule.android.kt` (add 1 binding)
- [ ] `iosMain/di/AppModule.ios.kt` (add 1 binding)
- [ ] Bottom nav integration (1 file - depends on your structure)

---

## 🚀 Execution Order

1. **Day 1: Domain Layer**
   - Create LocationRepository interface
   - Create UserLocation model
   - Time: ~15 minutes

2. **Day 1: Data Layer - API**
   - Create Location DTOs
   - Extend CamManApiService
   - Time: ~20 minutes

3. **Day 1: Data Layer - Implementation**
   - Create LocationRepositoryImpl
   - Time: ~30 minutes

4. **Day 2: Platform Services**
   - Create Android LocationService
   - Create iOS LocationService
   - Time: ~45 minutes

5. **Day 2: Presentation**
   - Create ViewModel
   - Create Screen
   - Time: ~60 minutes

6. **Day 2: Integration**
   - Add navigation
   - Add DI bindings
   - Time: ~20 minutes

7. **Day 3: Permissions & Testing**
   - Add Android permissions
   - Add permission handling
   - Test on both platforms
   - Time: ~60 minutes

**Total Estimated Time:** 4-5 hours

---

## 🐛 Common Issues & Solutions

| Issue | Solution | Reference |
|-------|----------|-----------|
| `LocationRepository` not found | Make sure interface is in `domain/repository/` | Section 2.1 |
| Koin can't resolve `LocationService` | Add platform-specific binding | Section 5.2 |
| Permission crashes on Android | Wrap calls in permission check | Section 6.2 |
| DTOs don't deserialize | Add `@Serializable` annotation | Section 2.1 |
| ViewModel gets null repository | Check Koin configuration in AppModule | Section 5.1 |
| Navigation fails | Verify Screen route is added to NavGraph | Section 4.2 |
| Screen not reachable | Add to bottom navigation or other nav | Section 4.3 |
| Location service returns null | Check permissions are granted | Section 6.1 |

---

## 📖 Reference Materials

**Key Files to Study (in order):**
1. `domain/repository/PhotographerRepository.kt` - Interface pattern
2. `data/repository/PhotographerRepositoryImpl.kt` - Implementation pattern
3. `presentation/screens/user/BrowsePhotographersViewModel.kt` - ViewModel pattern
4. `presentation/screens/user/BrowsePhotographersScreen.kt` - Screen pattern
5. `di/AppModule.kt` - DI configuration

**Documentation Files:**
1. `PROJECT_EXPLORATION.md` - Full project overview
2. `CODE_PATTERNS_REFERENCE.md` - Complete code examples
3. `composeApp/ARCHITECTURE.md` - Original architecture docs

---

## ✅ Definition of Done

The feature is complete when:
- [ ] All files created according to checklist
- [ ] All files modified according to checklist
- [ ] Code compiles without errors
- [ ] Android tests pass
- [ ] iOS tests pass
- [ ] Manual testing checklist complete
- [ ] No lint errors
- [ ] Code follows project patterns
- [ ] Documentation updated if needed

---

**Start with Section "Step 1: Prepare Domain Layer" and work through in order!**
