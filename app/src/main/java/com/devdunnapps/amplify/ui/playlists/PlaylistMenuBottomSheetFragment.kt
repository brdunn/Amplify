package com.devdunnapps.amplify.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.databinding.FragmentPlaylistBottomSheetBinding
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPlaylistBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBottomSheetBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlist.collect { result ->
                    if (result is Resource.Success) {
                        val playlist = result.data!!
                        binding.playlistBottomSheetTitle.text = playlist.title
                    }
                }
            }
        }

        binding.playlistBottomSheetDelPlaylistBtn.setOnClickListener {
            deleteConfirmationDialog()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete this playlist?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deletePlaylist()
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.closeObservable.collect {
                            dialog.dismiss()
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("refreshData", true)
                            dismiss()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
