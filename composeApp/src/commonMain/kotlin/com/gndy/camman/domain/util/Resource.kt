package com.gndy.camman.domain.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data!!))
            is Error -> Error(message!!, data?.let(transform))
            is Loading -> Loading(data?.let(transform))
        }
    }

    fun getOrNull(): T? = data

    fun getOrThrow(): T = data ?: throw IllegalStateException(message ?: "No data available")

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success && data != null) {
            action(data)
        }
        return this
    }

    inline fun onError(action: (String) -> Unit): Resource<T> {
        if (this is Error && message != null) {
            action(message)
        }
        return this
    }

    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}
