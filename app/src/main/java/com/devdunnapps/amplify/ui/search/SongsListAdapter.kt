package com.devdunnapps.amplify.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ItemSongBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils

class SongsListAdapter(
    private val songs: List<Song>,
    private val onClick: (Song) -> Unit
) : RecyclerView.Adapter<SongsListAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemSongBinding,
        private val onClick: (Song) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.songCardTitle.text = song.title
            binding.songCardArtist.text = song.artistName

            binding.songCardMenu.setOnClickListener {
                val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(song.id)
                binding.songCardMenu.findNavController().navigate(action)
            }

            val imageUrl = PlexUtils.getInstance(binding.songCardAlbumArtwork.context)
                .getSizedImage(song.thumb, 200, 200)
            binding.songCardAlbumArtwork.load(imageUrl) {
                placeholder(R.drawable.ic_albums_black_24dp)
                error(R.drawable.ic_albums_black_24dp)
            }

            binding.root.setOnClickListener { onClick(song) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(songs[position])
    }

    override fun getItemCount() = songs.size
}
