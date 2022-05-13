package com.devdunnapps.amplify.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentPlaylistsBinding
import com.devdunnapps.amplify.domain.models.Playlist
import com.devdunnapps.amplify.ui.components.ErrorScreen
import com.devdunnapps.amplify.ui.components.LoadingScreen
import com.devdunnapps.amplify.ui.components.PlaylistList
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.playlistsCreatePlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }

        return binding.root
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.navigation_playlists)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("refreshData")) {
                val playlistsShouldBeRefreshed = navBackStackEntry.savedStateHandle.get<Boolean>("refreshData")!!
                if (playlistsShouldBeRefreshed) {
                    viewModel.gatherPlaylists()
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

        binding.playlistsCompose.setContent {
            Mdc3Theme {
                Surface(
                    modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
                ) {
                    PlaylistsScreen(
                        viewModel = viewModel,
                        onClick = { playlistId ->
                            val action =
                                PlaylistsFragmentDirections.actionNavigationPlaylistsToPlaylistFragment(
                                    playlistId
                                )
                            findNavController().navigate(action)
                        },
                        onItemMenuClick = { playlistId ->
                            val action =
                                PlaylistsFragmentDirections.actionNavigationPlaylistsToPlaylistMenuBottomSheetFragment(
                                    playlistId
                                )
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.playlistsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.playlistsToolbar)
    }

    private fun showCreatePlaylistDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_playlist, null)
        val playlistName: EditText = dialogView.findViewById(R.id.create_playlist_name)

        val createPlaylistDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.create_playlist_dialog_title))
            .setView(dialogView)
            .setPositiveButton(resources.getString(R.string.create_playlist_dialog_confirm)) { _, _ ->
                viewModel.createPlaylist(playlistName.text.toString())
            }
            .setNegativeButton(resources.getString(R.string.create_playlist_dialog_cancel)) { _, _ ->}
            .show()

        createPlaylistDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        playlistName.doAfterTextChanged {
            val playlistNameIsNotEmpty = playlistName.text.isNotEmpty()
            createPlaylistDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = playlistNameIsNotEmpty
        }
    }
}

@Composable
private fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onClick: (String) -> Unit,
    onItemMenuClick: (String) -> Unit
) {
    val playlists by viewModel.playlists.observeAsState(Resource.Loading())
    when (playlists) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Success -> {
            PlaylistList(
                playlists = (playlists as Resource.Success<List<Playlist>>).data!!,
                onItemClick = onClick,
                onItemMenuClick = onItemMenuClick
            )
        }
        is Resource.Error -> {
            ErrorScreen()
        }
    }
}
