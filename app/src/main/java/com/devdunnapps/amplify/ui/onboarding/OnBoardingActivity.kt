package com.devdunnapps.amplify.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.ui.navigation.AmplifyNavHost
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window,true)

        setContent {
            Mdc3Theme {
                AmplifyNavHost(
                    onFinishOnboarding = {
                        val mainActivityIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainActivityIntent)
                        finish()
                    }
                )
            }
        }
    }
}
