package com.devdunnapps.amplify.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ItemArtistFragmentAlbumCardBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils

class ArtistTopAlbumsListAdapter(
    private val albums: List<Album>,
    private val onClick: (album: Album) -> Unit
) : RecyclerView.Adapter<ArtistTopAlbumsListAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemArtistFragmentAlbumCardBinding,
        private val onClick: (album: Album) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.artistAlbumCardTitle.text = album.title

            // TODO(handle the case of EPs and other non album items)
            val contentYear = album.year
            val subtitle = binding.artistAlbumCardSubtitle.context
                .resources.getString(R.string.artist_albums_subtitle, "Album", contentYear)
            binding.artistAlbumCardSubtitle.text = subtitle

            val imageUrl = PlexUtils
                .getInstance(binding.artistAlbumCardPicture.context).addKeyAndAddress(album.thumb)
            Glide.with(binding.artistAlbumCardPicture)
                .load(imageUrl)
                .error(R.drawable.ic_albums_black_24dp)
                .into(binding.artistAlbumCardPicture)

            binding.root.setOnClickListener { onClick(album) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding  = ItemArtistFragmentAlbumCardBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        return viewHolder.bind(albums[position])
    }

    override fun getItemCount() = albums.size
}
