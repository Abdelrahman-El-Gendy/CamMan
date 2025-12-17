package com.gndy.camman.presentation.screens.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gndy.camman.domain.model.PackageCategory
import com.gndy.camman.domain.model.PackageDuration
import com.gndy.camman.domain.model.PhotographyPackage
import com.gndy.camman.domain.usecase.packages.GetPackagesUseCase
import com.gndy.camman.domain.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

data class PackagesUiState(
    val isLoading: Boolean = true,
    val packages: List<PhotographyPackage> = emptyList(),
    val selectedCategory: PackageCategory? = null,
    val categories: List<PackageCategory> = PackageCategory.entries,
    val error: String? = null
)

sealed class PackagesUiEvent {
    data class NavigateToPackageDetail(val packageId: String) : PackagesUiEvent()
    data class NavigateToBooking(val packageId: String) : PackagesUiEvent()
    data class ShowError(val message: String) : PackagesUiEvent()
}

class PackagesViewModel(
    private val getPackagesUseCase: GetPackagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PackagesUiState())
    val uiState: StateFlow<PackagesUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PackagesUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadPackages()
    }

    private fun loadPackages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ===== MOCK DATA - Comment this block when backend is ready =====
            val packages = getMockPackages()
            val filteredPackages = _uiState.value.selectedCategory?.let { category ->
                packages.filter { it.category == category }
            } ?: packages

            _uiState.update {
                it.copy(
                    packages = filteredPackages,
                    isLoading = false
                )
            }
            // ===== END MOCK DATA =====

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                val flow = _uiState.value.selectedCategory?.let { category ->
                    getPackagesUseCase.getByCategory(category)
                } ?: getPackagesUseCase()
                
                flow.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    packages = result.data ?: emptyList(),
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
            // ===== END REAL IMPLEMENTATION =====
            */
        }
    }

    private fun getMockPackages(): List<PhotographyPackage> {
        val allPackages = listOf(
            PhotographyPackage(
                id = "1",
                name = "Essential Portrait",
                description = "Perfect for headshots and personal branding. Includes professional lighting and basic retouching.",
                category = PackageCategory.PORTRAIT,
                price = 199.0,
                currency = "USD",
                duration = PackageDuration(1, 0),
                features = listOf(
                    "1 hour session",
                    "1 location",
                    "15 edited photos",
                    "Online gallery",
                    "Basic retouching"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Print release"
                ),
                maxPhotos = 15,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 7,
                isPopular = false,
                isActive = true
            ),
            PhotographyPackage(
                id = "2",
                name = "Premium Portrait",
                description = "Our most popular portrait package with multiple outfits and locations.",
                category = PackageCategory.PORTRAIT,
                price = 399.0,
                currency = "USD",
                duration = PackageDuration(2, 0),
                features = listOf(
                    "2 hour session",
                    "2 locations",
                    "30 edited photos",
                    "Online gallery",
                    "Advanced retouching",
                    "Outfit changes"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Print release",
                    "5 premium prints (8x10)"
                ),
                maxPhotos = 30,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 10,
                isPopular = true,
                isActive = true
            ),
            PhotographyPackage(
                id = "3",
                name = "Wedding Essentials",
                description = "Coverage for your special day with all the essential moments captured.",
                category = PackageCategory.WEDDING,
                price = 1999.0,
                currency = "USD",
                duration = PackageDuration(6, 0),
                features = listOf(
                    "6 hours coverage",
                    "1 photographer",
                    "200+ edited photos",
                    "Online gallery",
                    "Professional editing",
                    "Engagement session included"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "USB drive with all photos",
                    "Print release"
                ),
                maxPhotos = 200,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 30,
                isPopular = false,
                isActive = true
            ),
            PhotographyPackage(
                id = "4",
                name = "Wedding Premium",
                description = "Complete wedding coverage with two photographers and premium album.",
                category = PackageCategory.WEDDING,
                price = 3499.0,
                currency = "USD",
                duration = PackageDuration(10, 0),
                features = listOf(
                    "10 hours coverage",
                    "2 photographers",
                    "400+ edited photos",
                    "Online gallery",
                    "Premium editing",
                    "Engagement session",
                    "Rehearsal dinner coverage"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Premium leather album",
                    "USB drive",
                    "Parent albums (2)"
                ),
                maxPhotos = 400,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 45,
                isPopular = true,
                isActive = true
            ),
            PhotographyPackage(
                id = "5",
                name = "Event Coverage",
                description = "Professional coverage for corporate events, parties, and celebrations.",
                category = PackageCategory.EVENT,
                price = 599.0,
                currency = "USD",
                duration = PackageDuration(4, 0),
                features = listOf(
                    "4 hours coverage",
                    "1 photographer",
                    "100+ edited photos",
                    "Online gallery",
                    "Quick turnaround"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Print release"
                ),
                maxPhotos = 100,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 5,
                isPopular = false,
                isActive = true
            ),
            PhotographyPackage(
                id = "6",
                name = "Mini Session",
                description = "Quick and affordable session perfect for seasonal photos or gifts.",
                category = PackageCategory.MINI_SESSION,
                price = 99.0,
                currency = "USD",
                duration = PackageDuration(0, 30),
                features = listOf(
                    "30 minute session",
                    "1 location",
                    "10 edited photos",
                    "Online gallery"
                ),
                deliverables = listOf(
                    "Digital files",
                    "Online gallery"
                ),
                maxPhotos = 10,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 3,
                isPopular = false,
                isActive = true
            )
        )

        return _uiState.value.selectedCategory?.let { category ->
            allPackages.filter { it.category == category }
        } ?: allPackages
    }

    fun onCategorySelected(category: PackageCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadPackages()
    }

    fun onPackageClick(packageId: String) {
        viewModelScope.launch {
            _uiEvents.emit(PackagesUiEvent.NavigateToPackageDetail(packageId))
        }
    }

    fun onBookNowClick(packageId: String) {
        viewModelScope.launch {
            _uiEvents.emit(PackagesUiEvent.NavigateToBooking(packageId))
        }
    }
}

