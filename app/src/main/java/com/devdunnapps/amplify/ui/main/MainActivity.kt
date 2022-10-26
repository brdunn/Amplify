package com.devdunnapps.amplify.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import coil.load
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ActivityMainBinding
import com.devdunnapps.amplify.ui.about.AboutActivity
import com.devdunnapps.amplify.ui.nowplaying.NowPlayingFragment
import com.devdunnapps.amplify.ui.onboarding.OnBoardingActivity
import com.devdunnapps.amplify.ui.settings.SettingsActivity
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.devdunnapps.amplify.utils.PreferencesUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var sharedPref: SharedPreferences? = null
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        setUITheme()

        sharedPref = getSharedPreferences(PreferencesUtils.PREFERENCES_FILE, MODE_PRIVATE)
        startOnBoardingIfFirstTime()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawUnderSystemBars()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_content_frame) as NavHostFragment
        binding.navView.setupWithNavController(navHostFragment.navController)

        viewModel.playbackState.observe(this) {
            if (it.state == PlaybackStateCompat.STATE_PAUSED) {
                binding.nowPlayingBox.nowPlayingPlayPause.setImageResource(R.drawable.ic_play_24dp)
            } else if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                binding.nowPlayingBox.nowPlayingPlayPause.setImageResource(R.drawable.ic_pause_24dp)

                val marginInDp =
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, resources.displayMetrics).toInt()
                (binding.navContentFrame.layoutParams as CoordinatorLayout.LayoutParams).setMargins(0, 0, 0, marginInDp)

                binding.nowPlayingBox.bottomSheet.visibility = View.VISIBLE
            }
        }

        viewModel.mediaMetadata.observe(this) { metadata ->
            if (metadata != NOTHING_PLAYING) {
                binding.nowPlayingBox.nowPlayingBoxSong.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                binding.nowPlayingBox.nowPlayingBoxArtist.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

                val imageUrl = Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                binding.nowPlayingBox.nowPlayingAlbumArtwork.load(imageUrl) {
                    error(R.drawable.ic_albums_black_24dp)
                }
            }
        }

        binding.nowPlayingBox.nowPlayingPlayPause.setOnClickListener {
            viewModel.togglePlaybackState()
        }

        binding.nowPlayingBox.nowPlayingSkip.setOnClickListener {
            viewModel.skipToNext()
        }

        bottomSheet = BottomSheetBehavior.from(binding.nowPlayingBox.bottomSheet)
        bottomSheet.isGestureInsetBottomIgnored = true
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.navView.visibility = View.GONE
                        binding.nowPlayingBox.nowPlayingBoxSmall.visibility = View.INVISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.nowPlayingBox.fragmentContainerViewBottomSheet.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.navView.visibility = View.VISIBLE
                binding.nowPlayingBox.nowPlayingBoxSmall.visibility = View.VISIBLE
                binding.nowPlayingBox.fragmentContainerViewBottomSheet.visibility = View.VISIBLE
                binding.nowPlayingBox.fragmentContainerViewBottomSheet.alpha = slideOffset
                binding.nowPlayingBox.nowPlayingBoxSmall.alpha = 1 - slideOffset

                // TODO: animate the navigation view off the page
            }
        })

        binding.nowPlayingBox.bottomSheet.setOnClickListener {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        supportFragmentManager.commit {
            add(R.id.fragment_container_view_bottom_sheet, NowPlayingFragment())
        }
    }

    /**
     * Draws the main content under the system bars for Android versions 11+
     */
    private fun drawUnderSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main_activity_search -> {
                val action = MobileNavigationDirections.actionGlobalSearchFragment()
                findNavController(item.itemId).navigate(action)
                true
            }
            R.id.menu_main_activity_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            R.id.menu_main_activity_about -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startOnBoardingIfFirstTime() {
        val isUserFirstTime = sharedPref!!.getBoolean(PreferencesUtils.PREF_USER_FIRST_TIME, true)
        if (isUserFirstTime) {
            val onBoardingIntent = Intent(this, OnBoardingActivity::class.java)
            startActivity(onBoardingIntent)
            finish()
        }
    }

    private fun setUITheme() {
        val darkModeValues = resources.getStringArray(R.array.theme_values)
        when (sharedPref!!.getString("theme", darkModeValues[2])) {
            "MODE_NIGHT_YES" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "MODE_NIGHT_NO" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "MODE_NIGHT_FOLLOW_SYSTEM" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(this, R.id.nav_content_frame).navigateUp() or super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val shouldOpenNowPlaying = intent.getBooleanExtra("launchNowPlaying", false)
        if (shouldOpenNowPlaying) {
            BottomSheetBehavior.from(binding.nowPlayingBox.bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onBackPressed() {
        val bottomSheet = BottomSheetBehavior.from(binding.nowPlayingBox.bottomSheet)
        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
