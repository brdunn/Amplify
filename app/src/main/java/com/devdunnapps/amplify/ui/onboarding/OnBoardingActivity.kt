package com.devdunnapps.amplify.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private var currentPage = 0
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var indicators: Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window,true)

        indicators = arrayOf(binding.introIndicator0, binding.introIndicator1, binding.introIndicator2, binding.introIndicator3)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentPage != 0) {
            updateIndicators { it - 1 }
        }
    }

    fun updateIndicators(onPageChanged: (Int) -> Int) {
        currentPage = onPageChanged(currentPage)
        for (i in indicators.indices) {
            indicators[i].setBackgroundResource(if (i == currentPage) R.drawable.indicator_selected else R.drawable.indicator_unselected)
        }
    }
}
