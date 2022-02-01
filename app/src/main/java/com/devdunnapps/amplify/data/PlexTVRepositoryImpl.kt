package com.devdunnapps.amplify.data

import android.content.Context
import com.devdunnapps.amplify.data.models.SigninDTO
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.domain.models.User
import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import com.devdunnapps.amplify.utils.PreferencesUtils
import com.devdunnapps.amplify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

class PlexTVRepositoryImpl @Inject constructor(
    private val api: PlexTVAPI,
    private val context: Context
): PlexTVRepository  {

    override fun getLibrarySections(): Flow<Resource<List<LibrarySection>>> = flow {
        emit(Resource.Loading())

        val url = PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS) ?: ""
        val userToken = PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_USER_TOKEN) ?: ""
        val libraryAPI = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlexAPI::class.java)
        try {
            val librarySections = libraryAPI.getLibrarySections(userToken).mediaContainer.directory!!
                .filter { it.type == "artist" }
                .map { it.toServer() }
            emit(Resource.Success(librarySections))
        } catch(e: HttpException) {
            emit(Resource.Error("Oops, something went wrong!"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't load library sections, please check your internet connection."))
        }
    }

    override fun signInUser(username: String, password: String, authToken: String?): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        val userCredentials = SigninDTO(username, password, authToken)
        val response = api.signInUser(userCredentials)
        if (response.code() == 201) {
            emit(Resource.Success(response.body()?.toUser()))
        } else {
            // TODO: error handling
            println("Failure: ${response.code()}")
            println(response.body())
            emit(Resource.Error("error"))
        }
    }

    override fun getUserServers(userToken: String): Flow<Resource<List<Server>>> = flow {
        emit(Resource.Loading())

        val resources = api.getServers(userToken).filter { it.provides == "server" }
        val servers: MutableList<Server> = mutableListOf()
        for (resource in resources) {
            for (connection in resource.connections) {
                servers.add(connection.toServer())
            }
        }
        emit(Resource.Success(servers.toList()))
    }
}
