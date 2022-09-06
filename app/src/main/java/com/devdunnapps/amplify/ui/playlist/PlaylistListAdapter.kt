package com.devdunnapps.amplify.ui.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.PlexUtils

class PlaylistListAdapter(
    private val context: Context,
    private val playlistId: String,
    private val songs: List<Song>
) : RecyclerView.Adapter<PlaylistListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val imageViewArtwork: ImageView = view.findViewById(R.id.song_card_album_artwork)
        val textViewSongTitle: TextView = view.findViewById(R.id.song_card_title)
        val textViewArtistName: TextView = view.findViewById(R.id.song_card_artist)
        val imageViewMenu: ImageView = view.findViewById(R.id.song_card_menu)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_song, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewSongTitle.text = songs[position].title
        viewHolder.textViewArtistName.text = songs[position].artistName
        viewHolder.imageViewMenu.setOnClickListener {
            val action = PlaylistFragmentDirections.actionNavigationPlaylistToPlaylistSongMenuBottomSheetFragment(songs[position], playlistId)
            viewHolder.imageViewMenu.findNavController().navigate(action)
        }

        val thumbKey = songs[position].thumb
        val imageUrl = PlexUtils.getInstance(context).addKeyAndAddress(thumbKey)
        viewHolder.imageViewArtwork.load(imageUrl) {
            placeholder(R.drawable.ic_albums_black_24dp)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun getItem(id: Int): Song {
        return songs[id]
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
