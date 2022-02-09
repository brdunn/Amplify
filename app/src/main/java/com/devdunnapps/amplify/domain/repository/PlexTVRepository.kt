package com.devdunnapps.amplify.domain.repository

import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.domain.models.User
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlexTVRepository {

    fun getLibrarySections(): Flow<Resource<List<LibrarySection>>>

    fun signInUser(username: String, password: String, authToken: String?): Flow<Resource<User>>

    fun getUserServers(userToken: String): Flow<Resource<List<Server>>>
}
