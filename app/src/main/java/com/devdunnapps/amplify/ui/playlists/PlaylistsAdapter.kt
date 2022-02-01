package com.devdunnapps.amplify.ui.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: List<Playlist>
) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val textViewPlaylistTitle: TextView = view.findViewById(R.id.playlist_card_title)
        val textViewPlaylistNumTracks: TextView = view.findViewById(R.id.playlist_card_num_tracks)
        val playlistItemMenu: ImageView = view.findViewById(R.id.playlist_card_menu)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_playlist, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewPlaylistTitle.text = playlists[position].title

        val numSongs = playlists[position].numSongs.toString()
        viewHolder.textViewPlaylistNumTracks.text =  "$numSongs Songs"

        viewHolder.playlistItemMenu.setOnClickListener {
            val action = PlaylistsFragmentDirections.actionNavigationPlaylistsToPlaylistMenuBottomSheetFragment(playlists[position].id)
            viewHolder.playlistItemMenu.findNavController().navigate(action)
        }
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
