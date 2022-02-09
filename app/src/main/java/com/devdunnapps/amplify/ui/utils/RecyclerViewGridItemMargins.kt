package com.devdunnapps.amplify.ui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewGridItemMargins(
    private val padding: Int,
    private val spanCount: Int = 3
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (recyclerView.getChildAdapterPosition(view) < spanCount) top = padding
            bottom = padding

            val column = recyclerView.getChildAdapterPosition(view) % spanCount
            left = padding - column * padding / spanCount
            right = (column + 1) * padding / spanCount
        }
    }
}
