package com.devdunnapps.amplify.ui.utils

import androidx.annotation.StringRes
import com.devdunnapps.amplify.R
import java.util.Calendar

object Greeting {
    @StringRes fun create(): Int = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 2..11 -> R.string.greeting_morning
        in 12..16 -> R.string.greeting_afternoon
        in 17..20 -> R.string.greeting_evening
        else -> R.string.greeting_night
    }
}
