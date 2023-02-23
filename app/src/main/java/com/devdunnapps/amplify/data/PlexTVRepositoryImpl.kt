package com.devdunnapps.amplify.data

import com.devdunnapps.amplify.data.models.ErrorsDTO
import com.devdunnapps.amplify.data.models.SigninDTO
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.domain.models.User
import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import com.devdunnapps.amplify.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class PlexTVRepositoryImpl @Inject constructor(
    private val plexTVClient: PlexTVAPI
): PlexTVRepository  {

    override fun signInUser(username: String, password: String, authToken: String?): Flow<Resource<User>> = flow {
        emit(Resource.Loading)

        try {
            val userCredentials = SigninDTO(username, password, authToken)
            val response = plexTVClient.signInUser(userCredentials)
            if (response.code() == HttpURLConnection.HTTP_CREATED) {
                val user = response.body()?.toUser() ?: run {
                    emit(Resource.Error())
                    return@flow
                }
                emit(Resource.Success(user))
            } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                val errors = Gson().fromJson(response.errorBody()?.charStream(), ErrorsDTO::class.java)
                if (errors.errors[0].code == 1029) {
                    emit(Resource.Error(errors.errors[0].code.toString()))
                } else {
                    emit(Resource.Error("Not authorized"))
                }
            } else {
                emit(Resource.Error("Error logging in"))
            }
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't sign in user, please check your internet connection."))
        }
    }

    override fun getUserServers(): Flow<Resource<List<Server>>> = flow {
        emit(Resource.Loading)

        val resources = plexTVClient.getServers().filter { it.provides == "server" }
        val servers: MutableList<Server> = mutableListOf()
        for (resource in resources) {
            for (connection in resource.connections) {
                servers.add(
                    connection.toServer(serverName = resource.name, accessToken = resource.accessToken.orEmpty())
                )
            }
        }
        emit(Resource.Success(servers.toList()))
    }
}
