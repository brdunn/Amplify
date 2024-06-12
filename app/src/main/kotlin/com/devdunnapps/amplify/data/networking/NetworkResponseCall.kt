package com.devdunnapps.amplify.data.networking

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class NetworkResponseCall<T>(
    private val originalCall: Call<T>,
    private val emptyBodyHandler: (Int) -> NetworkResponse<T> = { NetworkResponse.Failure(it) }
) : Call<NetworkResponse<T>> {

    override fun enqueue(callback: Callback<NetworkResponse<T>>) {
        originalCall.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val result = when {
                    response.isSuccessful ->
                        response.body()?.let { NetworkResponse.Success(it) } ?: NetworkResponse.Failure()
                    else -> NetworkResponse.Failure(code = response.code())
                }

                callback.onResponse(this@NetworkResponseCall, Response.success(result))
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.Failure()))
            }
        })
    }

    override fun isExecuted() = originalCall.isExecuted

    override fun clone() = NetworkResponseCall(originalCall.clone(), emptyBodyHandler)

    override fun isCanceled() = originalCall.isCanceled

    override fun cancel() = originalCall.cancel()

    override fun execute(): Response<NetworkResponse<T>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun request(): Request = originalCall.request()

    override fun timeout(): Timeout = originalCall.timeout()
}