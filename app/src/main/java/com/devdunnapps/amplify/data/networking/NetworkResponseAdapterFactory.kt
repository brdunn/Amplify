package com.devdunnapps.amplify.data.networking

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (returnType !is ParameterizedType) return null

        val containerType = getParameterUpperBound(0, returnType)

        if (getRawType(returnType) == NetworkResponseCall::class.java)
            return NetworkResultCallAdapter<Any>(getParameterUpperBound(0, returnType))

        if (containerType !is ParameterizedType) return null

        if (getRawType(containerType) != NetworkResponse::class.java) return null

        return when (getRawType(returnType)) {
            Call::class.java -> {
                val successType = getParameterUpperBound(0, containerType)
                if (successType == Unit::class.java)
                    EmptyResponseBodyNetworkResponseCallAdapter()
                else
                    NetworkResultCallAdapter(successType)
            }
            else -> null
        }
    }

}

private class NetworkResultCallAdapter<T>(
    private val successType: Type
) : CallAdapter<T, Call<NetworkResponse<T>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<NetworkResponse<T>> = NetworkResponseCall(call)
}

private class EmptyResponseBodyNetworkResponseCallAdapter :
    CallAdapter<Unit, Call<NetworkResponse<Unit>>> {
    override fun responseType(): Type = Unit.javaClass

    override fun adapt(call: Call<Unit>): Call<NetworkResponse<Unit>> =
        NetworkResponseCall(
            originalCall = call,
            emptyBodyHandler = { responseCode ->
                if (responseCode in 200 until 300)
                    NetworkResponse.Success(Unit)
                else
                    NetworkResponse.Failure(code = responseCode)
            }
        )
}