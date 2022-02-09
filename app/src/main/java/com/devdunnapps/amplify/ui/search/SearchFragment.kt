package com.devdunnapps.amplify.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentSearchBinding
import com.devdunnapps.amplify.ui.albums.AlbumsListAdapter
import com.devdunnapps.amplify.ui.artists.ArtistsListAdapter
import com.devdunnapps.amplify.ui.playlists.PlaylistsAdapter
import com.devdunnapps.amplify.ui.songs.SongsListAdapter
import com.devdunnapps.amplify.ui.utils.RecyclerViewGridItemMargins
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var songsListAdapter: SongsListAdapter? = null
    private var albumsListAdapter: AlbumsListAdapter? = null
    private var artistsListAdapter: ArtistsListAdapter? = null
    private var playlistsListAdapter: PlaylistsAdapter? = null
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.searchResultsAlbums.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsAlbums.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))
        binding.searchResultsArtists.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsArtists.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))

        binding.search.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString())
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val songs = result.data!!.songs
                binding.searchResultsSongsNoResults.visibility = if (songs.isEmpty()) View.VISIBLE else View.INVISIBLE

                songsListAdapter = SongsListAdapter(requireContext(), songs)
                songsListAdapter!!.setClickListener(object : SongsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        viewModel.playSong(position)
                    }
                })
                binding.searchResultsSongs.adapter = songsListAdapter

                val albums = result.data.albums
                binding.searchResultsAlbumsNoResults.visibility = if (albums.isEmpty()) View.VISIBLE else View.INVISIBLE

                albumsListAdapter = AlbumsListAdapter(albums, requireContext())
                albumsListAdapter!!.setClickListener(object : AlbumsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albums[position].id)
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsAlbums.adapter = albumsListAdapter

                val artists = result.data.artists
                binding.searchResultsArtistsNoResults.visibility = if (artists.isEmpty()) View.VISIBLE else View.INVISIBLE

                artistsListAdapter = ArtistsListAdapter(artists, requireContext())
                artistsListAdapter!!.setClickListener(object : ArtistsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val action = MobileNavigationDirections.actionGlobalNavigationArtist(artistsListAdapter!!.getItem(position))
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsArtists.adapter = artistsListAdapter

                val playlists = result.data.playlists
                binding.searchResultsPlaylistsNoResults.visibility = if (playlists.isEmpty()) View.VISIBLE else View.INVISIBLE

                playlistsListAdapter = PlaylistsAdapter(playlists)
                playlistsListAdapter!!.setClickListener(object : PlaylistsAdapter.ItemClickListener { override fun onItemClick(view: View?, position: Int) {
                        val action = SearchFragmentDirections.actionSearchFragmentToNavigationPlaylist(playlistsListAdapter!!.getItem(position).id)
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsPlaylists.adapter = playlistsListAdapter
            }
        }

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.forEach { it.isVisible = false }
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        setHasOptionsMenu(true)
    }
}
