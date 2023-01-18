package com.devdunnapps.amplify.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.ui.navigation.AmplifyNavHost
import com.devdunnapps.amplify.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window,true)

        setContent {
            Theme {
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
