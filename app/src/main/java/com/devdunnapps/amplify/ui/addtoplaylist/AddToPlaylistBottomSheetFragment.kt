package com.devdunnapps.amplify.ui.addtoplaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devdunnapps.amplify.databinding.FragmentAddToPlaylistBottomSheetBinding
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToPlaylistBottomSheetFragment : BottomSheetDialogFragment(),
    AddToPlaylistListAdapter.ItemClickListener {

    private var _binding: FragmentAddToPlaylistBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var playlistsAdapter: AddToPlaylistListAdapter? = null
    private val viewModel: AddToPlaylistViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       _binding = FragmentAddToPlaylistBottomSheetBinding.inflate(inflater, container, false)

        viewModel.playlists.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Resource.Success -> {
                    val playlists = result.data!!
                    playlistsAdapter = AddToPlaylistListAdapter(playlists)
                    playlistsAdapter!!.setClickListener(this@AddToPlaylistBottomSheetFragment)
                    binding.addToPlaylistLists.layoutManager = LinearLayoutManager(activity)
                    binding.addToPlaylistLists.adapter = playlistsAdapter
                }
                is Resource.Error -> {
                    Toast.makeText(activity, "Error getting playlists", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return binding.root
    }

    override fun onItemClick(view: View?, position: Int) {
        viewModel.addSongToPlaylist(playlistsAdapter!!.getItem(position).id)
        viewModel.isSongAddedComplete.observe(viewLifecycleOwner, { result ->
            if (result is Resource.Success) {
                dismiss()
            }
        })
    }
}
