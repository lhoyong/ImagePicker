package com.lhoyong.sample_navigation

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NavigationAdapter : ListAdapter<Uri, NavigationViewHolder>(
    diff
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationViewHolder {
        return NavigationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_navigate,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.image)

    fun bind(path: Uri?) {
        path?.let {
            Glide.with(imageView.context)
                .load(it)
                .centerCrop()
                .into(imageView)
        }
    }
}

val diff = object : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return false
    }
}
