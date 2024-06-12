/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devdunnapps.amplify.ui.utils

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultBackgroundColor: Color = MaterialTheme.colorScheme.background,
    defaultOnBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    defaultPrimaryContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    defaultOnPrimaryContainerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true }
): DarkMutedColorState = remember {
    DarkMutedColorState(
        context,
        defaultBackgroundColor,
        defaultOnBackgroundColor,
        defaultPrimaryContainerColor,
        defaultOnPrimaryContainerColor,
        cacheSize,
        isColorValid
    )
}

/**
 * A composable which allows dynamic theming of the [androidx.compose.material.Colors.primary]
 * color from an image.
 */
@Composable
fun DynamicThemePrimaryColorsFromImage(
    darkMutedColorState: DarkMutedColorState = rememberDominantColorState(),
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme.copy(
        background = animateColorAsState(
            darkMutedColorState.backgroundColor,
            spring(stiffness = Spring.StiffnessLow)
        ).value,
        onBackground = animateColorAsState(
            darkMutedColorState.onBackgroundColor,
            spring(stiffness = Spring.StiffnessLow)
        ).value,
        primaryContainer = animateColorAsState(
            darkMutedColorState.primaryContainerColor,
            spring(stiffness = Spring.StiffnessLow)
        ).value,
        onPrimaryContainer = animateColorAsState(
            darkMutedColorState.onPrimaryContainerColor,
            spring(stiffness = Spring.StiffnessLow)
        ).value
    )
    MaterialTheme(colorScheme = colors, content = content)
}

/**
 * A class which stores and caches the result of any calculated dominant colors
 * from images.
 *
 * @param context Android context
 * @param cacheSize The size of the [LruCache] used to store recent results. Pass `0` to
 * disable the cache.
 * @param isColorValid A lambda which allows filtering of the calculated image colors.
 */
@Stable
class DarkMutedColorState(
    private val context: Context,
    private val defaultBackgroundColor: Color,
    private val defaultOnBackgroundColor: Color,
    private val defaultPrimaryContainerColor: Color,
    private val defaultOnPrimaryContainerColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true }
) {
    var backgroundColor by mutableStateOf(defaultBackgroundColor)
        private set
    var onBackgroundColor by mutableStateOf(defaultOnBackgroundColor)
        private set
    var primaryContainerColor by mutableStateOf(defaultPrimaryContainerColor)
        private set
    var onPrimaryContainerColor by mutableStateOf(defaultOnPrimaryContainerColor)
        private set

    private val cache = when {
        cacheSize > 0 -> LruCache<String, DarkMutedColors>(cacheSize)
        else -> null
    }

    suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDarkMutedColor(url)
        backgroundColor = result?.backgroundColor ?: defaultBackgroundColor
        onBackgroundColor = result?.onBackgroundColor ?: defaultOnBackgroundColor
        primaryContainerColor = result?.primaryContainerColor ?: defaultPrimaryContainerColor
        onPrimaryContainerColor = result?.onPrimaryContainerColor ?: defaultOnPrimaryContainerColor
    }

    private suspend fun calculateDarkMutedColor(url: String): DarkMutedColors? {
        val cached = cache?.get(url)
        if (cached != null) {
            // If we already have the result cached, return early now...
            return cached
        }

        // Otherwise we calculate the swatches in the image, and return the first valid color
        val palette = calculatePaletteForImage(context, url)

        val darkMutedSwatch = palette?.darkMutedSwatch ?: return null
        val vibrantSwatch = palette.vibrantSwatch ?: return null

        return DarkMutedColors(
            backgroundColor = Color(darkMutedSwatch.rgb),
            onBackgroundColor = Color(darkMutedSwatch.bodyTextColor),
            primaryContainerColor = Color(vibrantSwatch.rgb),
            onPrimaryContainerColor = Color(vibrantSwatch.bodyTextColor)
        ).also { result -> cache?.put(url, result) }
    }

    /**
     * Reset the color values to [defaultPrimaryContainerColor].
     */
    fun reset() {
        backgroundColor = defaultBackgroundColor
        onBackgroundColor = defaultOnBackgroundColor
        primaryContainerColor = defaultPrimaryContainerColor
        onPrimaryContainerColor = defaultOnPrimaryContainerColor
    }
}

@Immutable
private data class DarkMutedColors(
    val backgroundColor: Color,
    val onBackgroundColor: Color,
    val primaryContainerColor: Color,
    val onPrimaryContainerColor: Color
)

/**
 * Fetches the given [imageUrl] with Coil, then uses [Palette] to calculate the dominant color.
 */
private suspend fun calculatePaletteForImage(
    context: Context,
    imageUrl: String
): Palette? {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        // We scale the image to cover 128px x 128px (i.e. min dimension == 128px)
        .size(128).scale(Scale.FILL)
        // Disable hardware bitmaps, since Palette uses Bitmap.getPixels()
        .allowHardware(false)
        // Set a custom memory cache key to avoid overwriting the displayed image in the cache
        .memoryCacheKey("$imageUrl.palette")
        .build()

    val bitmap = when (val result = context.imageLoader.execute(request)) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }

    return bitmap?.let {
        withContext(Dispatchers.Default) {
            Palette.Builder(bitmap)
                // Disable any bitmap resizing in Palette. We've already loaded an appropriately
                // sized bitmap through Coil
                .resizeBitmapArea(0)
                // Clear any built-in filters. We want the unfiltered dominant color
                .clearFilters()
                // We reduce the maximum color count down to 8
                .maximumColorCount(8)
                .generate()
        }
    }
}
