package com.devdunnapps.amplify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.devdunnapps.amplify.databinding.ActivityMainBinding
import com.devdunnapps.amplify.ui.onboarding.OnBoardingActivity
import com.devdunnapps.amplify.ui.theme.Theme
import com.devdunnapps.amplify.utils.PreferencesUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding
    lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startOnBoardingIfFirstTime()

        drawUnderSystemBars()



        setContent {
            Theme {
                val playbackState = viewModel.playbackState.collectAsState().value.state
                val currentlyPlayingMetadata = viewModel.mediaMetadata.collectAsState().value

                AmplifyApp(
                    windowSizeClass = calculateWindowSizeClass(this),
                    playbackState = playbackState,
                    currentlyPlayingMetadata = currentlyPlayingMetadata
                )
            }
        }

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        drawUnderSystemBars()
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_content_frame) as NavHostFragment
//        binding.navView.apply {
//            val navController = navHostFragment.navController
//            setupWithNavController(navController)
//            setOnItemSelectedListener { item ->
//                if (selectedItemId == item.itemId)
//                    navController.popBackStack(item.itemId, false)
//
//                NavigationUI.onNavDestinationSelected(item, navController)
//                true
//            }
//        }
//
//        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet)
//
//        binding.nowPlayingBoxCollapsed.setContent {
//            Mdc3Theme {
//
//
//                if (currentlyPlayingMetadata != NOTHING_PLAYING) {
//                    NowPlayingCollapsed(
//                        albumArtUrl = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
//                        title = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
//                        subtitle = currentlyPlayingMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
//                        isPlaying = playbackState == PlaybackStateCompat.STATE_PLAYING,
//                        onPlayPauseClick = viewModel::togglePlaybackState,
//                        onSkipClick = viewModel::skipToNext
//                    )
//                }
//            }
//        }
//
//        binding.nowPlayingExpanded.setContent {
//            NowPlayingScreen(
//                onCollapseNowPlaying = {
//                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
//                },
//                onNowPlayingMenuClick = { songId ->
//                    val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songId)
//                    findNavController(binding.navContentFrame).navigate(action)
//                }
//            )
//        }
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.playbackState.collect {
//                    if (it.state == PlaybackStateCompat.STATE_PLAYING) {
//                        val marginInDp =
//                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64f, resources.displayMetrics).toInt()
//                        (binding.navContentFrame.layoutParams as CoordinatorLayout.LayoutParams).setMargins(0, 0, 0, marginInDp)
//
//                        binding.bottomSheet.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//
//        bottomSheet.isGestureInsetBottomIgnored = true
//        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        binding.navView.visibility = View.GONE
//                        binding.nowPlayingBoxCollapsed.visibility = View.INVISIBLE
//                    }
//
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                        binding.nowPlayingExpanded.visibility = View.INVISIBLE
//                    }
//
//                    else -> Unit
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                binding.navView.visibility = View.VISIBLE
//                binding.nowPlayingBoxCollapsed.visibility = View.VISIBLE
//                binding.nowPlayingExpanded.visibility = View.VISIBLE
//                binding.nowPlayingExpanded.alpha = slideOffset
//                binding.nowPlayingBoxCollapsed.alpha = 1 - slideOffset
//
//                // TODO: animate the navigation view off the page
//            }
//        })
//
//        binding.bottomSheet.setOnClickListener {
//            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
//        }
    }

    /**
     * Draws the main content under the system bars for Android versions 11+
     */
    private fun drawUnderSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun startOnBoardingIfFirstTime() {
        val sharedPref = getSharedPreferences(PreferencesUtils.PREFERENCES_FILE, MODE_PRIVATE)
        val isUserFirstTime = sharedPref.getBoolean(PreferencesUtils.PREF_USER_FIRST_TIME, true)
        if (isUserFirstTime) {
            val onBoardingIntent = Intent(this, OnBoardingActivity::class.java)
            startActivity(onBoardingIntent)
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val shouldOpenNowPlaying = intent.getBooleanExtra("launchNowPlaying", false)
        if (shouldOpenNowPlaying) {
//            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

//    override fun onBackPressed() {
//        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
//        } else {
//            super.onBackPressed()
//        }
//    }
}

@Composable
internal fun NowPlayingCollapsed(
    albumArtUrl: String,
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onPlayPauseClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .requiredHeight(64.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        AsyncImage(
            model = albumArtUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(48.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }

        IconButton(onClick = onPlayPauseClick) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = null
            )
        }

        IconButton(onClick = onSkipClick) {
            Icon(imageVector = Icons.Filled.SkipNext, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun NowPlayingCollapsedPreview() {
    Theme {
        NowPlayingCollapsed(
            albumArtUrl = "",
            title = "Vienna",
            subtitle = "Billy Joel",
            isPlaying = true,
            onPlayPauseClick = {},
            onSkipClick = {}
        )
    }
}
