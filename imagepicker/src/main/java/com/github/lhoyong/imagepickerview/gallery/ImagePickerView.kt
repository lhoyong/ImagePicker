package com.github.lhoyong.imagepickerview.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepickerview.core.Config
import com.github.lhoyong.imagepickerview.core.ImageCallbackListener
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.*
import kotlinx.android.synthetic.main.fragment_image_picker.*
import java.io.File

class ImagePickerView : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val TAG = "ImagePickerView"
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val LOADER_ID = 3327

        private const val MAXIMUM_SELECTION = 30
    }

    private var maxSize = MAXIMUM_SELECTION
    private lateinit var listener: ImageCallbackListener

    private val imageList = mutableListOf<Image>()
    private val selectedList = mutableListOf<Image>()
    private var selectedText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        PermissionUtil.hasGalleryPermissionDenied(requireContext()) {
            if (it) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), REQUEST_PERMISSION
                )
            } else {
                loadImages()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureToolbar()

        recycler_view.apply {
            adapter = ImagePickerAdapter { selectedImage(it) }
            addItemDecoration(GridSpacingItemDecoration(3, 1, true))
            setHasFixedSize(true)
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
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                loadImages()
            } else {
                Log.d("Missing Permission", "Check for permission")
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

    private fun loadImages() {
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        check(id == LOADER_ID) { "illegal loader id: $id" }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        return CursorLoader(requireContext(), uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data ?: return

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
            updateList(imageList)
        }

        progress_bar.isVisible = false
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    private fun configureToolbar() {

        // set left icon , inflate menu
        tool_bar.apply {
            setNavigationIcon(R.drawable.ic_arrow_24dp)
            inflateMenu(R.menu.menu)
        }

        // left icon click event
        tool_bar.setNavigationOnClickListener {
            dismiss()
        }

        tool_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_done -> {
                    selectedList()?.let { uris -> listener.onLoad(uris) }
                    dismiss()
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
    }

    private fun selectedList(): List<Uri>? {
        return imageList.filter { it.selected }.map { it.path }
    }

    private fun selectedImage(image: Image) {
        if (image.selected) {
            imageList.find { it == image }?.selected = false
            selectedList.remove(image)
        } else {
            if (selectedList.size < maxSize) {
                imageList.find { it == image }?.selected = true
                selectedList.add(image)
            } else {
                Log.d(
                    TAG, StringUtil.getStringRes(
                        requireContext(),
                        R.string.select_max_toast,
                        maxSize
                    )
                )
            }
        }
        updateList(imageList)
        toolbarText(selectedList.size)
    }

    private fun updateList(items: List<Image>) {
        (recycler_view.adapter as ImagePickerAdapter).apply {
            submitList(null)
            submitList(items)
        }
    }

    private fun toolbarText(count: Int) {
        selectedText = if (count > 0) {
            count.toString()
        } else {
            ""
        }

        tool_bar.title = selectedText
    }

    fun onImageLoaderListener(action: (List<Uri>) -> Unit) {
        listener = object : ImageCallbackListener {
            override fun onLoad(uriList: List<Uri>) {
                action(uriList)
            }
        }
    }

    private fun onImageLoaderListener(l: ImageCallbackListener?) {
        l?.let { this.listener = it }
    }

    fun config(config: Config?) {
        config?.let {
            maxSize = it.maximumSize ?: MAXIMUM_SELECTION
        }
    }

    class Builder {
        private var config: Config? = null
        private var builderListener: ImageCallbackListener? = null

        fun config(config: Config) = apply { this.config = config }

        fun onImageLoaderListener(l: ImageCallbackListener?) =
            apply { l?.let { builderListener = it } }

        fun build(fm: FragmentManager, tag: String? = null) = apply {
            ImagePickerView().apply {
                config(config)
                onImageLoaderListener(builderListener)
                show(fm, tag)
            }
        }
    }
}