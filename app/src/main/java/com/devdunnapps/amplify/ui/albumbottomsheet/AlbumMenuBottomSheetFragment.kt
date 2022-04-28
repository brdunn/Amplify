package com.devdunnapps.amplify.ui.albumbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentAlbumBottomSheetBinding
import com.devdunnapps.amplify.domain.models.Album
import com.devdunnapps.amplify.ui.songbottomsheet.AlbumMenuBottomSheetViewModel
import com.devdunnapps.amplify.utils.PlexUtils
import com.devdunnapps.amplify.utils.Resource
import com.devdunnapps.amplify.utils.WhenToPlay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumMenuBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAlbumBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var album: Album
    private val viewModel: AlbumMenuBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       _binding = FragmentAlbumBottomSheetBinding.inflate(inflater, container, false)

        viewModel.album.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    album = response.data!!
                    binding.albumBottomSheetTitle.text = album.title
                    binding.albumBottomSheetArtist.text = album.artistName

                    val url = PlexUtils.getInstance(requireActivity()).addKeyAndAddress(album.thumb)
                    Glide.with(binding.albumBottomSheetAlbumArt)
                        .load(url)
                        .error(R.drawable.ic_albums_black_24dp)
                        .into(binding.albumBottomSheetAlbumArt)
                }
                else -> Unit
            }
        }

        binding.albumBottomSheetPlayNextBtn.setOnClickListener {
            viewModel.playAlbum(WhenToPlay.NEXT)
            dismiss()
        }

        binding.albumBottomSheetAddToQueueBtn.setOnClickListener {
            viewModel.playAlbum(WhenToPlay.QUEUE)
            dismiss()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
