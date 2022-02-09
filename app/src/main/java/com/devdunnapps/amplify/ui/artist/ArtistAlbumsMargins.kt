package com.devdunnapps.amplify.ui.artist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ArtistAlbumsMargins(private val spaceSize: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = spaceSize
            }
            top = spaceSize
            right = spaceSize
            bottom = spaceSize
        }
    }
}