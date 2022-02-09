package com.devdunnapps.amplify.ui.artist.albums

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.utils.PlexUtils

class ArtistAllAlbumsListAdapter(
    private val albums: List<Album>,
    private val context: Context
) : RecyclerView.Adapter<ArtistAllAlbumsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val imageViewAlbumArtwork: ImageView = view.findViewById(R.id.album_card_artwork)
        val textView: TextView = view.findViewById(R.id.album_card_title)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_album_card, viewGroup, false)
        view.clipToOutline = true
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = albums[position].title
        val thumbKey = albums[position].thumb
        val imageUrl = PlexUtils.getInstance(context).addKeyAndAddress(thumbKey)
        Glide.with(viewHolder.imageViewAlbumArtwork)
            .load(imageUrl)
            .placeholder(R.drawable.ic_albums_black_24dp)
            .into(viewHolder.imageViewAlbumArtwork)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    fun getItemRatingKey(id: Int): String {
        return albums[id].id
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    companion object {
        private var mClickListener: ItemClickListener? = null
    }
}
