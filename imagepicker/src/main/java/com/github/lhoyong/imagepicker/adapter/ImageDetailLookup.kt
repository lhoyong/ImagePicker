package com.github.lhoyong.imagepicker.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ImageDetailLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        view?.let {
            val holder = recyclerView.getChildViewHolder(view)
            (holder as? ImagePickerViewHolder).apply {
                return object : ItemDetails<Long>() {
                    override fun getSelectionKey() = holder.itemId
                    override fun getPosition() = holder.adapterPosition
                }
            }
        }
        return null

    }

}