package com.github.lhoyong.imagepicker.model

import android.net.Uri

data class Image(
    val id: Int,
    val path: Uri,
    var selected: Boolean
)