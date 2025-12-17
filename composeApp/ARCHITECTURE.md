# CamMan - Photographer Portfolio App Architecture

## 📁 Complete Folder Structure

```
composeApp/src/
├── commonMain/kotlin/com/gndy/camman/
│   ├── App.kt                              # Main App Composable
│   │
│   ├── domain/                             # DOMAIN LAYER
│   │   ├── model/                          # Entities
│   │   │   ├── PhotographerProfile.kt
│   │   │   ├── Album.kt
│   │   │   ├── Photo.kt
│   │   │   ├── Package.kt
│   │   │   ├── Booking.kt
│   │   │   └── PaymentMethod.kt
│   │   │
│   │   ├── repository/                     # Repository Interfaces
│   │   │   ├── AlbumRepository.kt
│   │   │   ├── PhotoRepository.kt
│   │   │   ├── PackageRepository.kt
│   │   │   ├── BookingRepository.kt
│   │   │   ├── PaymentRepository.kt
│   │   │   └── ProfileRepository.kt
│   │   │
│   │   ├── usecase/                        # Use Cases
│   │   │   ├── album/
│   │   │   │   ├── GetAlbumsUseCase.kt
│   │   │   │   └── GetAlbumPhotosUseCase.kt
│   │   │   ├── packages/
│   │   │   │   └── GetPackagesUseCase.kt
│   │   │   ├── booking/
│   │   │   │   └── CreateBookingUseCase.kt
│   │   │   ├── payment/
│   │   │   │   └── SubmitPaymentUseCase.kt
│   │   │   └── profile/
│   │   │       └── GetPhotographerContactInfoUseCase.kt
│   │   │
│   │   └── util/
│   │       └── Resource.kt                 # Result wrapper
│   │
│   ├── data/                               # DATA LAYER
│   │   ├── local/                          # Local Data Source
│   │   │   ├── CamManDatabase.kt           # Room Database
│   │   │   ├── DatabaseFactory.kt          # Platform DB factory
│   │   │   ├── dao/                        # Data Access Objects
│   │   │   │   ├── AlbumDao.kt
│   │   │   │   ├── PhotoDao.kt
│   │   │   │   ├── PackageDao.kt
│   │   │   │   ├── BookingDao.kt
│   │   │   │   ├── PaymentMethodDao.kt
│   │   │   │   └── PhotographerProfileDao.kt
│   │   │   └── entity/                     # Room Entities
│   │   │       ├── AlbumEntity.kt
│   │   │       ├── PhotoEntity.kt
│   │   │       ├── PackageEntity.kt
│   │   │       ├── BookingEntity.kt
│   │   │       ├── PaymentMethodEntity.kt
│   │   │       └── PhotographerProfileEntity.kt
│   │   │
│   │   ├── remote/                         # Remote Data Source
│   │   │   ├── HttpClientFactory.kt        # Ktor HTTP Client
│   │   │   ├── api/
│   │   │   │   └── CamManApiService.kt     # API endpoints
│   │   │   └── dto/                        # Data Transfer Objects
│   │   │       ├── AlbumDto.kt
│   │   │       ├── PhotoDto.kt
│   │   │       ├── PackageDto.kt
│   │   │       ├── BookingDto.kt
│   │   │       ├── PaymentDto.kt
│   │   │       └── ProfileDto.kt
│   │   │
│   │   └── repository/                     # Repository Implementations
│   │       ├── AlbumRepositoryImpl.kt
│   │       ├── PhotoRepositoryImpl.kt
│   │       ├── PackageRepositoryImpl.kt
│   │       ├── BookingRepositoryImpl.kt
│   │       ├── PaymentRepositoryImpl.kt
│   │       └── ProfileRepositoryImpl.kt
│   │
│   ├── presentation/                       # PRESENTATION LAYER
│   │   ├── theme/                          # Material 3 Theme
│   │   │   ├── Color.kt                    # Photography palette
│   │   │   ├── Type.kt                     # Typography
│   │   │   └── Theme.kt                    # Theme config
│   │   │
│   │   ├── navigation/
│   │   │   ├── Screen.kt                   # Navigation destinations
│   │   │   └── NavGraph.kt                 # Navigation graph
│   │   │
│   │   └── screens/
│   │       ├── splash/
│   │       │   └── SplashScreen.kt
│   │       │
│   │       ├── home/
│   │       │   ├── HomeScreen.kt
│   │       │   └── HomeViewModel.kt
│   │       │
│   │       ├── portfolio/
│   │       │   ├── PortfolioAlbumsScreen.kt
│   │       │   ├── AlbumDetailScreen.kt
│   │       │   ├── PhotoPreviewScreen.kt
│   │       │   └── PortfolioViewModel.kt
│   │       │
│   │       ├── packages/
│   │       │   ├── PackagesScreen.kt
│   │       │   ├── PackageDetailScreen.kt
│   │       │   └── PackagesViewModel.kt
│   │       │
│   │       ├── booking/
│   │       │   ├── BookingScreen.kt
│   │       │   └── BookingViewModel.kt
│   │       │
│   │       └── contact/
│   │           ├── ContactScreen.kt
│   │           └── ContactViewModel.kt
│   │
│   └── di/                                 # Dependency Injection
│       └── AppModule.kt                    # Koin modules
│
├── androidMain/kotlin/com/gndy/camman/
│   ├── MainActivity.kt
│   ├── CamManApplication.kt                # Application class
│   ├── data/local/DatabaseFactory.android.kt
│   ├── data/remote/HttpClientFactory.android.kt
│   └── di/AppModule.android.kt
│
└── iosMain/kotlin/com/gndy/camman/
    ├── data/local/DatabaseFactory.ios.kt
    ├── data/remote/HttpClientFactory.ios.kt
    └── di/AppModule.ios.kt
```

