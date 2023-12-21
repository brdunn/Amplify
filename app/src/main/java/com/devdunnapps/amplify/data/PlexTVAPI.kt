package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.models.ResourceDTO
import com.devdunnapps.amplify.data.models.SignInDTO
import com.devdunnapps.amplify.data.models.SignInResponseDTO
import com.devdunnapps.amplify.data.models.UserDTO
import com.devdunnapps.amplify.data.networking.NetworkResponse
import retrofit2.Response
import retrofit2.http.*

interface PlexTVAPI {

    @POST("api/v2/users/signin")
    @Headers("X-Amplify-Ignore-Auth-Errors: 1")
    suspend fun signInUser(@Body user: SignInDTO): Response<SignInResponseDTO>

    @GET("api/v2/resources?includeHttps=1&includeRelay=1")
    suspend fun getServers(): List<ResourceDTO>

    @GET("api/v2/user")
    suspend fun getUser(): NetworkResponse<UserDTO>
}
