package com.devdunnapps.amplify.ui.nowplaying

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentNowPlayingBinding
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.NOTHING_PLAYING
import com.devdunnapps.amplify.utils.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private var _binding: FragmentNowPlayingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NowPlayingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playbackState.observe(viewLifecycleOwner) {
            if (it.state == PlaybackStateCompat.STATE_PAUSED) {
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_24dp)
            } else {
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_24dp)
            }
        }

        viewModel.metadata.observe(viewLifecycleOwner) {
            if (it != NOTHING_PLAYING) {
                loadCurrentMetadata(it)
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.shuffleMode.collect { shuffleMode ->
                        when (shuffleMode) {
                            PlaybackStateCompat.SHUFFLE_MODE_NONE ->
                                binding.shuffleBtn.setImageResource(R.drawable.ic_shuffle_off_36dp)
                            PlaybackStateCompat.SHUFFLE_MODE_ALL ->
                                binding.shuffleBtn.setImageResource(R.drawable.ic_shuffle_on_36dp)
                        }
                    }
                }

                launch {
                    viewModel.repeatMode.collect { repeatMode ->
                        when (repeatMode) {
                            PlaybackStateCompat.REPEAT_MODE_NONE ->
                                binding.repeatBtn.setImageResource(R.drawable.ic_repeat_none_36dp)
                            PlaybackStateCompat.REPEAT_MODE_ONE ->
                                binding.repeatBtn.setImageResource(R.drawable.ic_repeat_one_36dp)
                            PlaybackStateCompat.REPEAT_MODE_ALL ->
                                binding.repeatBtn.setImageResource(R.drawable.ic_repeat_all_36dp)
                        }
                    }
                }

                launch {
                    viewModel.mediaPosition.collect { position ->
                        binding.seekBar.value = (position / 1000).toFloat()
                        binding.curPosition.text = TimeUtils.millisecondsToTime(position)
                    }
                }
            }
        }

        binding.seekBar.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) = Unit

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.seekTo((slider.value * 1000).toLong())
            }
        })

        binding.playPauseBtn.setOnClickListener { viewModel.togglePlayingState() }
        binding.prevBtn.setOnClickListener { viewModel.skipToPrevious() }
        binding.skipBtn.setOnClickListener { viewModel.skipToNext() }
        binding.shuffleBtn.setOnClickListener { viewModel.toggleShuffleState() }
        binding.repeatBtn.setOnClickListener { viewModel.toggleRepeatState() }

        binding.songMenu.setOnClickListener {
            val songId = viewModel.metadata.value!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songId)
            findNavController().navigate(action)
        }

        binding.collapseBtn.setOnClickListener {
            (requireActivity() as MainActivity).bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCurrentMetadata(metadata: MediaMetadataCompat) {
        // set song title and artist
        binding.songTitle.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        binding.songArtist.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

        // load album art
        val imageURI = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
        val request = ImageRequest.Builder(requireContext())
            .data(imageURI)
            .target { drawable ->
                binding.albumArtwork.setImageDrawable(drawable)
                setColors(drawable)
            }
            .build()
        ImageLoader(requireContext()).enqueue(request)

        // set time markers
        val songDurationMillis = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        binding.curPosition.text = getString(R.string.beginning_of_song_timestamp)
        binding.totalDuration.text = TimeUtils.millisecondsToTime(songDurationMillis)

        // set progressbar
        binding.seekBar.value = 0F
        binding.seekBar.valueTo = (songDurationMillis / 1000).toFloat()
    }

    /**
     * Sets the colors for the UI based on the album picture
     */
    private fun setColors(albumArtwork: Drawable) {
        // background colors
        val artworkBitmap = (albumArtwork as BitmapDrawable).bitmap
        val palette = Palette.from(artworkBitmap).generate()
        val backgroundColor = palette.getMutedColor(Color.BLACK)
        binding.nowPlayingBackground.setBackgroundColor(backgroundColor)

        // text and skip/back colors
        val darkMutedSwatch = palette.darkMutedSwatch
        if (darkMutedSwatch != null) {
            val colorOnBackground = darkMutedSwatch.bodyTextColor
            binding.songTitle.setTextColor(colorOnBackground)
            binding.songArtist.setTextColor(colorOnBackground)
            binding.curPosition.setTextColor(colorOnBackground)
            binding.totalDuration.setTextColor(colorOnBackground)
            binding.shuffleBtn.setColorFilter(colorOnBackground)
            binding.prevBtn.setColorFilter(colorOnBackground)
            binding.skipBtn.setColorFilter(colorOnBackground)
            binding.repeatBtn.setColorFilter(colorOnBackground)
            binding.collapseBtn.setColorFilter(colorOnBackground)
        }

        // fab and seek bar color
        val vibrantColor = palette.getVibrantColor(Color.BLACK)
        binding.playPauseBtn.backgroundTintList = ColorStateList.valueOf(vibrantColor)
        binding.seekBar.trackActiveTintList = ColorStateList.valueOf(vibrantColor)
        binding.seekBar.thumbTintList = ColorStateList.valueOf(vibrantColor)
    }
}
