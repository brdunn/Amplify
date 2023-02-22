package com.devdunnapps.amplify.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devdunnapps.amplify.domain.models.LibrarySection
import com.devdunnapps.amplify.domain.models.Server
import com.devdunnapps.amplify.domain.models.User
import com.devdunnapps.amplify.domain.usecases.GetLibrarySectionsUseCase
import com.devdunnapps.amplify.domain.usecases.GetUsersServersUseCase
import com.devdunnapps.amplify.domain.usecases.SignInUserUseCase
import com.devdunnapps.amplify.utils.PreferencesUtils
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFlowViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val getUsersServersUseCase: GetUsersServersUseCase,
    private val getLibrarySectionsUseCase: GetLibrarySectionsUseCase,
    private val app: Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading)
    val user = _user.asStateFlow()

    private val _twoFactorAuthRequired = MutableStateFlow(false)
    val twoFactorAuthRequired = _twoFactorAuthRequired.asStateFlow()

    private val _servers = MutableStateFlow<Resource<List<Server>>>(Resource.Loading)
    val servers = _servers.asStateFlow()

    private val _libraries = MutableStateFlow<Resource<List<LibrarySection>>>(Resource.Loading)
    val libraries = _libraries.asStateFlow()

    fun login(username: String, password: String, token: String) {
        viewModelScope.launch {
            signInUserUseCase(username, password, token).collect {
                if (it is Resource.Success) {
                    PreferencesUtils.saveString(
                        app.applicationContext,
                        PreferencesUtils.PREF_PLEX_USER_TOKEN, it.data.authToken
                    )
                    _user.value = it
                    getServers()
                } else if (it is Resource.Error && it.message == "1029") {
                    _twoFactorAuthRequired.emit(true)
                } else {
                    _user.value = it
                }
            }
        }
    }

    private fun getServers() {
        viewModelScope.launch {
            getUsersServersUseCase().collect {
                _servers.value = it
            }
        }
    }

    fun selectServer(server: Server) {
        PreferencesUtils.saveString(app.applicationContext, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS, server.address)

        getLibraries()
    }

    private fun getLibraries() {
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
