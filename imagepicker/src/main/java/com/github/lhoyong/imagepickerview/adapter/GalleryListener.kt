package com.github.lhoyong.imagepickerview.adapter

import android.view.View
import com.github.lhoyong.imagepickerview.model.Image

interface GalleryListener {

    //set Checked status with start Animation
    fun onChecked(image: Image)

    // Move to Detail View
    fun onClick(view: View, image: Image)

    // image is already checked
    var isMultipleChecked: Boolean
}