---

## 🏗️ Architecture Overview

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │  Screens (Composables) ◄─── ViewModels          │    │
│  │  • SplashScreen         • HomeViewModel         │    │
│  │  • HomeScreen           • PortfolioAlbumsVM     │    │
│  │  • PortfolioAlbumsScreen• AlbumDetailVM         │    │
│  │  • PhotoPreviewScreen   • PhotoPreviewVM        │    │
│  │  • PackagesScreen       • PackagesVM            │    │
│  │  • PackageDetailScreen  • PackageDetailVM       │    │
│  │  • BookingScreen        • BookingVM             │    │
│  │  • ContactScreen        • ContactVM             │    │
│  └─────────────────────────────────────────────────┘    │
│                          │                               │
│                    UIState + UIEvents                    │
│                          ▼                               │
├─────────────────────────────────────────────────────────┤
│                      DOMAIN                              │
│  ┌─────────────────────────────────────────────────┐    │
│  │  Use Cases                    Entities          │    │
│  │  • GetAlbumsUseCase           • Album           │    │
│  │  • GetAlbumPhotosUseCase      • Photo           │    │
│  │  • GetPackagesUseCase         • Package         │    │
│  │  • CreateBookingUseCase       • Booking         │    │
│  │  • SubmitPaymentUseCase       • PaymentMethod   │    │
│  │  • GetPhotographerContactInfo • Profile         │    │
│  └─────────────────────────────────────────────────┘    │
│                          │                               │
│               Repository Interfaces                      │
│                          ▼                               │
├─────────────────────────────────────────────────────────┤
│                       DATA                               │
│  ┌─────────────────────────────────────────────────┐    │
│  │  Repository Implementations                      │    │
│  │  (Offline-first with caching strategy)          │    │
│  └─────────────────────────────────────────────────┘    │
│           │                              │               │
│           ▼                              ▼               │
│  ┌────────────────────┐    ┌────────────────────┐       │
│  │   Local Source     │    │   Remote Source    │       │
│  │   (Room Database)  │    │   (Ktor/Retrofit)  │       │
│  │   • DAOs           │    │   • API Service    │       │
│  │   • Entities       │    │   • DTOs           │       │
│  └────────────────────┘    └────────────────────┘       │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Data Flow Diagram

### Flow: User Views Album Photos

