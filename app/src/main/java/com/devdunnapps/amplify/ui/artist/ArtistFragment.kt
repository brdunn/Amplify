package com.devdunnapps.amplify.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentArtistBinding
import com.devdunnapps.amplify.domain.models.Song
import com.devdunnapps.amplify.ui.components.ExpandableText
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ArtistFragment : Fragment(),
    ArtistTopAlbumsListAdapter.ItemClickListener,
    ArtistTopSongsListAdapter.ItemClickListener,
    View.OnClickListener {

    private var artistTopAlbumsListAdapter: ArtistTopAlbumsListAdapter? = null
    private var songsListAdapter: ArtistTopSongsListAdapter? = null

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private var artistSongs: List<Song>? = null

    private val viewModel: ArtistViewModel by viewModels()
    private val args: ArtistFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        setSystemUI()

        loadArtist()
        loadArtistTopSongs()
        loadArtistTopAlbums()

        binding.artistSeeAllAlbumsBtn.setOnClickListener(this)
        binding.artistSeeAllSongsBtn.setOnClickListener(this)

        return binding.root
    }

    private fun loadArtistTopAlbums() {
        binding.artistAlbumsRecyclerView.addItemDecoration(ArtistAlbumsMargins(resources.getDimensionPixelSize(R.dimen.default_margin)))
        viewModel.artistAlbums.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val albums = result.data!!
                artistTopAlbumsListAdapter = ArtistTopAlbumsListAdapter(albums, requireActivity())
                artistTopAlbumsListAdapter!!.setClickListener(this@ArtistFragment)
                binding.artistAlbumsRecyclerView.adapter = artistTopAlbumsListAdapter
            }
        }
    }

    private fun loadArtist() {
        viewModel.artist.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val artist = result.data!!

                binding.artistToolbar.title = artist.name
                binding.artistName.text = artist.name

                if (artist.bio.isNotEmpty()) {
                    binding.artistBioCompose.setContent {
                        Mdc3Theme {
                            ExpandableText(artist.bio)
                        }
                    }
                }

                setArtistImage(artist.thumb)
            }
        }
    }

    /**
     * Populates the recyclerview that holds the artist's top songs
     */
    private fun loadArtistTopSongs() {
        viewModel.artistSongs.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val artistsSongs = result.data!!
                songsListAdapter = ArtistTopSongsListAdapter(
                    requireActivity(),
                    artistsSongs.subList(0, if (artistsSongs.size < 5) artistsSongs.size else 5)
                )
                songsListAdapter?.setClickListener(this@ArtistFragment)
                binding.artistTopSongsRecyclerView.adapter = songsListAdapter

                artistSongs = artistsSongs
                binding.artistShuffleBtn.setOnClickListener(this)
            }
        }
    }

    /**
     * Sets the image of the artist at the top of the fragment
     */
    private fun setArtistImage(url: String) {
        val imageUrl = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(url)
        Glide.with(binding.artistPicture.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_artists_black_24dp)
            .into(binding.artistPicture)
    }

    override fun onItemClick(view: View?, position: Int) {
        when (view?.id) {
            R.id.song_item -> {
                val song = songsListAdapter!!.getItem(position)
                viewModel.playSong(song)
            }
            R.id.artist_album_card -> {
                val action = MobileNavigationDirections.actionGlobalNavigationAlbum(artistTopAlbumsListAdapter!!.getItem(position))
                findNavController().navigate(action)
            }
        }
    }

    /**
     * Sets the toolbar as the appbar, home as up, and system bars
     */
    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.artistToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.artistToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.artist_shuffle_btn -> {
                viewModel.shuffleArtist()
            }
            R.id.artist_see_all_albums_btn -> {
                val action = ArtistFragmentDirections.actionNavigationArtistToNavigationArtistAllAlbums(args.artistKey)
                findNavController().navigate(action)
            }
            R.id.artist_see_all_songs_btn -> {
                val action = ArtistFragmentDirections.actionNavigationArtistToNavigationArtistAllSongs(args.artistKey)
                findNavController().navigate(action)
            }
        }
    }
}
