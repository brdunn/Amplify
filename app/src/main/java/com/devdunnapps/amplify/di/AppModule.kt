package com.devdunnapps.amplify.di

import android.content.ComponentName
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.devdunnapps.amplify.UserPreferences
import com.devdunnapps.amplify.data.*
import com.devdunnapps.amplify.data.networking.PlexTVAuthorizationInterceptor
import com.devdunnapps.amplify.data.networking.DeviceInfoInterceptor
import com.devdunnapps.amplify.data.networking.PMSAuthorizationInterceptor
import com.devdunnapps.amplify.data.networking.PMSServerURLInterceptor
import com.devdunnapps.amplify.data.networking.UnauthorizedInterceptor
import com.devdunnapps.amplify.domain.repository.PlexRepository
import com.devdunnapps.amplify.domain.repository.PlexTVRepository
import com.devdunnapps.amplify.domain.repository.PreferencesRepository
import com.devdunnapps.amplify.utils.MusicService
import com.devdunnapps.amplify.utils.MusicServiceConnection
import com.devdunnapps.amplify.utils.PreferencesUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMusicServiceConnection(@ApplicationContext context: Context): MusicServiceConnection =
        MusicServiceConnection(context, ComponentName(context, MusicService::class.java))

    @Provides
    fun providePlexTVAPI(@ApplicationContext context: Context): PlexTVAPI =
        Retrofit.Builder()
            .baseUrl("https://plex.tv/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(PlexTVAuthorizationInterceptor(context))
                    .addInterceptor(DeviceInfoInterceptor(context))
                    .addInterceptor(UnauthorizedInterceptor(context))
                    .build()
            )
            .build()
            .create(PlexTVAPI::class.java)

    @Provides
    fun providePlexTVRepository(plexTVAPI: PlexTVAPI): PlexTVRepository = PlexTVRepositoryImpl(plexTVAPI)

    @Provides
    fun providePlexAPI(@ApplicationContext context: Context): PlexAPI {
        return Retrofit.Builder()
            .baseUrl("https://localhost")  // Dummy value that will be replaced with an interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(PMSServerURLInterceptor(context))
                    .addInterceptor(PMSAuthorizationInterceptor(context))
                    .addInterceptor(DeviceInfoInterceptor(context))
                    .addInterceptor(UnauthorizedInterceptor(context))
                    .build()
            )
            .build()
            .create(PlexAPI::class.java)
    }

    @Provides
    @Named("library")
    fun provideLibrary(@ApplicationContext context: Context): String =
        PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY).orEmpty()

    @Provides
    fun providePlexRepository(api: PlexAPI, @Named("library") library: String): PlexRepository =
        PlexRepositoryImpl(api, library)

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<UserPreferences> =
        DataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            serializer = UserPreferencesSerializer()
        ) {
            context.dataStoreFile("preferences.pb")
        }

    @Provides
    @Singleton
    fun providePreferencesRepository(preferencesDataStore: DataStore<UserPreferences>): PreferencesRepository =
        PreferencesRepositoryImpl(preferences = preferencesDataStore)
}
