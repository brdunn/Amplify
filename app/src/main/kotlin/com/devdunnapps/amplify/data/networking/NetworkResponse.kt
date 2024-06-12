package com.devdunnapps.amplify.data.networking

sealed class NetworkResponse<out T> {
    data class Success<T>(val result: T): NetworkResponse<T>()
    data class Failure(val code: Int = 0): NetworkResponse<Nothing>()

    val data: T get() = (this as Success).result
}

fun <T, R> NetworkResponse<T>.map(transform: (T) -> R?): NetworkResponse<R> {
    return when (this) {
        is NetworkResponse.Success -> {
            val transformedData = transform(data) ?: return NetworkResponse.Failure()
            NetworkResponse.Success(transformedData)
        }
        is NetworkResponse.Failure -> NetworkResponse.Failure(code)
    }
}
