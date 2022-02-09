package com.devdunnapps.amplify.ui.addtoplaylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist

class AddToPlaylistListAdapter(
    private val playlists: List<Playlist>
) : RecyclerView.Adapter<AddToPlaylistListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val textViewSongTitle: TextView = view.findViewById(R.id.playlist_item_title)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_add_to_playlist, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewSongTitle.text = playlists[position].title
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun getItem(id: Int): Playlist {
        return playlists[id]
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
