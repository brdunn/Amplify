package com.devdunnapps.amplify.ui.utils

import androidx.compose.ui.Modifier

fun Modifier.whenTrue(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition) this.modifier() else this

fun <T : Any> Modifier.whenNotNull(value: T?, modifier: Modifier.(T) -> Modifier): Modifier =
    value?.let { this.modifier(it) } ?: this
