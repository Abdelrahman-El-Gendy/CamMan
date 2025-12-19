# CamMan Kotlin Multiplatform Project - Comprehensive Exploration

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Project Structure](#project-structure)
3. [Architecture Patterns](#architecture-patterns)
4. [Navigation Setup](#navigation-setup)
5. [UI Framework & Design Patterns](#ui-framework--design-patterns)
6. [Network Layer](#network-layer)
7. [Dependency Injection](#dependency-injection)
8. [Domain Models & Data Layer](#domain-models--data-layer)
9. [Permissions Handling](#permissions-handling)
10. [Map Integrations](#map-integrations)
11. [Existing Features Reference](#existing-features-reference)

---

## 1. Project Overview

**Project Name:** CamMan (Photographer Portfolio App)
**Type:** Kotlin Multiplatform (KMP) with Compose Multiplatform
**Target Platforms:** Android, iOS
**Architecture Pattern:** Clean Architecture + MVVM
**State Management:** StateFlow + SharedFlow

### Key Information
- **Gradle Version:** 8.5.2
- **Kotlin Version:** 2.1.0
- **Compose Multiplatform:** 1.7.3
- **Android Min SDK:** 24, Target SDK: 35
- **Database:** Room 2.7.0-alpha12 with SQLite bundled
- **HTTP Client:** Ktor 3.0.3

---

## 2. Project Structure

### Directory Layout
```
CamMan/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/com/gndy/camman/         # Shared Kotlin code
│   │   │   ├── domain/                                # Domain Layer
│   │   │   │   ├── model/                             # Data Models
│   │   │   │   ├── repository/                        # Repository Interfaces
│   │   │   │   ├── usecase/                           # Business Logic
│   │   │   │   └── util/                              # Utilities (Resource<T>)
│   │   │   ├── data/                                  # Data Layer
│   │   │   │   ├── local/                             # Room Database
│   │   │   │   │   ├── dao/                           # Data Access Objects
│   │   │   │   │   └── entity/                        # Room Entities
│   │   │   │   ├── remote/                            # Ktor HTTP Client
│   │   │   │   │   ├── api/                           # API Service
│   │   │   │   │   └── dto/                           # Data Transfer Objects
│   │   │   │   └── repository/                        # Repository Implementations
│   │   │   ├── presentation/                          # Presentation Layer
│   │   │   │   ├── theme/                             # Material 3 Theme
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── navigation/                        # Navigation Setup
│   │   │   │   │   ├── Screen.kt                      # Navigation Routes
│   │   │   │   │   ├── NavGraph.kt                    # Monolithic Nav Graph
│   │   │   │   │   ├── ModularNavGraph.kt             # Modular Nav Graph
│   │   │   │   │   ├── builders/                      # Entry Builders
│   │   │   │   │   └── keys/                          # Navigation Keys
│   │   │   │   ├── screens/                           # UI Screens
│   │   │   │   │   ├── splash/
│   │   │   │   │   ├── auth/
│   │   │   │   │   ├── onboarding/
│   │   │   │   │   ├── home/
│   │   │   │   │   ├── user/                          # Client browsing
│   │   │   │   │   ├── photographer/                  # Photographer side
│   │   │   │   │   ├── portfolio/
│   │   │   │   │   ├── packages/
│   │   │   │   │   ├── booking/
│   │   │   │   │   ├── review/
│   │   │   │   │   └── contact/
│   │   │   │   └── components/                        # Reusable Components
│   │   │   ├── di/                                    # Dependency Injection
│   │   │   │   └── AppModule.kt                       # Koin Modules
│   │   │   └── App.kt                                 # Main App Composable
│   │   ├── androidMain/                               # Android-specific code
│   │   │   ├── kotlin/com/gndy/camman/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── CamManApplication.kt               # App initialization
│   │   │   │   ├── data/local/DatabaseFactory.android.kt
│   │   │   │   └── data/remote/HttpClientFactory.android.kt
│   │   │   └── res/                                   # Android resources
│   │   └── iosMain/                                   # iOS-specific code
│   │       └── kotlin/com/gndy/camman/
│   │           ├── data/local/DatabaseFactory.ios.kt
│   │           └── data/remote/HttpClientFactory.ios.kt
│   ├── build.gradle.kts                               # Module build config
│   ├── ARCHITECTURE.md                                # Architecture docs
│   └── FIREBASE_SETUP.md                              # Firebase setup guide
├── gradle/
│   └── wrapper/
├── gradle.properties
├── gradle/libs.versions.toml                          # Dependency versions
├── settings.gradle.kts                                # Root settings
├── build.gradle.kts                                   # Root build config
├── local.properties                                   # Local paths
└── README.md
```

### Key Directory Paths for Reference

| Component | Path |
|-----------|------|
| Domain Models | `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/model/` |
| Repositories (Interface) | `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/repository/` |
| Repositories (Implementation) | `composeApp/src/commonMain/kotlin/com/gndy/camman/data/repository/` |
| Use Cases | `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/usecase/` |
| Database DAOs | `composeApp/src/commonMain/kotlin/com/gndy/camman/data/local/dao/` |
| Room Entities | `composeApp/src/commonMain/kotlin/com/gndy/camman/data/local/entity/` |
| API DTOs | `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/dto/` |
| API Service | `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/api/CamManApiService.kt` |
| Screens | `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/` |
| Theme | `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/theme/` |
| Navigation | `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/` |
| DI Configuration | `composeApp/src/commonMain/kotlin/com/gndy/camman/di/AppModule.kt` |

---

## 3. Architecture Patterns

### 3.1 Clean Architecture Layers

```
┌─────────────────────────────────────────────────┐
│           PRESENTATION (UI)                      │
│  ┌──────────────────────────────────────────┐   │
│  │  Screens (Composables) ◄── ViewModels    │   │
│  │  State Management: StateFlow + SharedFlow│   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                       │
                  UIState + UIEvents
                       ▼
┌─────────────────────────────────────────────────┐
│             DOMAIN (Business Logic)              │
│  ┌──────────────────────────────────────────┐   │
│  │  Use Cases            Entities           │   │
│  │  Repository Interfaces                  │   │
│  │  Resource<T> wrapper for error handling │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
                       │
              Repository Interfaces
                       ▼
┌─────────────────────────────────────────────────┐
│              DATA (Persistence)                  │
│  ┌──────────────────────────────────────────┐   │
│  │  Repository Implementations (Offline-first) │
│  │  ┌────────────────┐   ┌────────────────┐ │   │
│  │  │  Local Source  │   │  Remote Source │ │   │
│  │  │  (Room DB)     │   │  (Ktor Client) │ │   │
│  │  │  • DAOs        │   │  • API Service │ │   │
│  │  │  • Entities    │   │  • DTOs        │ │   │
│  │  └────────────────┘   └────────────────┘ │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### 3.2 MVVM Pattern Details

#### ViewModel Structure
- **State Management**: Uses `MutableStateFlow` for UI state
- **Event Handling**: Uses `MutableSharedFlow` for one-time events
- **Scope**: `viewModelScope` for lifecycle-aware coroutines
- **Pattern**: StateFlow + SharedFlow for unidirectional data flow

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/user/BrowsePhotographersViewModel.kt`

```kotlin
// UI State - Immutable data class
data class BrowsePhotographersUiState(
    val isLoading: Boolean = true,
    val photographers: List<Photographer> = emptyList(),
    val searchQuery: String = "",
    val selectedSpecialty: String? = null,
    val specialties: List<String> = listOf(...),
    val error: String? = null
)

// UI Events - One-time events
sealed class BrowsePhotographersUiEvent {
    data class NavigateToPhotographer(val photographerId: String) : BrowsePhotographersUiEvent()
    data class ShowError(val message: String) : BrowsePhotographersUiEvent()
}

// ViewModel
class BrowsePhotographersViewModel(
    private val photographerRepository: PhotographerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BrowsePhotographersUiState())
    val uiState: StateFlow<BrowsePhotographersUiState> = _uiState.asStateFlow()
    
    private val _uiEvents = MutableSharedFlow<BrowsePhotographersUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()
    
    // User action handlers
    fun onPhotographerClick(photographerId: String) { ... }
    fun onSearchQueryChanged(query: String) { ... }
}
```

### 3.3 Repository Pattern with Resource Wrapper

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/util/Resource.kt`

```kotlin
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    
    // Helper functions for handling results
    fun <R> map(transform: (T) -> R): Resource<R>
    fun getOrNull(): T?
    inline fun onSuccess(action: (T) -> Unit): Resource<T>
    inline fun onError(action: (String) -> Unit): Resource<T>
    inline fun onLoading(action: () -> Unit): Resource<T>
}
```

---

## 4. Navigation Setup

### 4.1 Navigation System Overview

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/Screen.kt`

The app uses **Type-Safe Navigation** with sealed classes:

```kotlin
@Serializable
sealed class Screen {
    // Onboarding & Auth
    data object Splash : Screen()
    data object Onboarding : Screen()
    data object UserTypeSelection : Screen()
    data object SignIn : Screen()
    data object SignUp : Screen()
    data object ForgotPassword : Screen()
    
    // User (Client) Navigation
    data object UserMain : Screen()
    data object BrowsePhotographers : Screen()
    data object UserBookings : Screen()
    data object Favorites : Screen()
    data object UserProfile : Screen()
    
    // Photographer Navigation
    data object PhotographerMain : Screen()
    data object PhotographerHome : Screen()
    data object PhotographerPortfolio : Screen()
    data object PhotographerBookings : Screen()
    data object PhotographerProfile : Screen()
    
    // Shared Screens
    data class PhotographerDetail(val photographerId: String) : Screen()
    data class AlbumDetail(val albumId: String) : Screen()
    data class PhotoPreview(val photoId: String, val albumId: String) : Screen()
    data class Booking(val packageId: String, val photographerId: String? = null) : Screen()
    data class SubmitReview(...) : Screen()
    // ... more screens
}
```

### 4.2 Navigation Approaches

#### Approach 1: Monolithic NavGraph (Current - `USE_MODULAR_NAVIGATION = false`)
**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/NavGraph.kt`
- Single `NavHost` with all routes defined
- Good for simple navigation flows
- Easier debugging and understanding
- Lines: ~374

#### Approach 2: Modular NavGraph (Alternative - `USE_MODULAR_NAVIGATION = true`)
**Files:**
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/ModularNavGraph.kt`
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/builders/NavEntryBuilder.kt`
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/navigation/builders/*.kt` (6 builders for different feature areas)

Entry Builders:
- `AuthEntryBuilder.kt` - Auth flow
- `OnboardingEntryBuilder.kt` - Onboarding flow
- `UserEntryBuilder.kt` - Client user flow
- `PhotographerEntryBuilder.kt` - Photographer flow
- `PortfolioEntryBuilder.kt` - Portfolio screens
- `BookingEntryBuilder.kt` - Booking flow

### 4.3 App Entry Point
**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/App.kt`

```kotlin
@Composable
fun App() {
    CamManTheme {
        val navController = rememberNavController()
        val hasSeenOnboarding by AppPreferencesHolder.instance.hasSeenOnboarding.collectAsState()

        if (USE_MODULAR_NAVIGATION) {
            ModularCamManNavGraph(navController, hasSeenOnboarding, ...)
        } else {
            CamManNavGraph(navController, hasSeenOnboarding, ...)
        }
    }
}
```

### 4.4 Navigation Flow Examples

**User Flow:**
```
Splash → Onboarding → UserTypeSelection → UserMain (Bottom Nav)
                                       └→ PhotographerMain (Bottom Nav)
```

**Booking Flow:**
```
BrowsePhotographers → PhotographerDetail → Packages → PackageDetail → Booking 
→ BookingConfirmation → UserMain
```

**Photographer Flow:**
```
PhotographerMain (Dashboard) → EditProfile → PhotographerMain
```

---

## 5. UI Framework & Design Patterns

### 5.1 Compose Multiplatform Setup
- **Framework:** Jetpack Compose (Compose Multiplatform 1.7.3)
- **Material Design:** Material 3
- **Image Loading:** Coil 3
- **Image Picker:** Peekaboo

### 5.2 Theme Configuration

**Files:**
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/theme/Color.kt`
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/theme/Type.kt`
- `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/theme/Theme.kt`

#### Color Palette (Photography-Inspired)

```kotlin
// Primary Colors
val Gold = Color(0xFFD4AF37)              // Main accent
val GoldLight = Color(0xFFE6C866)
val GoldDark = Color(0xFFAA8B2C)

// Neutrals
val PureBlack = Color(0xFF000000)
val CharcoalBlack = Color(0xFF1A1A1A)
val DarkGray = Color(0xFF2D2D2D)
val MediumGray = Color(0xFF4D4D4D)
val LightGray = Color(0xFFB0B0B0)
val OffWhite = Color(0xFFF5F5F5)
val PureWhite = Color(0xFFFFFFFF)

// Dark Theme
val DarkBackground = Color(0xFF0D0D0D)
val DarkSurface = Color(0xFF1A1A1A)
val DarkSurfaceVariant = Color(0xFF2A2A2A)

// Light Theme
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F0F0)

// Status Colors
val Success = Color(0xFF4CAF50)
val Error = Color(0xFFE53935)
val Warning = Color(0xFFFFA726)
val Info = Color(0xFF42A5F5)
```

#### Theme Implementation

```kotlin
@Composable
fun CamManTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CamManTypography,
        content = content
    )
}
```

### 5.3 Typography

Material 3 typography is used with:
- **Display**: Bold, large headings for hero sections
- **Headline**: Semi-bold section titles
- **Title**: Medium weight for cards
- **Body**: Normal weight for descriptions
- **Label**: Medium weight for buttons

### 5.4 Component Examples

**Photographer Card Component**
- Location: `composeApp/src/commonMain/kotlin/com/gndy/camman/presentation/screens/user/BrowsePhotographersScreen.kt`
- Features: Image loading with Coil, rating display, specialty badges, location info

---

## 6. Network Layer

### 6.1 Ktor HTTP Client Setup

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/HttpClientFactory.kt`

```kotlin
fun createHttpClient(): HttpClient {
    return createPlatformHttpClient().config {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
                encodeDefaults = true
            })
        }
        
        install(Logging) {
            level = LogLevel.BODY
        }
        
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}

expect fun createPlatformHttpClient(): HttpClient
```

**Platform-Specific Implementations:**
- **Android:** `composeApp/src/androidMain/kotlin/com/gndy/camman/data/remote/HttpClientFactory.android.kt` (OkHttp)
- **iOS:** `composeApp/src/iosMain/kotlin/com/gndy/camman/data/remote/HttpClientFactory.ios.kt` (Darwin)

### 6.2 API Service

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/api/CamManApiService.kt`

**Base URL:** `https://api.camman.com/v1` (configurable)

#### API Endpoints

```kotlin
// Albums
suspend fun getAlbums(): AlbumsResponse
suspend fun getAlbumById(albumId: String): AlbumDto
suspend fun getFeaturedAlbums(): AlbumsResponse

// Photos
suspend fun getPhotosByAlbum(albumId: String): PhotosResponse
suspend fun getPhotoById(photoId: String): PhotoDto
suspend fun getFeaturedPhotos(): PhotosResponse
suspend fun searchPhotos(query: String): PhotosResponse

// Packages
suspend fun getPackages(): PackagesResponse
suspend fun getPackageById(packageId: String): PackageDto
suspend fun getPopularPackages(): PackagesResponse
suspend fun getPackageAddOns(packageId: String): List<PackageAddOnDto>

// Bookings
suspend fun createBooking(request: CreateBookingRequest): BookingResponse
suspend fun getBookingById(bookingId: String): BookingResponse
suspend fun getUserBookings(): List<BookingResponse>
suspend fun getAvailableDates(packageId: String, month: Int, year: Int): AvailabilityResponse
suspend fun cancelBooking(bookingId: String): Unit

// Payments
suspend fun submitPayment(request: SubmitPaymentRequest): PaymentResultDto
suspend fun getPaymentMethods(): List<PaymentMethodDto>
suspend fun addPaymentMethod(request: AddPaymentMethodRequest): PaymentMethodDto

// Profile
suspend fun getPhotographerProfile(): PhotographerProfileDto
suspend fun sendContactMessage(request: ContactMessageRequest): Unit
```

### 6.3 Data Transfer Objects (DTOs)

**Files in:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/remote/dto/`

Available DTOs:
- `AlbumDto.kt`
- `PhotoDto.kt`
- `PackageDto.kt`
- `BookingDto.kt`
- `PaymentDto.kt`
- `ProfileDto.kt`

---

## 7. Dependency Injection

### 7.1 Koin Setup

**Framework:** Koin 4.0.0 (Kotlin-first DI)

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/di/AppModule.kt`

### 7.2 Module Structure

#### Shared Module (Common to all platforms)

```kotlin
val sharedModule = module {
    // Database
    single<CamManDatabase> { ... }
    
    // DAOs
    single { get<CamManDatabase>().albumDao() }
    single { get<CamManDatabase>().photoDao() }
    single { get<CamManDatabase>().bookingDao() }
    single { get<CamManDatabase>().packageDao() }
    single { get<CamManDatabase>().paymentMethodDao() }
    single { get<CamManDatabase>().photographerProfileDao() }
    single { get<CamManDatabase>().photographerRegistrationDao() }
    single { get<CamManDatabase>().notificationDao() }
    single { get<CamManDatabase>().reviewDao() }
    
    // Network
    single { createHttpClient() }
    single { CamManApiService(get()) }
    
    // Repositories
    single<AlbumRepository> { AlbumRepositoryImpl(get(), get()) }
    single<PhotoRepository> { PhotoRepositoryImpl(get(), get()) }
    single<PackageRepository> { PackageRepositoryImpl(get(), get()) }
    single<BookingRepository> { BookingRepositoryImpl(get(), get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    single<PhotographerRepository> { PhotographerRepositoryImpl(get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }
    single { ClientBookingRepositoryImpl(get(), get()) }
    
    // Use Cases
    single { GetAlbumsUseCase(get()) }
    single { GetAlbumPhotosUseCase(get()) }
    single { GetPackagesUseCase(get()) }
    single { CreateBookingUseCase(get()) }
    single { SubmitPaymentUseCase(get()) }
    single { SubmitReviewUseCase(get()) }
    single { GetReviewsUseCase(get()) }
    
    // ViewModels
    viewModel { BrowsePhotographersViewModel(get()) }
    viewModel { MyBookingsViewModel(get()) }
    viewModel { PhotographerDetailViewModel(get()) }
    viewModel { PhotographerMainViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { BookingViewModel(get(), get(), get(), get()) }
    viewModel { ReviewViewModel(get(), get(), get(), get(), get()) }
    // ... more ViewModels
}
```

### 7.3 Platform-Specific Module

```kotlin
expect val platformModule: Module
```

**Android Implementation:** 
- Location: `composeApp/src/androidMain/kotlin/com/gndy/camman/di/AppModule.android.kt`
- Provides platform-specific dependencies

**iOS Implementation:**
- Location: `composeApp/src/iosMain/kotlin/com/gndy/camman/di/AppModule.ios.kt`
- Provides platform-specific dependencies

### 7.4 Koin Initialization

**File:** `composeApp/src/androidMain/kotlin/com/gndy/camman/CamManApplication.kt`

```kotlin
class CamManApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CamManApplication)
            androidLogger()
            modules(sharedModule, platformModule)
        }
    }
}
```

### 7.5 ViewModel Injection in Composables

```kotlin
@Composable
fun BrowsePhotographersScreen(
    onNavigateToPhotographer: (String) -> Unit,
    viewModel: BrowsePhotographersViewModel = koinViewModel()
) {
    // Use injected viewModel
}
```

---

## 8. Domain Models & Data Layer

### 8.1 Domain Models

**Location:** `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/model/`

Available Models:
1. **Photographer** - Basic photographer info for browsing
2. **PhotographerProfile** - Detailed photographer profile
3. **PhotographerRegistration** - Photographer registration data
4. **Album** - Photo album with categories
5. **Photo** - Individual photo
6. **Package** - Photography package/service
7. **Booking** - Client booking with status tracking
8. **PaymentMethod** - Payment options
9. **Review** - Client reviews with rating system
10. **Notification** - In-app notifications
11. **AuthUser** - Authenticated user info
12. **UserType** - Enum for user role (Client/Photographer)

#### Key Model Examples

**Photographer Model:**
```kotlin
data class Photographer(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val bio: String?,
    val specialties: List<String>,
    val location: String?,
    val rating: Float,
    val reviewCount: Int,
    val startingPrice: Double?,
    val currency: String = "USD",
    val isAvailable: Boolean = true,
    val portfolioPreviewUrls: List<String> = emptyList()
)
```

**Review Model:**
```kotlin
data class Review(
    val id: String,
    val clientId: String,
    val photographerId: String,
    val bookingId: String,
    val rating: Int,              // 1-5 stars
    val reviewText: String,
    val tags: List<ReviewTag>,
    val photoUrls: List<String>,
    val isAnonymous: Boolean,
    val isVerified: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val photographerResponse: String?,
    val helpfulCount: Int,
    // ... more fields
)
```

**Album Model:**
```kotlin
data class Album(
    val id: String,
    val title: String,
    val description: String,
    val coverImageUrl: String,
    val category: AlbumCategory,
    val photoCount: Int,
    val createdAt: LocalDate,
    val isFeatured: Boolean = false
)

enum class AlbumCategory(val displayName: String) {
    WEDDING, PORTRAIT, EVENT, LANDSCAPE, PRODUCT, FASHION, 
    LIFESTYLE, CORPORATE, FAMILY, MATERNITY, NEWBORN, OTHER
}
```

### 8.2 Repository Interfaces

**Location:** `composeApp/src/commonMain/kotlin/com/gndy/camman/domain/repository/`

#### PhotographerRepository (Reference Example)
```kotlin
interface PhotographerRepository {
    fun getAllPhotographers(): Flow<Resource<List<Photographer>>>
    suspend fun getPhotographerById(id: String): Resource<Photographer>
    fun getCurrentPhotographerProfile(): Flow<Resource<PhotographerRegistration?>>
    suspend fun hasCompletedRegistration(): Boolean
    suspend fun savePhotographerRegistration(registration: PhotographerRegistration): Resource<PhotographerRegistration>
    suspend fun updatePhotographerProfile(registration: PhotographerRegistration): Resource<Unit>
    suspend fun updateAvailability(isAvailable: Boolean): Resource<Unit>
    fun searchPhotographers(query: String): Flow<Resource<List<Photographer>>>
    fun filterBySpecialty(specialty: String): Flow<Resource<List<Photographer>>>
}
```

Other Repositories:
- `AlbumRepository.kt`
- `PhotoRepository.kt`
- `PackageRepository.kt`
- `BookingRepository.kt`
- `PaymentRepository.kt`
- `ProfileRepository.kt`
- `ReviewRepository.kt`
- `AuthRepository.kt`

### 8.3 Room Database

**File:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/local/CamManDatabase.kt`

```kotlin
@Database(
    entities = [
        AlbumEntity::class,
        PhotoEntity::class,
        PackageEntity::class,
        BookingEntity::class,
        PaymentMethodEntity::class,
        PhotographerProfileEntity::class,
        PhotographerRegistrationEntity::class,
        NotificationEntity::class,
        ReviewEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class CamManDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun photoDao(): PhotoDao
    abstract fun packageDao(): PackageDao
    abstract fun bookingDao(): BookingDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun photographerProfileDao(): PhotographerProfileDao
    abstract fun photographerRegistrationDao(): PhotographerRegistrationDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    
    companion object {
        const val DATABASE_NAME = "camman_database"
    }
}
```

### 8.4 Database Entities

**Location:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/local/entity/`

- `AlbumEntity.kt`
- `PhotoEntity.kt`
- `PackageEntity.kt`
- `BookingEntity.kt`
- `PaymentMethodEntity.kt`
- `PhotographerProfileEntity.kt`
- `PhotographerRegistrationEntity.kt`
- `NotificationEntity.kt`
- `ReviewEntity.kt`

### 8.5 Repository Implementations

**Location:** `composeApp/src/commonMain/kotlin/com/gndy/camman/data/repository/`

**PhotographerRepositoryImpl Example:**
- Handles offline-first approach (checks local DB first, then API)
- Includes mock data for demonstration
- Implements caching with MutableStateFlow triggers
- Transforms between domain models and entities

---

## 9. Permissions Handling

### 9.1 Current State
**Status:** Minimal permissions setup in place

**Android Manifest:**
**File:** `composeApp/src/androidMain/AndroidManifest.xml`

Currently declared permissions:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 9.2 Required Permissions for "Nearby Photographers" Feature

#### Location Permissions (iOS & Android)
For implementing the nearby photographers feature, you'll need:

**Android (AndroidManifest.xml - add these):**
```xml
<!-- Approximate location -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!-- Precise location -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<!-- Background location (if needed for continuous tracking) -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

**iOS (Info.plist - handled via Xcode or property list):**
```
NSLocationWhenInUseUsageDescription
NSLocationAlwaysAndWhenInUseUsageDescription (for background)
```

### 9.3 Permission Request Pattern to Follow

Similar to how other platforms handle permissions, you should:
1. Create a platform-specific module/class for permission requests
2. Use Koin to inject platform-specific permission handlers
3. Integrate with ViewModel to show permission dialogs
4. Handle permission states (Not Requested, Granted, Denied, Permanently Denied)

**Recommended Library:** Kermit or ComposePermissions (KMP-compatible)

---

## 10. Map Integrations

### 10.1 Current State
**Status:** No existing map integrations found

### 10.2 Map Integration Recommendations for "Nearby Photographers"

#### Option 1: Google Maps (Recommended for Android, requires workaround for iOS)
- **Library:** `google-maps-compose` (Android only)
- **Consideration:** iOS support is limited; may need custom implementation

#### Option 2: Mapbox (KMP Support via KMP library)
- **Advantages:** 
  - Good KMP support through community libraries
  - Works on both Android and iOS
  - Better styling options
- **Libraries:** 
  - `mapbox-maps-android` (Android)
  - Native MapKit integration (iOS)

#### Option 3: OpenStreetMap (OSM) with Leaflet-like approach
- **Advantages:**
  - Free and open-source
  - Good KMP support
- **Considerations:** May require web view wrapper

#### Option 4: Apple Maps (Native for iOS) + Google Maps (Android)
- Use platform-specific implementations
- Android: `composeApp/src/androidMain/`
- iOS: `composeApp/src/iosMain/`

### 10.3 Integration Pattern to Follow

Based on the project's patterns, you should:

1. **Create Data Layer for Location:**
   ```
   domain/repository/LocationRepository.kt
   data/repository/LocationRepositoryImpl.kt
   domain/model/PhotographerLocation.kt
   ```

2. **API Endpoints for Nearby Search:**
   ```
   GET /photographers/nearby?lat=XX&lng=XX&radius=10&limit=20
   GET /photographers?location=current&distance=10km
   ```

3. **Platform-Specific Map Implementations:**
   ```
   presentation/components/MapScreen.kt
   androidMain/.../presentation/components/GoogleMapScreen.kt
   iosMain/.../presentation/components/AppleMapScreen.kt
   ```

---

## 11. Existing Features Reference

### 11.1 Feature Overview

#### 1. **Authentication & Onboarding**
**Status:** Partially implemented with Firebase Auth support (disabled by default)

**Files:**
- Auth ViewModels: `presentation/screens/auth/authviewmodel/*.kt`
- Auth Screens: `presentation/screens/auth/authscreens/*.kt`
- Domain Use Cases: `domain/usecase/auth/*.kt`
- Repository: `data/repository/AuthRepositoryImpl.kt`

**Features:**
- Sign In
- Sign Up
- Forgot Password
- User Type Selection (Client vs Photographer)
- Onboarding flow

#### 2. **Photographer Registration & Profile Management**
**Status:** Fully implemented with local persistence

**Files:**
- Model: `domain/model/PhotographerRegistration.kt`
- Entity: `data/local/entity/PhotographerRegistrationEntity.kt`
- DAO: `data/local/dao/PhotographerRegistrationDao.kt`
- ViewModel: `presentation/screens/photographer/PhotographerRegistrationViewModel.kt`
- EditProfile Screen: `presentation/screens/photographer/EditProfileScreen.kt`

**Features:**
- Profile creation and editing
- Image upload (profile & cover)
- Portfolio management
- Specialty selection
- Availability status toggle

**Profile Data Stored Locally:**
```kotlin
data class PhotographerRegistration(
    val id: String,
    val fullName: String,
    val bio: String,
    val specialties: List<String>,
    val location: String,
    val profileImageUrl: String?,
    val coverImageUrl: String?,
    val startingPrice: Double,
    val currency: String,
    val portfolioUrls: List<String>,
    val isAvailable: Boolean,
    val isProfileComplete: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
```

#### 3. **Browse Photographers**
**Status:** Fully implemented with search & filter

**Files:**
- Model: `domain/model/Photographer.kt`
- ViewModel: `presentation/screens/user/BrowsePhotographersViewModel.kt`
- Screen: `presentation/screens/user/BrowsePhotographersScreen.kt`
- Repository: `domain/repository/PhotographerRepository.kt`

**Features:**
- Browse all photographers
- Search by name, location, specialty
- Filter by specialty (Wedding, Portrait, Event, etc.)
- Debounced search (300ms)
- Display ratings and review counts
- Show availability status
- Display starting prices
- Mock data with 6 sample photographers

#### 4. **Portfolio Management**
**Status:** Fully implemented with album & photo browsing

**Files:**
- Models: `domain/model/Album.kt`, `domain/model/Photo.kt`
- Repositories: `domain/repository/AlbumRepository.kt`, `domain/repository/PhotoRepository.kt`
- Screens: `presentation/screens/portfolio/`.kt files
- ViewModels: `presentation/screens/portfolio/PortfolioViewModel.kt`, etc.

**Features:**
- Album display with categories (Wedding, Portrait, Event, etc.)
- Album detail with photo grid
- Photo preview with full-screen pager
- Album filtering by category
- Featured albums

#### 5. **Package Management & Booking**
**Status:** Fully implemented with 5-step booking wizard

**Files:**
- Models: `domain/model/Package.kt`, `domain/model/Booking.kt`
- Repository: `domain/repository/BookingRepository.kt`
- Screen: `presentation/screens/booking/BookingScreen.kt`
- ViewModel: `presentation/screens/booking/BookingViewModel.kt`

**Booking Steps:**
1. Package selection with add-ons
2. Choose date & time (availability check)
3. Client details input
4. Payment method selection
5. Confirmation with booking summary

**Booking Data Model:**
```kotlin
data class Booking(
    val id: String,
    val packageId: String,
    val clientInfo: ClientInfo,
    val sessionDate: LocalDate,
    val sessionTime: LocalTime,
    val location: String,
    val notes: String?,
    val status: BookingStatus,
    val totalAmount: Double,
    val depositAmount: Double,
    val depositPaid: Boolean,
    val paymentMethod: PaymentMethod?,
    val addOns: List<BookingAddOn> = emptyList()
)
```

#### 6. **Reviews & Ratings System**
**Status:** Fully implemented with photographer responses

**Files:**
- Model: `domain/model/Review.kt`
- Entity: `data/local/entity/ReviewEntity.kt`
- DAO: `data/local/dao/ReviewDao.kt`
- Repository: `domain/repository/ReviewRepository.kt`
- Screens: `presentation/screens/review/` (5 screens)
- ViewModels: `presentation/screens/review/ReviewViewModel.kt`

**Features:**
- Submit reviews (1-5 stars)
- Add review photos (up to 3)
- Predefined review tags (20 tags: positive + neutral/negative)
- Anonymous reviews option
- Photographer response capability
- Review editing (within 48 hours)
- Review statistics with breakdown by rating
- Mark review as helpful
- Sort options: Most Recent, Highest Rated, Lowest Rated, Most Helpful
- Filter options: By rating, verified only, with photos, with response

**Review Tags:**
```kotlin
enum class ReviewTag(val displayName: String, val isPositive: Boolean) {
    PROFESSIONAL, ON_TIME, GREAT_QUALITY, GOOD_COMMUNICATION, CREATIVE,
    FRIENDLY, PATIENT, EXCEEDED_EXPECTATIONS, QUICK_DELIVERY, GREAT_VALUE,
    LATE, POOR_COMMUNICATION, UNPROFESSIONAL, SLOW_DELIVERY, OVERPRICED, QUALITY_ISSUES
}
```

#### 7. **User Bookings & My Bookings**
**Status:** Fully implemented with booking history

**Files:**
- Screen: `presentation/screens/user/MyBookingsScreen.kt`
- ViewModel: `presentation/screens/user/MyBookingsViewModel.kt`
- Repository: `data/repository/ClientBookingRepositoryImpl.kt`

**Features:**
- View booking history
- Current and past bookings
- Booking status tracking
- View booking details
- Submit review for completed bookings

#### 8. **Notifications**
**Status:** Partially implemented with local persistence

**Files:**
- Model: `domain/model/Notification.kt`
- Entity: `data/local/entity/NotificationEntity.kt`
- DAO: `data/local/dao/NotificationDao.kt`
- Screen: `presentation/screens/photographer/PhotographerNotificationsScreen.kt`
- ViewModel: `presentation/screens/photographer/PhotographerNotificationsViewModel.kt`

**Features:**
- In-app notifications
- Booking notifications (for photographers)
- Review notifications
- Local persistence with Room DB
- Mark as read functionality

#### 9. **Photographer Detail Screen**
**Status:** Fully implemented with portfolio preview

**Files:**
- Screen: `presentation/screens/user/PhotographerDetailScreen.kt`
- ViewModel: `presentation/screens/user/PhotographerDetailViewModel.kt`

**Features:**
- Full photographer profile view
- Portfolio preview (3-5 photos)
- Rating and review count
- Specialties display
- Bio and experience info
- Contact information
- Quick action buttons (View Portfolio, View Packages, Message, Book Now)

### 11.2 Database Schema (Current)

**Database Version:** 4
**Table Count:** 9 entities

```
CamManDatabase
├── album
├── photo
├── package
├── booking
├── payment_method
├── photographer_profile
├── photographer_registration
├── notification
└── review
```

### 11.3 API Service Endpoints (Implemented)

**Base URL:** `https://api.camman.com/v1`

**Categories:**
- Albums (4 endpoints)
- Photos (4 endpoints)
- Packages (5 endpoints)
- Bookings (5 endpoints)
- Payments (5 endpoints)
- Profile (2 endpoints)

**Total:** 25 endpoints

### 11.4 Bottom Navigation Structure

#### Client (UserMain)
1. **BrowsePhotographers** - Home tab
2. **UserBookings** - My Bookings tab
3. **Favorites** - Favorites tab (placeholder)
4. **UserProfile** - Profile tab (placeholder)

#### Photographer (PhotographerMain)
1. **PhotographerHome** - Dashboard
2. **PhotographerPortfolio** - Portfolio management
3. **PhotographerBookings** - Bookings management
4. **PhotographerProfile** - Profile/Settings

---

## Summary Table: Key Files by Component

| Component | File Path | Lines | Purpose |
|-----------|-----------|-------|---------|
| **App Entry** | `App.kt` | 50 | Main app composable with theme and navigation |
| **Navigation (Monolithic)** | `presentation/navigation/NavGraph.kt` | 374 | All routes in single NavHost |
| **Navigation (Modular)** | `presentation/navigation/ModularNavGraph.kt` | Variable | Entry builders pattern |
| **Navigation Routes** | `presentation/navigation/Screen.kt` | 109 | Type-safe route definitions |
| **DI Configuration** | `di/AppModule.kt` | 172 | All dependency definitions |
| **App Initialization** | `androidMain/CamManApplication.kt` | 21 | Koin setup for Android |
| **API Service** | `data/remote/api/CamManApiService.kt` | 155 | 25 API endpoints |
| **HTTP Client Setup** | `data/remote/HttpClientFactory.kt` | 43 | Ktor client configuration |
| **Database** | `data/local/CamManDatabase.kt` | 54 | Room database definition |
| **Resource Wrapper** | `domain/util/Resource.kt` | 48 | Result handling pattern |
| **Theme** | `presentation/theme/Theme.kt` | 88 | Material 3 theme config |
| **Colors** | `presentation/theme/Color.kt` | 40 | Photography-inspired palette |

---

## Implementation Guide for "Nearby Photographers"

### Recommended Approach Based on Existing Patterns

1. **Create Domain Layer:**
   - Add `domain/model/NearbyPhotographerLocation.kt`
   - Add `domain/repository/LocationRepository.kt`
   - Add `domain/usecase/nearby/GetNearbyPhotographersUseCase.kt`

2. **Create Data Layer:**
   - Extend `data/remote/api/CamManApiService.kt` with location endpoints
   - Add `data/remote/dto/LocationDto.kt`
   - Add `data/repository/LocationRepositoryImpl.kt`
   - Add location permission handling

3. **Create Presentation Layer:**
   - Add `presentation/screens/nearby/NearbyPhotographersScreen.kt`
   - Add `presentation/screens/nearby/NearbyPhotographersViewModel.kt`
   - Add map component (platform-specific)
   - Update `presentation/navigation/Screen.kt` with new route

4. **Update DI:**
   - Add new repository to `di/AppModule.kt`
   - Add new usecase to `di/AppModule.kt`
   - Add new viewmodel to `di/AppModule.kt`

5. **Add Permissions:**
   - Update `AndroidManifest.xml` with location permissions
   - Create `androidMain/presentation/permissions/LocationPermissionHandler.kt`
   - Create `iosMain/presentation/permissions/LocationPermissionHandler.kt`
   - Inject via Koin

6. **Integrate with Navigation:**
   - Add route to bottom nav in `BrowsePhotographersScreen`
   - Update nav graph to include new screen
   - Add navigation between browse and nearby screens

---

**Document Version:** 1.0
**Last Updated:** December 2025
**Based on:** CamMan Project Source Code Analysis
