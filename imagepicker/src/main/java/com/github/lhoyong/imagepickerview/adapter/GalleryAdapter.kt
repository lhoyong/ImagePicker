package com.github.lhoyong.imagepickerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.GlideApp
import com.github.lhoyong.imagepickerview.util.scaleRevert
import com.github.lhoyong.imagepickerview.util.scaleStart

class ImagePickerAdapter(
    private val items: List<Image>,
    private val listener: GalleryListener
) : RecyclerView.Adapter<ImagePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImagePickerViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItem(item: Image) {
        val position = items.indexOf(item)
        notifyItemChanged(position)
    }
}

class ImagePickerViewHolder(
    view: View,
    private val listener: GalleryListener
) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.item_image)
    private val filter = view.findViewById<View>(R.id.image_filter)
    private val checkbox = view.findViewById<AppCompatImageView>(R.id.image_checkbox)

    fun bind(image: Image) {

        itemView.setOnClickListener {
            if (listener.isMultipleChecked) {
                listener.onChecked(image)
            } else {
                listener.onClick(imageView, image)
            }
        }

        itemView.setOnLongClickListener {
            if (!listener.isMultipleChecked) {
                listener.onChecked(image)
            }
            return@setOnLongClickListener true
        }

        GlideApp.with(imageView)
            .load(image.path)
            .centerCrop()
            .into(imageView)

        if (image.selected) {
            scaleStart(imageView) {
                checkbox.isVisible = true
                filter.isVisible = true
            }
        } else {
            scaleRevert(imageView) {
                checkbox.isVisible = false
                filter.isVisible = false
            }
        }
    }
}