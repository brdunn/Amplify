package com.devdunnapps.amplify.domain.repository

interface PreferencesRepository {

    suspend fun write(key: String, value: String)

    suspend fun writeBoolean(key: String, value: Boolean)

    suspend fun read(key: String): String?

    suspend fun readBoolean(key: String): Boolean
}
