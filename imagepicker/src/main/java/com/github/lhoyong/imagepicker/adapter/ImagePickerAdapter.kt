package com.github.lhoyong.imagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.lhoyong.imagepicker.R
import com.github.lhoyong.imagepicker.model.Image

class ImagePickerAdapter : ListAdapter<Image, ImagePickerViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImagePickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ImagePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.item_image)

    fun bind(image: Image) {

        Glide.with(imageView)
            .load(image.path)
            .into(imageView)
    }
}

val diffUtil = object : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.selected == newItem.selected && oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean = oldItem == newItem
}