package com.devdunnapps.amplify.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.ItemArtistCardBinding
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.utils.PlexUtils

class ArtistsListAdapter(
    private val artists: List<Artist>,
    private val onClick: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistsListAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemArtistCardBinding,
        private val onClick: (Artist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.artistCardArtist.text = artist.name

            val imageUrl = PlexUtils.getInstance(binding.artistCardPicture.context)
                .addKeyAndAddress(artist.thumb)
            binding.artistCardPicture.load(imageUrl) {
                placeholder(R.drawable.ic_albums_black_24dp)
            }

            binding.root.setOnClickListener { onClick(artist) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtistCardBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false),
            onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(artists[position])
    }

    override fun getItemCount() = artists.size
}
