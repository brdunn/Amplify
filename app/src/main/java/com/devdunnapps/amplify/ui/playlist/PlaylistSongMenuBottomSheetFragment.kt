package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentPlaylistSongBottomSheetBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistSongMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPlaylistSongBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var song: Song
    private val viewModel: PlaylistSongBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       _binding = FragmentPlaylistSongBottomSheetBinding.inflate(inflater, container, false)

        song = requireArguments().getSerializable("song") as Song

        binding.playlistBottomSheetTitle.text = song.title
        binding.songBottomSheetArtist.text = song.artistName

        val url = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(song.thumb)
        Glide.with(binding.playlistBottomSheetAlbumArt)
            .load(url)
            .error(R.drawable.ic_albums_black_24dp)
            .into(binding.playlistBottomSheetAlbumArt)

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
            viewModel.removeSongFromPlaylist()
            viewModel.isSongRemovedComplete.observe(viewLifecycleOwner) { result ->
                if (result is Resource.Success) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("refreshData", true)
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
