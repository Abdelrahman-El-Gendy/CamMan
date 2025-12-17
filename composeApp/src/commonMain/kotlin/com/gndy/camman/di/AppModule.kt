package com.gndy.camman.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gndy.camman.data.local.CamManDatabase
import com.gndy.camman.data.local.DatabaseFactory
import com.gndy.camman.data.remote.api.CamManApiService
import com.gndy.camman.data.remote.createHttpClient
import com.gndy.camman.data.repository.AlbumRepositoryImpl
import com.gndy.camman.data.repository.BookingRepositoryImpl
import com.gndy.camman.data.repository.ClientBookingRepositoryImpl
import com.gndy.camman.data.repository.PackageRepositoryImpl
import com.gndy.camman.data.repository.PaymentRepositoryImpl
import com.gndy.camman.data.repository.PhotoRepositoryImpl
import com.gndy.camman.data.repository.PhotographerRepositoryImpl
import com.gndy.camman.data.repository.ProfileRepositoryImpl
import com.gndy.camman.data.repository.ReviewRepositoryImpl
import com.gndy.camman.domain.repository.AlbumRepository
import com.gndy.camman.domain.repository.BookingRepository
import com.gndy.camman.domain.repository.PackageRepository
import com.gndy.camman.domain.repository.PaymentRepository
import com.gndy.camman.domain.repository.PhotoRepository
import com.gndy.camman.domain.repository.PhotographerRepository
import com.gndy.camman.domain.repository.ProfileRepository
import com.gndy.camman.domain.repository.ReviewRepository
import com.gndy.camman.domain.usecase.album.GetAlbumPhotosUseCase
import com.gndy.camman.domain.usecase.album.GetAlbumsUseCase
import com.gndy.camman.domain.usecase.booking.CreateBookingUseCase
import com.gndy.camman.domain.usecase.packages.GetPackagesUseCase
import com.gndy.camman.domain.usecase.payment.SubmitPaymentUseCase
import com.gndy.camman.domain.usecase.profile.GetPhotographerContactInfoUseCase
import com.gndy.camman.domain.usecase.review.GetReviewsUseCase
import com.gndy.camman.domain.usecase.review.MarkReviewHelpfulUseCase
import com.gndy.camman.domain.usecase.review.RespondToReviewUseCase
import com.gndy.camman.domain.usecase.review.SubmitReviewUseCase
import com.gndy.camman.domain.usecase.review.UpdateReviewUseCase
import com.gndy.camman.presentation.screens.booking.BookingViewModel
import com.gndy.camman.presentation.screens.contact.ContactViewModel
import com.gndy.camman.presentation.screens.home.HomeViewModel
import com.gndy.camman.presentation.screens.packages.PackageDetailViewModel
import com.gndy.camman.presentation.screens.packages.PackagesViewModel
import com.gndy.camman.presentation.screens.photographer.EditProfileViewModel
import com.gndy.camman.presentation.screens.photographer.PhotographerMainViewModel
import com.gndy.camman.presentation.screens.photographer.PhotographerRegistrationViewModel
import com.gndy.camman.presentation.screens.portfolio.AlbumDetailViewModel
import com.gndy.camman.presentation.screens.portfolio.PhotoPreviewViewModel
import com.gndy.camman.presentation.screens.portfolio.PortfolioAlbumsViewModel
import com.gndy.camman.presentation.screens.review.ReviewViewModel
import com.gndy.camman.presentation.screens.user.BrowsePhotographersViewModel
import com.gndy.camman.presentation.screens.user.MyBookingsViewModel
import com.gndy.camman.presentation.screens.user.PhotographerDetailViewModel
import com.gndy.camman.presentation.screens.photographer.PhotographerNotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Set to true to enable Firebase Auth (requires valid google-services.json)
private const val ENABLE_FIREBASE_AUTH = false

val sharedModule = module {
    // Database
    single<CamManDatabase> {
        get<DatabaseFactory>()
            .create()
            .fallbackToDestructiveMigration(dropAllTables = true) // Added for version 4 (ReviewEntity)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    // DAOs
    single { get<CamManDatabase>().albumDao() }
    single { get<CamManDatabase>().photoDao() }
    single { get<CamManDatabase>().packageDao() }
    single { get<CamManDatabase>().bookingDao() }
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

    // Client Booking Repository (for client-side booking with notifications)
    single { ClientBookingRepositoryImpl(get(), get()) }

    // Photographer Repository (Singleton to share state between screens)
    single<PhotographerRepository> { PhotographerRepositoryImpl(get()) }

    // Review Repository
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }

    // Use Cases
    single { GetAlbumsUseCase(get()) }
    single { GetAlbumPhotosUseCase(get()) }
    single { GetPackagesUseCase(get()) }
    single { CreateBookingUseCase(get()) }
    single { SubmitPaymentUseCase(get()) }
    single { GetPhotographerContactInfoUseCase(get()) }

    // Review Use Cases
    single { SubmitReviewUseCase(get()) }
    single { GetReviewsUseCase(get()) }
    single { UpdateReviewUseCase(get()) }
    single { RespondToReviewUseCase(get()) }
    single { MarkReviewHelpfulUseCase(get()) }

    // ViewModels - User (Client)
    viewModel { BrowsePhotographersViewModel(get()) }
    viewModel { MyBookingsViewModel(get()) }
    viewModel { PhotographerDetailViewModel(get()) }

    // ViewModels - Photographer
    viewModel { PhotographerMainViewModel(get()) }
    viewModel { PhotographerRegistrationViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { PhotographerNotificationsViewModel(get(), get()) }

    // ViewModels - Shared/Photographer Detail
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { PortfolioAlbumsViewModel(get()) }
    viewModel { AlbumDetailViewModel(get(), get()) }
    viewModel { PhotoPreviewViewModel(get()) }
    viewModel { PackagesViewModel(get()) }
    viewModel { PackageDetailViewModel(get()) }
    viewModel { BookingViewModel(get(), get(), get(), get()) }
    viewModel { com.gndy.camman.presentation.screens.booking.BookingConfirmationViewModel(get()) }
    viewModel { ContactViewModel(get()) }

    // ViewModels - Reviews
    viewModel { ReviewViewModel(get(), get(), get(), get(), get()) }
}

// ===== FIREBASE AUTH MODULE =====
// Uncomment this when you have a valid Firebase configuration
/*
val firebaseAuthModule = module {
    // Firebase Auth
    single { dev.gitlive.firebase.Firebase.auth }
    
    // Auth Repository
    single<com.gndy.camman.domain.repository.AuthRepository> { 
        com.gndy.camman.data.repository.AuthRepositoryImpl(get()) 
    }
    
    // Auth Use Cases
    single { com.gndy.camman.domain.usecase.auth.GetAuthStateUseCase(get()) }
    single { com.gndy.camman.domain.usecase.auth.SignInUseCase(get()) }
    single { com.gndy.camman.domain.usecase.auth.SignUpUseCase(get()) }
    single { com.gndy.camman.domain.usecase.auth.SignOutUseCase(get()) }
    single { com.gndy.camman.domain.usecase.auth.ResetPasswordUseCase(get()) }
    
    // Auth ViewModels
    viewModel { com.gndy.camman.presentation.screens.auth.SignInViewModel(get(), get()) }
    viewModel { com.gndy.camman.presentation.screens.auth.SignUpViewModel(get(), get()) }
    viewModel { com.gndy.camman.presentation.screens.auth.ForgotPasswordViewModel(get()) }
}
*/

expect val platformModule: Module
