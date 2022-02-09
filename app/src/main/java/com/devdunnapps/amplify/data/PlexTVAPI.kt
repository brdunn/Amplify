package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.models.ResourceDTO
import com.devdunnapps.amplify.data.models.SigninDTO
import com.devdunnapps.amplify.data.models.UserDTO
import retrofit2.Response
import retrofit2.http.*

interface PlexTVAPI {

    @POST("api/v2/users/signin")
    @Headers(
        "Accept: application/json",
        "X-Plex-Client-Identifier: Amplify"
    )
    suspend fun signInUser(
        @Body user: SigninDTO
    ): Response<UserDTO>

    @GET("api/v2/resources?includeHttps=1&includeRelay=1")
    @Headers(
        "Accept: application/json",
        "X-Plex-Client-Identifier: Amplify"
    )
    suspend fun getServers(
        @Header("X-Plex-Token") userToken: String
    ): List<ResourceDTO>
}
