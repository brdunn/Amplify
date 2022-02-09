package com.devdunnapps.amplify.ui.artists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Artist
import com.devdunnapps.amplify.utils.PlexUtils

class ArtistsListAdapter(
    private val artists: List<Artist>,
    private val context: Context
) : RecyclerView.Adapter<ArtistsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val textViewArtistName: TextView = view.findViewById(R.id.artist_card_artist)
        val imageViewArtistThumb: ImageView = view.findViewById(R.id.artist_card_picture)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_artist_card, viewGroup, false)
        view.clipToOutline = true
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewArtistName.text = artists[position].name

        val imageUrl = PlexUtils.getInstance(context).addKeyAndAddress(artists[position].thumb)
        Glide.with(viewHolder.imageViewArtistThumb)
            .load(imageUrl)
            .placeholder(R.drawable.ic_artists_black_24dp)
            .into(viewHolder.imageViewArtistThumb)
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    fun getItem(id: Int): String {
        return artists[id].id
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
