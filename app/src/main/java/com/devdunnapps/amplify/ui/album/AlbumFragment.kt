package com.devdunnapps.amplify.ui.album

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentAlbumBinding
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.TimeUtils
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : Fragment(), AlbumSongsAdapter.ItemClickListener, View.OnClickListener {

    private var albumSongsAdapter: AlbumSongsAdapter? = null
    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        setSystemUI()

        viewModel.album.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val album = result.data!!

                binding.albumToolbar.title = album.title
                binding.albumTitle.text = album.title
                binding.albumYear.text = getString(R.string.album_subtitle, album.artistName, album.year)
                binding.albumNumTracks.text = resources.getQuantityString(R.plurals.album_track_count, album.numSongs, album.numSongs)
                binding.albumStudioName.text = album.studio

                val imageUrl = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(album.thumb)
                Glide.with(binding.albumArtwork.context)
                    .load(imageUrl)
                    .error(R.drawable.ic_albums_black_24dp)
                    .into(binding.albumArtwork)

                if (album.review.isNotEmpty()) {
                    binding.albumReviewCompose.setContent {
                        Mdc3Theme {
                            ExpandableText(album.review)
                        }
                    }
                }
            }
        }

        viewModel.songs.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val songs = result.data!!
                albumSongsAdapter = AlbumSongsAdapter(songs)
                albumSongsAdapter?.setClickListener(this@AlbumFragment)
                binding.albumSongsRecyclerView.adapter = albumSongsAdapter
            }
        }

        viewModel.albumDuration.observe(viewLifecycleOwner) {
            val duration = TimeUtils.millisecondsToMinutes(it)
            binding.albumDuration.text = resources.getQuantityString(R.plurals.album_duration, duration, duration)
        }

        binding.albumSongsRecyclerView.layoutManager =  LinearLayoutManager(activity)
        val songsDivider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.albumSongsRecyclerView.addItemDecoration(songsDivider)

        binding.albumPlayBtn.setOnClickListener(this)
        binding.albumShuffleBtn.setOnClickListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        albumSongsAdapter = null
    }

    override fun onItemClick(view: View?, position: Int) {
        val song = albumSongsAdapter!!.getItem(position)
        viewModel.playSong(song)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_album, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_album_add_to_queue -> {
                viewModel.playAlbum(WhenToPlay.QUEUE)
                true
            }
            R.id.menu_album_play_next -> {
                viewModel.playAlbum(WhenToPlay.NEXT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.album_play_btn -> playBtnClicked()
            R.id.album_shuffle_btn -> shuffleBtnClicked()
        }
    }

    /**
     * Play the entire album from beginning to end
     */
    private fun playBtnClicked() {
        viewModel.playAlbum()
    }

    /**
     * Shuffle the entire album
     */
    private fun shuffleBtnClicked() {
        viewModel.playAlbum(shuffle = true)
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.albumToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.albumToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
    }
}
