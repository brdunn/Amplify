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

@AndroidEntryPoint
class ArtistFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private var artistSongs: List<Song>? = null

    private val viewModel: ArtistViewModel by viewModels()
    private val args: ArtistFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        setSystemUI()

        loadArtist()
        loadArtistTopAlbums()
        loadArtistTopSinglesEps()
        loadArtistTopSongs()

        binding.artistSeeAllAlbumsBtn.setOnClickListener(this)
        binding.artistSeeAllSongsBtn.setOnClickListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadArtistTopAlbums() {
        binding.artistAlbumsRecyclerView.addItemDecoration(ArtistAlbumsMargins(resources.getDimensionPixelSize(R.dimen.default_margin)))
        viewModel.artistAlbums.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                result.data?.let { albums ->
                    if (albums.isEmpty()) {
                        binding.apply {
                            artistAlbumsHeader.visibility = View.GONE
                            artistSeeAllAlbumsBtn.visibility = View.GONE
                            artistAlbumsRecyclerView.visibility = View.GONE
                        }
                    }

                    val artistTopAlbumsListAdapter = ArtistTopAlbumsListAdapter(albums) { album ->
                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(album.id)
                        findNavController().navigate(action)
                    }
                    binding.artistAlbumsRecyclerView.adapter = artistTopAlbumsListAdapter
                }
            }
        }
    }

    private fun loadArtistTopSinglesEps() {
        binding.artistEpsSinglesRecyclerView.addItemDecoration(ArtistAlbumsMargins(resources.getDimensionPixelSize(R.dimen.default_margin)))
        viewModel.artistSinglesEPs.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                result.data?.let { singlesEPs ->
                    if (singlesEPs.isEmpty()) {
                        binding.apply {
                            artistSinglesEpsHeader.visibility = View.GONE
                            artistSeeAllEpsSinglesBtn.visibility = View.GONE
                            artistEpsSinglesRecyclerView.visibility = View.GONE
                        }
                    }

                    val artistTopSinglesEPsListAdapter = ArtistTopAlbumsListAdapter(singlesEPs) { singleEP ->
                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(singleEP.id)
                        findNavController().navigate(action)
                    }
                    binding.artistEpsSinglesRecyclerView.adapter = artistTopSinglesEPsListAdapter
                }
            }
        }
    }

    private fun loadArtist() {
        viewModel.artist.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val artist = result.data!!

                binding.artistToolbar.title = artist.name
                binding.artistName.text = artist.name

                binding.artistBioCompose.setContent {
                    Mdc3Theme {
                        ExpandableText(artist.bio)
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
                val songs = result.data!!
                val artistsSongs = songs.subList(0, if (songs.size < 5) songs.size else 5)
                val songsListAdapter = ArtistTopSongsListAdapter(artistsSongs) { song ->
                    viewModel.playSong(song)
                }
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
