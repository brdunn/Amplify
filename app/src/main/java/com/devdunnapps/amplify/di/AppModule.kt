package com.devdunnapps.amplify.di

import android.content.ComponentName
import android.content.Context
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

    @Provides
    @Singleton
    fun providePlexTVAPI(): PlexTVAPI {
        return Retrofit.Builder()
            .baseUrl("https://plex.tv/")
            .addConverterFactory(GsonConverterFactory.create())
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
    fun provideUserToken(@ApplicationContext context: Context): String {
        return PreferencesUtils.readSharedSetting(context, PreferencesUtils.PREF_PLEX_USER_TOKEN) ?: ""
    }

    @Provides
    @Singleton
    fun providePlexRepository(api: PlexAPI, userToken: String, @Named("library") library: String): PlexRepository {
        return PlexRepositoryImpl(api, userToken, library)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext app: Context): PreferencesRepository = PreferencesRepositoryImpl(app)
}
