package com.devdunnapps.amplify.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.ItemPlaylistBinding
import com.devdunnapps.amplify.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemPlaylistBinding,
        private val onClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistCardTitle.text = playlist.title

            val numSongs = playlist.numSongs.toString()
            binding.playlistCardNumTracks.text =  "$numSongs Songs"

            binding.playlistCardMenu.setOnClickListener {
                val action = MobileNavigationDirections.actionGlobalPlaylistMenuBottomSheetFragment(playlist.id)
                binding.playlistCardMenu.findNavController().navigate(action)
            }

            binding.root.setOnClickListener { onClick(playlist) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size
}
