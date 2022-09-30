package com.devdunnapps.amplify.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ItemAlbumCardBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils

class AlbumsListAdapter(
    private val albums: List<Album>,
    private val onClick: (Album) -> Unit
) : RecyclerView.Adapter<AlbumsListAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemAlbumCardBinding,
        private val onClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.albumCardTitle.text = album.title

            val imageUrl = PlexUtils
                .getInstance(binding.albumCardArtwork.context).addKeyAndAddress(album.thumb)
            binding.albumCardArtwork.load(imageUrl) {
                placeholder(R.drawable.ic_albums_black_24dp)
                error(R.drawable.ic_albums_black_24dp)
            }

            binding.root.setOnClickListener { onClick(album) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
            onClick
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(albums[position])
    }

    override fun getItemCount() = albums.size
}
