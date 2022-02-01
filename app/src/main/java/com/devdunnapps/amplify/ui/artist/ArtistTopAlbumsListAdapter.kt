package com.devdunnapps.amplify.ui.artist

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
import com.devdunnapps.amplify.utils.StringUtils

class ArtistTopAlbumsListAdapter(
    private val albums: List<Album>,
    private val context: Context
) : RecyclerView.Adapter<ArtistTopAlbumsListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val textViewAlbumTitle: TextView = view.findViewById(R.id.artist_album_card_title)
        val textViewAlbumSubtitle: TextView = view.findViewById(R.id.artist_album_card_subtitle)
        val imageViewAlbumArtwork: ImageView = view.findViewById(R.id.artist_album_card_picture)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_artist_fragment_album_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewAlbumTitle.text = albums[position].title

        // TODO("Does this this need to handle the case of EPs and other non album items?")
        val contentType = StringUtils.toTitleCase("album")
        val contentYear = albums[position].year
        viewHolder.textViewAlbumSubtitle.text = context.resources.getString(R.string.artist_albums_subtitle, contentType, contentYear)

        val imageUrl = PlexUtils.getInstance(context).addKeyAndAddress(albums[position].thumb)
        Glide.with(viewHolder.imageViewAlbumArtwork.context)
            .load(imageUrl)
            .error(R.drawable.ic_albums_black_24dp)
            .into(viewHolder.imageViewAlbumArtwork)
    }

    override fun getItemCount() = albums.size

    fun getItem(id: Int) = albums[id].id

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
