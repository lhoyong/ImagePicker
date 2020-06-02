package com.lhoyong.imagepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lhoyong.imagepicker.R
import com.lhoyong.imagepicker.databinding.ItemGalleryImageBinding
import com.lhoyong.imagepicker.model.Image
import com.lhoyong.imagepicker.util.scaleRevert
import com.lhoyong.imagepicker.util.scaleStart

internal class ImagePickerAdapter(
    private val items: List<Image>,
    private val listener: GalleryListener
) : RecyclerView.Adapter<ImagePickerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        val binding =
            ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagePickerViewHolder(
            binding,
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
    private val binding: ItemGalleryImageBinding,
    private val listener: GalleryListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(image: Image) {

        binding.itemImage.transitionName = image.id.toString()
        binding.root.setOnClickListener {
            if (listener.isMultipleChecked) {
                listener.onChecked(image)
            } else {
                listener.onClick(binding.itemImage, image)
            }
        }

        itemView.setOnLongClickListener {
            if (!listener.isMultipleChecked) {
                listener.onChecked(image)
            }
            return@setOnLongClickListener true
        }

        Glide.with(binding.itemImage)
            .load(image.path)
            .centerCrop()
            .into(binding.itemImage)

        if (image.selected) {
            binding.imageCheckbox.setBackgroundResource(R.drawable.bg_checked)
            scaleStart(binding.itemImage) {
                binding.imageFilter.isVisible = true
            }
        } else {
            binding.imageCheckbox.setBackgroundResource(R.drawable.bg_unchecked)
            scaleRevert(binding.itemImage) {
                binding.imageFilter.isVisible = false
            }
        }
    }
}
