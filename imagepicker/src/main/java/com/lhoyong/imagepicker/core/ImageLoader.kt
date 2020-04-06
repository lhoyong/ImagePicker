package com.lhoyong.imagepicker.core

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.lhoyong.imagepicker.model.Image

interface ImageLoader {
    fun load(action: (List<Image>) -> Unit)
}

class ImageLoaderImpl(private val context: Context) :
    ImageLoader {

    override fun load(action: (List<Image>) -> Unit) {
        action(getFiles(context))
    }

    private fun getFiles(context: Context): List<Image> {
        val fileList = mutableListOf<Image>()
        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val sortOrder = MediaStore.Images.Media._ID + " DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                fileList.add(
                    Image(
                        id.toInt(),
                        contentUri,
                        false
                    )
                )
            }
        }
        return fileList
    }
}