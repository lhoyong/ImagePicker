package com.github.lhoyong.imagepicker.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.github.lhoyong.imagepicker.R
import com.github.lhoyong.imagepicker.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepicker.model.Image
import com.github.lhoyong.imagepicker.util.PermissionUtil
import kotlinx.android.synthetic.main.fragment_image_picker.*
import java.io.File

class ImagePickerView : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private companion object {
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val LOADER_ID = 3327
    }

    private val imageList = mutableListOf<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        PermissionUtil.hasGalleryPermissionDenied(requireContext()) {
            if (it) {
                PermissionUtil.requestGalleryPermission(requireActivity(), REQUEST_PERMISSION)
            } else {
                LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_image_picker, container, false)

        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.apply {
            adapter = ImagePickerAdapter()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (permissions.size == 2 &&
                permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                permissions[1] == Manifest.permission.WRITE_EXTERNAL_STORAGE &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED

            ) {
                LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
            } else {
                //denied
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        if (requestCode == REQUEST_GALLERY) {
            data?.data?.let { uri ->
                Log.e("uri", uri.toString())
            }
        }

    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id != LOADER_ID) throw IllegalStateException("illegal loader id: $id")
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        Log.e("createLoader", "create")
        return CursorLoader(requireContext(), uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data ?: return

        val columnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA)
        //val items = ArrayList<Uri>()

        while (data.moveToNext()) {
            val str = data.getString(columnIndex)
            val path = Uri.fromFile(File(str))
            imageList.add(Image(path.toString(), false))
        }

        /* while (items.size < 512 && data.moveToNext()) {
             val str = data.getString(columnIndex)
             items.add(Uri.fromFile(File(str)))
         }*/
        Log.e("load finish", "${imageList.size}")
        data.moveToFirst()
        if (imageList.isNotEmpty()) {
            (recycler_view.adapter as ImagePickerAdapter).submitList(imageList)
        }

        progress_bar.isVisible = false
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
// reset
    }


}