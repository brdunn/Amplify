package com.devdunnapps.amplify.ui.onboarding

import android.app.Application
import androidx.lifecycle.*
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.domain.models.User
import com.devdunnapps.amplify.domain.usecases.GetLibrarySectionsUseCase
import com.devdunnapps.amplify.domain.usecases.GetUsersServersUseCase
import com.devdunnapps.amplify.domain.usecases.SignInUserUseCase
import com.devdunnapps.amplify.utils.PreferencesUtils
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFlowViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val getUsersServersUseCase: GetUsersServersUseCase,
    private val getLibrarySectionsUseCase: GetLibrarySectionsUseCase,
    private val app: Application
): AndroidViewModel(app) {

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    private val _servers = MutableLiveData<Resource<List<Server>>>()
    val servers: LiveData<Resource<List<Server>>> = _servers

    private val _server = MutableLiveData<Resource<Server>>()
    val server: LiveData<Resource<Server>> = _server

    private val _libraries = MutableLiveData<Resource<List<LibrarySection>>>()
    val libraries: LiveData<Resource<List<LibrarySection>>> = _libraries

    private val _library = MutableLiveData<Resource<LibrarySection>>()
    val library: LiveData<Resource<LibrarySection>> = _library

    fun login(username: String, password: String, token: String) {
        viewModelScope.launch {
            signInUserUseCase(username, password, token).collect {
                if (it is Resource.Success) {
                    PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_USER_TOKEN, it.data!!.authToken)
                    _user.value = it
                    getServers()
                } else {
                    _user.value = it
                }
            }
        }
    }

    fun getServers() {
        viewModelScope.launch {
            getUsersServersUseCase(_user.value!!.data!!.authToken).collect {
                _servers.value = it
            }
        }
    }

    fun selectServer(server: Server) {
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, server.address)
        _server.value = Resource.Success(server)
    }

    fun getLibraries() {
        viewModelScope.launch {
            getLibrarySectionsUseCase().collect {
                _libraries.value = it
            }
        }
    }

    fun selectLibrary(libraryKey: String) {
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY, libraryKey)
        PreferencesUtils.saveBoolean(app.applicationContext, PreferencesUtils.PREF_USER_FIRST_TIME, false)
    }
}