// ============== Package Detail ViewModel ==============

data class PackageDetailUiState(
    val isLoading: Boolean = true,
    val packageDetails: PhotographyPackage? = null,
    val error: String? = null
)

class PackageDetailViewModel(
    private val getPackagesUseCase: GetPackagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PackageDetailUiState())
    val uiState: StateFlow<PackageDetailUiState> = _uiState.asStateFlow()

    fun loadPackage(packageId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ===== MOCK DATA - Comment this block when backend is ready =====
            val pkg = getMockPackage(packageId)
            _uiState.update {
                it.copy(
                    packageDetails = pkg,
                    isLoading = false
                )
            }
            // ===== END MOCK DATA =====

            /*
            // ===== REAL IMPLEMENTATION - Uncomment when backend is ready =====
            try {
                getPackagesUseCase.getPackageById(packageId).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    packageDetails = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
            // ===== END REAL IMPLEMENTATION =====
            */
        }
    }

    private fun getMockPackage(packageId: String): PhotographyPackage {
        // Return package based on ID for more accurate mock data
        val packages = mapOf(
            "1" to PhotographyPackage(
                id = "1",
                name = "Essential Portrait",
                description = "Perfect for headshots and personal branding. Includes professional lighting and basic retouching.",
                category = PackageCategory.PORTRAIT,
                price = 199.0,
                currency = "USD",
                duration = PackageDuration(1, 0),
                features = listOf(
                    "1 hour session",
                    "1 location",
                    "15 edited photos",
                    "Online gallery",
                    "Basic retouching"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Print release"
                ),
                maxPhotos = 15,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 7,
                isPopular = false,
                isActive = true
            ),
            "2" to PhotographyPackage(
                id = "2",
                name = "Premium Portrait",
                description = "Our most popular portrait package with multiple outfits and locations. Perfect for professionals, influencers, and anyone wanting high-quality portraits.",
                category = PackageCategory.PORTRAIT,
                price = 399.0,
                currency = "USD",
                duration = PackageDuration(2, 0),
                features = listOf(
                    "2 hour session",
                    "2 locations of your choice",
                    "30 professionally edited photos",
                    "Online viewing gallery",
                    "Advanced skin retouching",
                    "Multiple outfit changes",
                    "Professional lighting setup",
                    "Props and accessories available"
                ),
                deliverables = listOf(
                    "High-resolution digital files (300 DPI)",
                    "Web-optimized versions",
                    "Online viewing gallery",
                    "Print release for personal use",
                    "5 premium prints (8x10)"
                ),
                maxPhotos = 30,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 10,
                isPopular = true,
                isActive = true
            ),
            "3" to PhotographyPackage(
                id = "3",
                name = "Wedding Essentials",
                description = "Coverage for your special day with all the essential moments captured.",
                category = PackageCategory.WEDDING,
                price = 1999.0,
                currency = "USD",
                duration = PackageDuration(6, 0),
                features = listOf(
                    "6 hours coverage",
                    "1 photographer",
                    "200+ edited photos",
                    "Online gallery",
                    "Professional editing",
                    "Engagement session included"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "USB drive with all photos",
                    "Print release"
                ),
                maxPhotos = 200,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 30,
                isPopular = false,
                isActive = true
            ),
            "4" to PhotographyPackage(
                id = "4",
                name = "Wedding Premium",
                description = "Complete wedding coverage with two photographers and premium album.",
                category = PackageCategory.WEDDING,
                price = 3499.0,
                currency = "USD",
                duration = PackageDuration(10, 0),
                features = listOf(
                    "10 hours coverage",
                    "2 photographers",
                    "400+ edited photos",
                    "Online gallery",
                    "Premium editing",
                    "Engagement session",
                    "Rehearsal dinner coverage"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Premium leather album",
                    "USB drive",
                    "Parent albums (2)"
                ),
                maxPhotos = 400,
                includesEditing = true,
                includesPrints = true,
                turnaroundDays = 45,
                isPopular = true,
                isActive = true
            ),
            "5" to PhotographyPackage(
                id = "5",
                name = "Event Coverage",
                description = "Professional coverage for corporate events, parties, and celebrations.",
                category = PackageCategory.EVENT,
                price = 599.0,
                currency = "USD",
                duration = PackageDuration(4, 0),
                features = listOf(
                    "4 hours coverage",
                    "1 photographer",
                    "100+ edited photos",
                    "Online gallery",
                    "Quick turnaround"
                ),
                deliverables = listOf(
                    "High-resolution digital files",
                    "Online viewing gallery",
                    "Print release"
                ),
                maxPhotos = 100,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 5,
                isPopular = false,
                isActive = true
            ),
            "6" to PhotographyPackage(
                id = "6",
                name = "Mini Session",
                description = "Quick and affordable session perfect for seasonal photos or gifts.",
                category = PackageCategory.MINI_SESSION,
                price = 99.0,
                currency = "USD",
                duration = PackageDuration(0, 30),
                features = listOf(
                    "30 minute session",
                    "1 location",
                    "10 edited photos",
                    "Online gallery"
                ),
                deliverables = listOf(
                    "Digital files",
                    "Online gallery"
                ),
                maxPhotos = 10,
                includesEditing = true,
                includesPrints = false,
                turnaroundDays = 3,
                isPopular = false,
                isActive = true
            )
        )

        return packages[packageId] ?: packages["2"]!!
    }
}
