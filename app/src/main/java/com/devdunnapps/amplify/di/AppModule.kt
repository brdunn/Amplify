package com.devdunnapps.amplify.di

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.devdunnapps.amplify.BuildConfig
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.data.*
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
    fun provideMusicServiceConnection(@ApplicationContext context: Context): MusicServiceConnection {
        return MusicServiceConnection(
            context,
            ComponentName(context, MusicService::class.java)
        )
    }

    fun getOKHTTPClient(context: Context): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("X-Plex-Product", context.getString(R.string.app_name))
                .addHeader("X-Plex-Version", BuildConfig.VERSION_NAME)
                .addHeader("X-Plex-Platform", "Android")
                .addHeader("X-Plex-Platform-Version", Build.VERSION.SDK_INT.toString())
                .addHeader("X-Plex-Device", Build.MODEL)
                .addHeader("X-Plex-Device-Name", Settings.Global.getString(context.contentResolver, "device_name"))
                .addHeader("X-Plex-Client-Identifier", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
                .build()
            it.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun providePlexTVAPI(@ApplicationContext context: Context): PlexTVAPI {
        return Retrofit.Builder()
            .baseUrl("https://plex.tv/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOKHTTPClient(context))
            .build()
            .create(PlexTVAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLibraryRepository(api: PlexTVAPI, @ApplicationContext context: Context): PlexTVRepository {
        return PlexTVRepositoryImpl(api, context)
    }

    @Provides
    @Singleton
    fun providePlexAPI(@ApplicationContext context: Context): PlexAPI {
        val url = PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_SERVER_ADDRESS) ?: ""
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOKHTTPClient(context))
            .build()
            .create(PlexAPI::class.java)
    }

    @Provides
    @Singleton
    @Named("library")
    fun provideLibrary(@ApplicationContext context: Context): String {
        return PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_SERVER_LIBRARY) ?: ""
    }

    @Provides
    @Singleton
    @Named("plexToken")
    fun provideUserToken(@ApplicationContext context: Context): String {
        return PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_USER_TOKEN) ?: ""
    }

    @Provides
    @Singleton
    fun providePlexRepository(api: PlexAPI, @Named("plexToken") userToken: String, @Named("library") library: String): PlexRepository {
        return PlexRepositoryImpl(api, userToken, library)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext app: Context): PreferencesRepository = PreferencesRepositoryImpl(app)
}
