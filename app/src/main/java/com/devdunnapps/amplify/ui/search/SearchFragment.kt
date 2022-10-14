package com.devdunnapps.amplify.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.devdunnapps.amplify.ui.utils.RecyclerViewGridItemMargins
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.searchResultsAlbums.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsAlbums.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))
        binding.searchResultsArtists.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.searchResultsArtists.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(R.dimen.eight_margin)))

        binding.search.requestFocus()

        binding.search.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString())
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val data = result.data ?: return@observe

                val zeroStateVisible =
                    data.songs.isEmpty() && data.albums.isEmpty() && data.artists.isEmpty() && data.playlists.isEmpty()

                binding.searchEmptyResults.visibility = if (zeroStateVisible) View.INVISIBLE else View.INVISIBLE

                val songs = result.data.songs
                binding.searchResultsSongsHeader.visibility = if (songs.isEmpty()) View.GONE else View.VISIBLE

                binding.searchResultsSongs.adapter = SongsListAdapter(songs) { song ->
                    viewModel.playSong(song)
                }

                val albums = result.data.albums
                binding.searchResultsAlbumsHeader.visibility = if (albums.isEmpty()) View.GONE else View.VISIBLE

                binding.searchResultsAlbums.adapter = AlbumsListAdapter(albums) { album ->
                    val action = MobileNavigationDirections.actionGlobalNavigationAlbum(album.id)
                    findNavController().navigate(action)
                }

                val artists = result.data.artists
                binding.searchResultsArtistsHeader.visibility =
                    if (artists.isEmpty()) View.GONE else View.VISIBLE

                binding.searchResultsArtists.adapter = ArtistsListAdapter(artists) { artist ->
                    val action = MobileNavigationDirections.actionGlobalNavigationArtist(artist.id)
                    findNavController().navigate(action)
                }

                val playlists = result.data.playlists
                binding.searchResultsPlaylistsHeader.visibility =
                    if (playlists.isEmpty()) View.GONE else View.VISIBLE

                binding.searchResultsPlaylists.adapter = PlaylistsAdapter(playlists) { playlist ->
                    val action = SearchFragmentDirections.actionSearchFragmentToNavigationPlaylist(playlist.id)
                    findNavController().navigate(action)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // automatically show the software keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.search, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
