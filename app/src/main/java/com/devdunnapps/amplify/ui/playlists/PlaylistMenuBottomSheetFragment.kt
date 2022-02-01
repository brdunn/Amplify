package com.devdunnapps.amplify.ui.playlists

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentPlaylistBottomSheetBinding
import com.devdunnapps.amplify.databinding.FragmentSongBottomSheetBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.*

@AndroidEntryPoint
class PlaylistMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPlaylistBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBottomSheetBinding.inflate(inflater, container, false)

        viewModel.playlist.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val playlist = result.data!!
                binding.playlistBottomSheetTitle.text = playlist.title
            }
        }

        binding.playlistBottomSheetDelPlaylistBtn.setOnClickListener {
            viewModel.deletePlaylist()
            viewModel.isPlaylistDeletionComplete.observe(viewLifecycleOwner) { result ->
                if (result is Resource.Success) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("refreshData", true)
                    dismiss()
                }
            }
        }

        return binding.root
    }
}
