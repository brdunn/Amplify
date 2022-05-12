package com.devdunnapps.amplify.ui.artist.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ItemAlbumCardBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils

class ArtistAllAlbumsListAdapter(
    private val albums: List<Album>,
    private val onClick: (Album) -> Unit
) : RecyclerView.Adapter<ArtistAllAlbumsListAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemAlbumCardBinding,
        private val onClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.albumCardTitle.text = album.title

            val imageUrl = PlexUtils.getInstance(binding.albumCardArtwork.context)
                .addKeyAndAddress(album.thumb)
            Glide.with(binding.albumCardArtwork)
                .load(imageUrl)
                .placeholder(R.drawable.ic_albums_black_24dp)
                .into(binding.albumCardArtwork)

            binding.root.setOnClickListener { onClick(album) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumCardBinding.inflate(LayoutInflater
                .from(viewGroup.context), viewGroup, false),
            onClick
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(albums[position])
    }

    override fun getItemCount() = albums.size
}
