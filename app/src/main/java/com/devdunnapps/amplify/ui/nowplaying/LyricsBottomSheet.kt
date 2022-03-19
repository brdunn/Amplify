package com.devdunnapps.amplify.ui.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.devdunnapps.amplify.databinding.FragmentLyricsBottomSheetBinding
import com.devdunnapps.amplify.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LyricsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentLyricsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LyricsBottomSheetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       _binding = FragmentLyricsBottomSheetBinding.inflate(inflater, container, false)

        viewModel.songLyrics.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Success -> {
                    binding.lyricsText.text = result.data!!.lyrics
                }
                is Resource.Error -> {
                    binding.lyricsText.text = "Could not find lyrics for this song"
                }
            }
        }

        binding.songBottomSheetTitle.text = requireArguments().getString("songTitle")!!
        binding.songBottomSheetArtist.text = requireArguments().getString("songArtist")!!

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
