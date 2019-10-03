package com.github.lhoyong.imagepicker.model

import android.net.Uri

data class Image(
    val path: Uri,
    val selected: Boolean
)