package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentSongBottomSheetBinding
import com.devdunnapps.amplify.domain.models.Rating
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.main.MainActivity
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSongBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var song: Song
    private val viewModel: SongMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSongBottomSheetBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.song.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            song = response.data!!
                            binding.songBottomSheetTitle.text = song.title
                            binding.songBottomSheetArtist.text = song.artistName

                            val url = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(song.thumb)
                            binding.songBottomSheetAlbumArt.load(url) {
                                error(R.drawable.ic_albums_black_24dp)
                            }

                            when (song.userRating) {
                                Rating.THUMB_UP -> {
                                    binding.songBottomSheetThumbDown.setImageResource(R.drawable.ic_thumb_down_24dp_outlined)
                                    binding.songBottomSheetThumbUp.setImageResource(R.drawable.ic_thumb_up_24dp)
                                }
                                Rating.THUMB_DOWN -> {
                                    binding.songBottomSheetThumbDown.setImageResource(R.drawable.ic_thumb_down_24dp)
                                    binding.songBottomSheetThumbUp.setImageResource(R.drawable.ic_thumb_up_24dp_outlined)
                                }
                                else -> {
                                    binding.songBottomSheetThumbDown.setImageResource(R.drawable.ic_thumb_down_24dp_outlined)
                                    binding.songBottomSheetThumbUp.setImageResource(R.drawable.ic_thumb_up_24dp_outlined)
                                }
                            }

                            binding.songBottomSheetThumbDown.setOnClickListener {
                                if (song.userRating == Rating.THUMB_DOWN) {
                                    viewModel.rateSong(Rating.THUMB_GONE)
                                } else {
                                    viewModel.rateSong(Rating.THUMB_DOWN)
                                }
                            }

                            binding.songBottomSheetThumbUp.setOnClickListener {
                                if (song.userRating == Rating.THUMB_UP) {
                                    viewModel.rateSong(Rating.THUMB_GONE)
                                } else {
                                    viewModel.rateSong(Rating.THUMB_UP)
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.songBottomSheetAlbumBtn.setOnClickListener {
            (activity as MainActivity).bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            val action = MobileNavigationDirections.actionGlobalNavigationAlbum(song.albumId)
            findNavController().navigate(action)
            dismiss()
        }

        binding.songBottomSheetArtistBtn.setOnClickListener {
            (activity as MainActivity).bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            val action = MobileNavigationDirections.actionGlobalNavigationArtist(song.artistId)
            findNavController().navigate(action)
            dismiss()
        }

        binding.songBottomSheetPlayNextBtn.setOnClickListener {
            viewModel.playSong(WhenToPlay.NEXT)
            dismiss()
        }

        binding.songBottomSheetAddToQueueBtn.setOnClickListener {
            viewModel.playSong(WhenToPlay.QUEUE)
            dismiss()
        }

        binding.songBottomSheetPlaylistBtn.setOnClickListener {
            val action =
                SongMenuBottomSheetFragmentDirections.actionNavigationSongBottomSheetToAddToPlaylistBottomSheet(song.id)
            findNavController().navigate(action)
        }

        binding.songBottomSheetLyricsBtn.setOnClickListener {
            val action = SongMenuBottomSheetFragmentDirections.actionNavigationSongBottomSheetToSongLyrics(
                song.id,
                song.title,
                song.artistName
            )
            findNavController().navigate(action)
        }

        binding.songBottomSheetInfoBtn.setOnClickListener {
            val action = SongMenuBottomSheetFragmentDirections
                .actionNavigationSongBottomSheetToSongAdditionalInfoBottomSheetFragment(song)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
