package com.gndy.camman.data.remote.api

import com.gndy.camman.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CamManApiService(
    private val httpClient: HttpClient
) {
    companion object {
        // Placeholder base URL - replace with actual backend URL
        private const val BASE_URL = "https://api.camman.com/v1"
    }

    // ============== Albums ==============

    suspend fun getAlbums(): AlbumsResponse {
        return httpClient.get("$BASE_URL/albums").body()
    }

    suspend fun getAlbumById(albumId: String): AlbumDto {
        return httpClient.get("$BASE_URL/albums/$albumId").body()
    }

    suspend fun getAlbumsByCategory(category: String): AlbumsResponse {
        return httpClient.get("$BASE_URL/albums") {
            parameter("category", category)
        }.body()
    }

    suspend fun getFeaturedAlbums(): AlbumsResponse {
        return httpClient.get("$BASE_URL/albums/featured").body()
    }

    // ============== Photos ==============

    suspend fun getPhotosByAlbum(albumId: String): PhotosResponse {
        return httpClient.get("$BASE_URL/albums/$albumId/photos").body()
    }

    suspend fun getPhotoById(photoId: String): PhotoDto {
        return httpClient.get("$BASE_URL/photos/$photoId").body()
    }

    suspend fun getFeaturedPhotos(): PhotosResponse {
        return httpClient.get("$BASE_URL/photos/featured").body()
    }

    suspend fun searchPhotos(query: String): PhotosResponse {
        return httpClient.get("$BASE_URL/photos/search") {
            parameter("q", query)
        }.body()
    }

    // ============== Packages ==============

    suspend fun getPackages(): PackagesResponse {
        return httpClient.get("$BASE_URL/packages").body()
    }

    suspend fun getPackageById(packageId: String): PackageDto {
        return httpClient.get("$BASE_URL/packages/$packageId").body()
    }

    suspend fun getPackagesByCategory(category: String): PackagesResponse {
        return httpClient.get("$BASE_URL/packages") {
            parameter("category", category)
        }.body()
    }

    suspend fun getPopularPackages(): PackagesResponse {
        return httpClient.get("$BASE_URL/packages/popular").body()
    }

    suspend fun getPackageAddOns(packageId: String): List<PackageAddOnDto> {
        return httpClient.get("$BASE_URL/packages/$packageId/addons").body()
    }

    // ============== Bookings ==============

    suspend fun createBooking(request: CreateBookingRequest): BookingResponse {
        return httpClient.post("$BASE_URL/bookings") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getBookingById(bookingId: String): BookingResponse {
        return httpClient.get("$BASE_URL/bookings/$bookingId").body()
    }

    suspend fun getUserBookings(): List<BookingResponse> {
        return httpClient.get("$BASE_URL/bookings").body()
    }

    suspend fun getAvailableDates(packageId: String, month: Int, year: Int): AvailabilityResponse {
        return httpClient.get("$BASE_URL/availability") {
            parameter("packageId", packageId)
            parameter("month", month)
            parameter("year", year)
        }.body()
    }

    suspend fun cancelBooking(bookingId: String): Unit {
        httpClient.post("$BASE_URL/bookings/$bookingId/cancel")
    }

    // ============== Payments ==============

    suspend fun submitPayment(request: SubmitPaymentRequest): PaymentResultDto {
        return httpClient.post("$BASE_URL/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getPaymentMethods(): List<PaymentMethodDto> {
        return httpClient.get("$BASE_URL/payment-methods").body()
    }

    suspend fun addPaymentMethod(request: AddPaymentMethodRequest): PaymentMethodDto {
        return httpClient.post("$BASE_URL/payment-methods") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun removePaymentMethod(paymentMethodId: String): Unit {
        httpClient.post("$BASE_URL/payment-methods/$paymentMethodId/delete")
    }

    suspend fun setDefaultPaymentMethod(paymentMethodId: String): Unit {
        httpClient.post("$BASE_URL/payment-methods/$paymentMethodId/set-default")
    }

    // ============== Profile ==============

    suspend fun getPhotographerProfile(): PhotographerProfileDto {
        return httpClient.get("$BASE_URL/profile").body()
    }

    suspend fun sendContactMessage(request: ContactMessageRequest): Unit {
        httpClient.post("$BASE_URL/contact") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
