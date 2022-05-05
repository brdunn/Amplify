package com.devdunnapps.amplify.ui.artist.albums

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
import com.devdunnapps.amplify.databinding.FragmentArtistAllAlbumsBinding
import com.devdunnapps.amplify.ui.utils.RecyclerViewGridItemMargins
import com.devdunnapps.amplify.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistAllAlbumsFragment : Fragment() {

    private var _binding: FragmentArtistAllAlbumsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistAllAlbumsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistAllAlbumsBinding.inflate(inflater, container, false)

        setSystemUI()

        binding.albumsTabRecyclerView.addItemDecoration(RecyclerViewGridItemMargins(resources.getDimensionPixelSize(
                R.dimen.eight_margin), 2))
        binding.albumsTabRecyclerView.layoutManager = GridLayoutManager(context, 2)

        viewModel.artistAlbums.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val albums = result.data!!
                val albumsListAdapter = ArtistAllAlbumsListAdapter(albums) { album ->
                    val action = MobileNavigationDirections.actionGlobalNavigationAlbum(album.id)
                    findNavController().navigate(action)
                }
                binding.albumsProgressBar.visibility = View.INVISIBLE
                binding.albumsTabRecyclerView.adapter = albumsListAdapter
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.albumsToolbarLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.albumsToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}
