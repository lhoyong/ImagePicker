package com.github.lhoyong.imagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.lhoyong.imagepicker.R
import com.github.lhoyong.imagepicker.model.Image
import com.github.lhoyong.imagepicker.util.GlideApp

class ImagePickerAdapter : ListAdapter<Image, ImagePickerViewHolder>(diffUtil),
    ImageAdapterListener {

    private var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImagePickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        tracker?.let {
            holder.bind(getItem(position), it.isSelected(getItemId(position)))
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun setSelectedTracker(tracker: SelectionTracker<Long>) {
        this.tracker = tracker
    }
}

class ImagePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.item_image)
    private val filter = view.findViewById<View>(R.id.image_filter)

    fun bind(image: Image, isSelected: Boolean = false) {

        GlideApp.with(imageView)
            .load(image.path)
            .centerCrop()
            .into(imageView)

        if (isSelected) {
            filter.visibility = View.VISIBLE
        } else {
            filter.visibility = View.GONE
        }
    }
}

val diffUtil = object : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.selected == newItem.selected && oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean = oldItem == newItem
}