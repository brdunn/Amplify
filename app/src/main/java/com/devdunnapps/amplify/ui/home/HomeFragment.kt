package com.devdunnapps.amplify.ui.home

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
import androidx.recyclerview.widget.GridLayoutManager
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentHomeBinding
import com.devdunnapps.amplify.ui.albums.AlbumsListAdapter
import com.devdunnapps.amplify.ui.artists.ArtistsListAdapter
import com.devdunnapps.amplify.ui.playlists.PlaylistsAdapter
import com.devdunnapps.amplify.ui.songs.SongsListAdapter
import com.devdunnapps.amplify.ui.utils.RecyclerViewGridItemMargins
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var songsListAdapter: SongsListAdapter? = null
    private var albumsListAdapter: AlbumsListAdapter? = null
    private var artistsListAdapter: ArtistsListAdapter? = null
    private var playlistsListAdapter: PlaylistsAdapter? = null
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.searchResultsAlbums.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsAlbums.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))
        binding.searchResultsArtists.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsArtists.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))

        viewModel.recentlyPlayedMedia.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val songs = result.data!!.songs
                binding.searchResultsSongsHeader.visibility = if (songs.isEmpty()) View.GONE else View.VISIBLE

                songsListAdapter = SongsListAdapter(requireContext(), songs)
                songsListAdapter!!.setClickListener(object : SongsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        viewModel.playSong(position)
                    }
                })
                binding.searchResultsSongs.adapter = songsListAdapter

                val albums = result.data.albums
                binding.searchResultsAlbumsHeader.visibility = if (albums.isEmpty()) View.GONE else View.VISIBLE

                albumsListAdapter = AlbumsListAdapter(albums, requireContext())
                albumsListAdapter!!.setClickListener(object : AlbumsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val action = MobileNavigationDirections.actionGlobalNavigationAlbum(albums[position].id)
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsAlbums.adapter = albumsListAdapter

                val artists = result.data.artists
                binding.searchResultsArtistsHeader.visibility = if (artists.isEmpty()) View.GONE else View.VISIBLE

                artistsListAdapter = ArtistsListAdapter(artists, requireContext())
                artistsListAdapter!!.setClickListener(object : ArtistsListAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val action = MobileNavigationDirections.actionGlobalNavigationArtist(artistsListAdapter!!.getItem(position))
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsArtists.adapter = artistsListAdapter

                val playlists = result.data.playlists
                binding.searchResultsPlaylistsHeader.visibility = if (playlists.isEmpty()) View.GONE else View.VISIBLE

                playlistsListAdapter = PlaylistsAdapter(playlists)
                playlistsListAdapter!!.setClickListener(object : PlaylistsAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val action = HomeFragmentDirections.actionHomeFragmentToNavigationPlaylist(playlistsListAdapter!!.getItem(position).id)
                        findNavController().navigate(action)
                    }
                })
                binding.searchResultsPlaylists.adapter = playlistsListAdapter
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        songsListAdapter = null
        albumsListAdapter = null
        artistsListAdapter = null
        playlistsListAdapter = null
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.searchToolbar)
    }
}
