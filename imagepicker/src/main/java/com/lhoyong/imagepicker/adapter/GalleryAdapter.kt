package com.lhoyong.imagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lhoyong.imagepicker.R
import com.lhoyong.imagepicker.model.Image
import com.lhoyong.imagepicker.util.scaleRevert
import com.lhoyong.imagepicker.util.scaleStart

internal class ImagePickerAdapter(
    private val items: List<Image>,
    private val listener: GalleryListener
) : RecyclerView.Adapter<ImagePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_image, parent, false)
        return ImagePickerViewHolder(
            view,
            listener
        )
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

internal class ImagePickerViewHolder(
    view: View,
    private val listener: GalleryListener
) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.item_image)
    private val filter = view.findViewById<View>(R.id.image_filter)
    private val checkbox = view.findViewById<AppCompatImageView>(R.id.image_checkbox)

    fun bind(image: Image) {

        imageView.transitionName = image.id.toString()
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

        Glide.with(imageView)
            .load(image.path)
            .centerCrop()
            .into(imageView)

        if (image.selected) {
            checkbox.setBackgroundResource(R.drawable.bg_checked)
            scaleStart(imageView) {
                filter.isVisible = true
            }
        } else {
            checkbox.setBackgroundResource(R.drawable.bg_unchecked)
            scaleRevert(imageView) {
                filter.isVisible = false
            }
        }
    }
}
