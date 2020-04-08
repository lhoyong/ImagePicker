package com.lhoyong.imagepicker.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val id: Int,
    val path: Uri,
    var selected: Boolean
) : Parcelable
