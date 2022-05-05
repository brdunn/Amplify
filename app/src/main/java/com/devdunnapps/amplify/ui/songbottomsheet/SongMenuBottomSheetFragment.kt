package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentSongBottomSheetBinding
import com.devdunnapps.amplify.domain.models.Rating
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSongBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var song: Song
    private val viewModel: SongMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       _binding = FragmentSongBottomSheetBinding.inflate(inflater, container, false)

        viewModel.song.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    song = response.data!!
                    binding.songBottomSheetTitle.text = song.title
                    binding.songBottomSheetArtist.text = song.artistName

                    val url = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(song.thumb)
                    Glide.with(binding.songBottomSheetAlbumArt)
                        .load(url)
                        .error(R.drawable.ic_albums_black_24dp)
                        .into(binding.songBottomSheetAlbumArt)

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

        binding.songBottomSheetAlbumBtn.setOnClickListener {
            val action = MobileNavigationDirections.actionGlobalNavigationAlbum(song.albumId)
            findNavController().navigate(action)
            dismiss()
        }

        binding.songBottomSheetArtistBtn.setOnClickListener {
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
            dismiss()
            val action = MobileNavigationDirections.actionGlobalNavigationAddToPlaylistBottomSheet(song.id)
            findNavController().navigate(action)
        }

        binding.songBottomSheetInfoBtn.setOnClickListener {
            dismiss()
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