```
User Action                         Screen                    ViewModel
    │                                  │                          │
    │ ──── Tap on Album ───────────────►                          │
    │                                  │ ─── loadAlbum(id) ───────►
    │                                  │                          │
    │                                  │                   ┌──────┴──────┐
    │                                  │                   │  UseCase    │
    │                                  │                   │  getPhotos  │
    │                                  │                   └──────┬──────┘
    │                                  │                          │
    │                               Repository                    │
    │                                  │◄──── Flow<Resource> ─────┤
    │                                  │                          │
    │                          ┌───────┴───────┐                  │
    │                          ▼               ▼                  │
    │                    ┌──────────┐   ┌──────────┐              │
    │                    │  Room    │   │  Ktor    │              │
    │                    │  (Cache) │   │  (API)   │              │
    │                    └──────────┘   └──────────┘              │
    │                                                             │
    │                                  │                          │
    │◄───── UIState Update ────────────│◄── emit(photos) ─────────┤
    │      (photos list)               │                          │
```

### Flow: Booking Creation

```
┌─────────────────────────────────────────────────────────────────┐
│                     BOOKING FLOW (5 Steps)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Step 1: SELECT PACKAGE                                          │
│  ┌────────────────────────┐                                      │
│  │ • View package details │                                      │
│  │ • Select add-ons       │                                      │
│  └────────────┬───────────┘                                      │
│               ▼                                                  │
│  Step 2: CHOOSE DATE & TIME                                      │
│  ┌────────────────────────┐                                      │
│  │ • View availability    │ ◄─── getAvailableDates()             │
│  │ • Select date/time     │                                      │
│  │ • Enter location       │                                      │
│  └────────────┬───────────┘                                      │
│               ▼                                                  │
│  Step 3: CLIENT DETAILS                                          │
│  ┌────────────────────────┐                                      │
│  │ • Name, Email, Phone   │                                      │
│  │ • Address (optional)   │                                      │
│  │ • Special requests     │                                      │
│  └────────────┬───────────┘                                      │
│               ▼                                                  │
│  Step 4: PAYMENT METHOD                                          │
│  ┌────────────────────────┐                                      │
│  │ • Select payment type  │ ◄─── getPaymentMethods()             │
│  │ • Card/PayPal/Cash     │                                      │
│  └────────────┬───────────┘                                      │
│               ▼                                                  │
│  Step 5: CONFIRMATION                                            │
│  ┌────────────────────────┐                                      │
│  │ • Review summary       │                                      │
│  │ • Add notes            │                                      │
│  │ • Confirm booking      │ ────► createBooking() ───►           │
│  └────────────┬───────────┘                    │                 │
│               ▼                                ▼                 │
│  ┌────────────────────────┐       ┌────────────────────┐         │
│  │  BookingSummary        │ ◄─────│  BookingRepository │         │
│  │  • Confirmation #      │       │  • API call        │         │
│  │  • Delivery date       │       │  • Cache locally   │         │
│  └────────────────────────┘       └────────────────────┘         │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔌 Interface Contracts

### Repository Interfaces

```kotlin
interface AlbumRepository {
    fun getAlbums(): Flow<Resource<List<Album>>>
    fun getAlbumById(albumId: String): Flow<Resource<Album>>
    fun getAlbumsByCategory(category: AlbumCategory): Flow<Resource<List<Album>>>
    fun getFeaturedAlbums(): Flow<Resource<List<Album>>>
    suspend fun refreshAlbums(): Resource<Unit>
}

interface PhotoRepository {
    fun getPhotosByAlbum(albumId: String): Flow<Resource<List<Photo>>>
    fun getPhotoById(photoId: String): Flow<Resource<Photo>>
    fun getFeaturedPhotos(): Flow<Resource<List<Photo>>>
    fun searchPhotos(query: String): Flow<Resource<List<Photo>>>
    suspend fun refreshPhotos(albumId: String): Resource<Unit>
}

interface PackageRepository {
    fun getPackages(): Flow<Resource<List<PhotographyPackage>>>
    fun getPackageById(packageId: String): Flow<Resource<PhotographyPackage>>
    fun getPackagesByCategory(category: PackageCategory): Flow<Resource<List<PhotographyPackage>>>
    fun getPopularPackages(): Flow<Resource<List<PhotographyPackage>>>
    fun getPackageAddOns(packageId: String): Flow<Resource<List<PackageAddOn>>>
    suspend fun refreshPackages(): Resource<Unit>
}

