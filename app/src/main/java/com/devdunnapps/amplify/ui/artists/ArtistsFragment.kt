package com.devdunnapps.amplify.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdunnapps.amplify.MobileNavigationDirections
import com.devdunnapps.amplify.databinding.FragmentArtistsBinding
import com.devdunnapps.amplify.ui.components.*
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistsFragment : Fragment() {

    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.artistsCompose.setContent {
            Mdc3Theme {
                Surface(
                    modifier = Modifier.nestedScroll(rememberViewInteropNestedScrollConnection())
                ) {
                    ArtistsScreen(viewModel) { artist ->
                        findNavController().navigate(MobileNavigationDirections.actionGlobalNavigationArtist(artist))
                    }
                }

            }
        }

        return binding.root
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.artistsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.artistsToolbar)
    }
}

@Composable
private fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onClick: (String) -> Unit
) {
    val artists by viewModel.artists.collectAsState()
    when (artists) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Success -> {
            ArtistList(
                artists = artists.data!!,
                onItemClick = onClick
            )
        }
        is Resource.Error -> {
            ErrorScreen()
        }
    }
}
