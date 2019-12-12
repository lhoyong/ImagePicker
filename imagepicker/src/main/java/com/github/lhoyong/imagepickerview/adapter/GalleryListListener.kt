package com.github.lhoyong.imagepickerview.adapter

import com.github.lhoyong.imagepickerview.model.Image

interface GalleryListListener {
    fun onChecked(image: Image)
    fun onClick(image: Image)
}