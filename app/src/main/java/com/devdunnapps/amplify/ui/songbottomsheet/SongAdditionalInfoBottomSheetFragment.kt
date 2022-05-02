package com.devdunnapps.amplify.ui.songbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.devdunnapps.amplify.R
import com.devdunnapps.amplify.databinding.FragmentSongAdditionalInfoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongAdditionalInfoBottomSheetFragment: BottomSheetDialogFragment() {

    private var _binding: FragmentSongAdditionalInfoBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val args: SongAdditionalInfoBottomSheetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongAdditionalInfoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playCount = args.song.playCount ?: 0
        binding.songBottomSheetPlayCount.text =
            resources.getQuantityString(R.plurals.song_play_count, playCount, playCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
