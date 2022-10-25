package com.devdunnapps.amplify.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.collectAsState
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentPlaylistBinding
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.SongItem
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        setSystemUI()

        val playlistId = requireArguments().getString("playlistId")!!

        viewModel.playlist.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                binding.playlistToolbar.title = result.data!!.title
            }
        }

        binding.playlistSongs.setContent {
            when (val playlistSongs = viewModel.playlistSongs.collectAsState().value) {
                is Resource.Loading -> LoadingScreen()
                is Resource.Error -> ErrorScreen()
                is Resource.Success -> {
                    LazyColumn {
                        itemsIndexed(playlistSongs.data!!) { index, song ->
                            SongItem(
                                song = song,
                                onClick = { viewModel.playSong(index) },
                                onItemMenuClick = {
                                    val action = PlaylistFragmentDirections
                                        .actionNavigationPlaylistToPlaylistSongMenuBottomSheetFragment(song, playlistId)
                                    findNavController().navigate(action)
                                }
                            )
                        }
                    }
                }
            }
        }

        binding.playlistPlayBtn.setOnClickListener(this)
        binding.playlistShuffleBtn.setOnClickListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.navigation_playlist)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("refreshData")) {
                val songsShouldBeRefreshed = navBackStackEntry.savedStateHandle.get<Boolean>("refreshData")!!
                if (songsShouldBeRefreshed) {
                    viewModel.gatherSongs()
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets the toolbar as the appbar, home as up, and system bars
     */
    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.playlistToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.playlistToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.playlist_play_btn -> playBtnClicked()
            R.id.playlist_shuffle_btn -> shuffleBtnClicked()
        }
    }

    private fun playBtnClicked() {
        viewModel.playPlaylist()
    }

    private fun shuffleBtnClicked() {
        viewModel.playPlaylist(shuffle = true)
    }
}
