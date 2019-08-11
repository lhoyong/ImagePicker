package com.github.lhoyong.imagepicker.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun Uri.toImagePath(context: Context): String? {
    var res: String? = null
    val cursor =
        context.contentResolver.query(this, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    cursor?.let {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = it.getString(columnIndex)
        }
    }
    cursor?.close()
    return res
}