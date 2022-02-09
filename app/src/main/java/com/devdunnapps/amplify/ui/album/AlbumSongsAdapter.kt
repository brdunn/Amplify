package com.devdunnapps.amplify.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.utils.TimeUtils.millisecondsToTime

class AlbumSongsAdapter(
    private val songs: List<Song>
) : RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textViewSongIndex: TextView = view.findViewById(R.id.album_song_index)
        val textViewSongTitle: TextView = view.findViewById(R.id.album_song_title)
        val textViewSongArtist: TextView = view.findViewById(R.id.album_song_artist)
        val imageViewMenu: ImageView = view.findViewById(R.id.album_song_menu)

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, bindingAdapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_album_song, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewSongIndex.text = (position + 1).toString()
        viewHolder.textViewSongTitle.text = songs[position].title
        val songDuration = millisecondsToTime(songs[position].duration)
        viewHolder.textViewSongArtist.text = songs[position].artistName + " • " + songDuration
        viewHolder.imageViewMenu.setOnClickListener {
            val action = MobileNavigationDirections.actionGlobalNavigationSongBottomSheet(songs[position].id)
            viewHolder.imageViewMenu.findNavController().navigate(action)
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
