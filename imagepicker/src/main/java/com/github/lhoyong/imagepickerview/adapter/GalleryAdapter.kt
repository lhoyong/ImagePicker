package com.github.lhoyong.imagepickerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.GlideApp

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

        checkbox.setOnClickListener { listener.onChecked(image) }
        imageView.setOnClickListener { listener.onClick(imageView, image) }
        GlideApp.with(imageView)
            .load(image.path)
            .centerCrop()
            .into(imageView)

        if (image.selected) {
            filter.visibility = View.VISIBLE
            checkbox.setBackgroundResource(R.drawable.bg_checked)
        } else {
            filter.visibility = View.GONE
            checkbox.setBackgroundResource(R.drawable.bg_unchecked)
        }
    }
}