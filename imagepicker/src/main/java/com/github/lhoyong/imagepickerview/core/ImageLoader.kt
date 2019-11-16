package com.github.lhoyong.imagepickerview.core

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.github.lhoyong.imagepickerview.model.Image
import java.io.File

interface ImageLoader {
    fun loadFinish(list: List<Image>)
}

class ImageLoaderImpl(private val context: Context) : LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var listener: ImageLoader

    private companion object {
        private const val LOADER_ID = 3327
    }

    fun <T> init(owner: T, loader: ImageLoader) where T : LifecycleOwner, T : ViewModelStoreOwner {
        listener = loader
        LoaderManager.getInstance(owner).initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        check(id == LOADER_ID) { "illegal loader id: $id" }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        return CursorLoader(context, uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data ?: return

        val imageList = mutableListOf<Image>()
        val columnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA)
        var index = 0
        while (data.moveToNext()) {
            val str = data.getString(columnIndex)
            val path = Uri.fromFile(File(str))
            imageList.add(Image(index, path, false))
            index++
        }

        data.moveToFirst()
        if (imageList.isNotEmpty()) {
            listener.loadFinish(imageList)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}