interface BookingRepository {
    suspend fun createBooking(...): Resource<BookingSummary>
    fun getBookingById(bookingId: String): Flow<Resource<Booking>>
    fun getUserBookings(): Flow<Resource<List<Booking>>>
    fun getAvailableDates(packageId: String, month: Int, year: Int): Flow<Resource<List<AvailableDate>>>
    suspend fun cancelBooking(bookingId: String): Resource<Unit>
    suspend fun rescheduleBooking(...): Resource<Booking>
}

interface PaymentRepository {
    suspend fun submitPayment(...): Resource<PaymentResult>
    fun getPaymentMethods(): Flow<Resource<List<PaymentMethod>>>
    suspend fun addPaymentMethod(paymentMethod: PaymentMethod): Resource<PaymentMethod>
    suspend fun removePaymentMethod(paymentMethodId: String): Resource<Unit>
    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Resource<Unit>
}

interface ProfileRepository {
    fun getPhotographerProfile(): Flow<Resource<PhotographerProfile>>
    suspend fun refreshProfile(): Resource<Unit>
    suspend fun sendContactMessage(...): Resource<Unit>
}
```

### ViewModel Pattern

```kotlin
// UI State - Immutable data class representing screen state
data class HomeUiState(
    val isLoading: Boolean = true,
    val profile: PhotographerProfile? = null,
    val featuredAlbums: List<Album> = emptyList(),
    val heroPhotos: List<Photo> = emptyList(),
    val error: String? = null
)

// UI Events - Sealed class for one-time events
sealed class HomeUiEvent {
    data class NavigateToAlbum(val albumId: String) : HomeUiEvent()
    data object NavigateToPortfolio : HomeUiEvent()
    data class ShowError(val message: String) : HomeUiEvent()
}

// ViewModel structure
class HomeViewModel(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    // ... other use cases
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _uiEvents = MutableSharedFlow<HomeUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()
    
    // User actions
    fun onAlbumClick(albumId: String) { ... }
    fun refresh() { ... }
}
```

---

## 🎨 Theme Configuration

### Color Palette (Photography Inspired)

```kotlin
// Primary - Gold accents
val Gold = Color(0xFFD4AF37)
val GoldLight = Color(0xFFE6C866)
val GoldDark = Color(0xFFAA8B2C)

// Neutrals - Black & White
val PureBlack = Color(0xFF000000)
val CharcoalBlack = Color(0xFF1A1A1A)
val DarkGray = Color(0xFF2D2D2D)
val OffWhite = Color(0xFFF5F5F5)
val PureWhite = Color(0xFFFFFFFF)

// Dark Theme
val DarkBackground = Color(0xFF0D0D0D)
val DarkSurface = Color(0xFF1A1A1A)

// Light Theme
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)
```

### Typography (Premium Style)

- **Display**: Bold, large headings for hero sections
- **Headline**: Semi-bold section titles
- **Title**: Medium weight for cards
- **Body**: Normal weight for descriptions
- **Label**: Medium weight for buttons

---

## 🔄 Tech Stack Summary

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose Multiplatform |
| Navigation | Navigation Compose (Type-safe) |
| State Management | StateFlow + SharedFlow |
| DI | Koin |
| Network | Ktor Client |
| Local Storage | Room Database |
| Image Loading | Coil 3 |
| Serialization | Kotlinx Serialization |
| Date/Time | Kotlinx DateTime |
| Architecture | MVVM + Clean Architecture |

---

## 🚀 Getting Started

1. **Sync Gradle** to download all dependencies
2. **Build the project** - Room will generate DAOs
3. **Run on Android** or **iOS**

### API Configuration

Update the base URL in `CamManApiService.kt`:

```kotlin
private const val BASE_URL = "https://your-api.com/v1"
```

---

## 📱 Screens Overview

| Screen | Description |
|--------|-------------|
| Splash | Animated logo, auto-navigate to Home |
| Home | Hero carousel, featured albums, quick actions |
| Portfolio Albums | Grid of albums with category filter |
| Album Detail | Photo grid within an album |
| Photo Preview | Full-screen pager with photo info |
| Packages | List of photography packages |
| Package Detail | Full package info with features |
| Booking | 5-step wizard for booking |
| Contact | Profile info + contact form |

---

This architecture provides a solid, scalable foundation for the Photographer Portfolio App with
offline-first capabilities and clean separation of concerns.
