package com.github.lhoyong.imagepickerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.GlideApp

class ImagePickerAdapter(
    private val listener: GalleryListListener
) : ListAdapter<Image, ImagePickerViewHolder>(diffUtil) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImagePickerViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id?.toLong() ?: position.toLong()
    }
}

class ImagePickerViewHolder(
    view: View,
    private val listener: GalleryListListener
) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.item_image)
    private val filter = view.findViewById<View>(R.id.image_filter)
    private val checkbox = view.findViewById<AppCompatCheckBox>(R.id.image_checkbox)

    fun bind(image: Image) {

        checkbox.setOnClickListener { listener.onChecked(image) }
        imageView.setOnClickListener { listener.onClick(image) }

        GlideApp.with(imageView)
            .load(image.path)
            .centerCrop()
            .into(imageView)

        if (image.selected) {
            filter.visibility = View.VISIBLE
        } else {
            filter.visibility = View.GONE
        }

        checkbox.isChecked = image.selected
    }
}

val diffUtil = object : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.selected == newItem.selected
    }

}