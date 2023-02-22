package com.devdunnapps.amplify.ui.utils

import androidx.compose.ui.Modifier

fun Modifier.whenTrue(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition) this.modifier() else this
