package com.lhoyong.imagepicker.model

sealed class GalleryClickAction {
    data class Select(val image: Image) : GalleryClickAction()
    data class Detail(val image: Image) : GalleryClickAction()
}