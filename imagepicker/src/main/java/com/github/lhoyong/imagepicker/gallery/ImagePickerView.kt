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
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.github.lhoyong.imagepicker.R
import com.github.lhoyong.imagepicker.adapter.ImageDetailLookup
import com.github.lhoyong.imagepicker.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepicker.core.Config
import com.github.lhoyong.imagepicker.core.ImageCallbackListener
import com.github.lhoyong.imagepicker.model.Image
import com.github.lhoyong.imagepicker.util.GridSpacingItemDecoration
import com.github.lhoyong.imagepicker.util.PermissionUtil
import com.github.lhoyong.imagepicker.util.StringUtil
import kotlinx.android.synthetic.main.fragment_image_picker.*
import java.io.File

class ImagePickerView : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val LOADER_ID = 3327

        private const val MAXIMUM_SELECTION = 30
    }

    private var maxSize = MAXIMUM_SELECTION
    private lateinit var listener: ImageCallbackListener

    private val imageList = mutableListOf<Image>()
    private var tracker: SelectionTracker<Long>? = null

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
            adapter = ImagePickerAdapter()
            addItemDecoration(GridSpacingItemDecoration(3, 1, true))
        }
        configureTracker()

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
                println(123)
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

        while (data.moveToNext()) {
            val str = data.getString(columnIndex)
            val path = Uri.fromFile(File(str))
            imageList.add(Image(path, false))
        }

        data.moveToFirst()
        if (imageList.isNotEmpty()) {
            (recycler_view.adapter as ImagePickerAdapter).submitList(imageList)
        }

        progress_bar.isVisible = false
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    private fun configureToolbar() {

        // set left icon , inflate menu
        tool_bar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
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
        return tracker?.selection?.map { imageList[it.toInt()].path }
    }

    private fun configureTracker() {
        tracker = SelectionTracker.Builder<Long>(
            "select_id",
            recycler_view,
            StableIdKeyProvider(recycler_view),
            ImageDetailLookup(recycler_view),
            StorageStrategy.createLongStorage()
        )
            .withOnItemActivatedListener { _, _ ->
                true
            }
            .withSelectionPredicate(selectionPredicate)
            .build()

        tracker?.let {
            (recycler_view.adapter as ImagePickerAdapter).setSelectedTracker(it)
        }

    }

    private val selectionPredicate = object : SelectionTracker.SelectionPredicate<Long>() {
        override fun canSelectMultiple(): Boolean {
            return true
        }

        override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
            val selectedCount = tracker?.selection?.size() ?: 0
            return if (selectedCount >= maxSize && nextState) {
                showLimitToast(maxSize)
                false
            } else {
                true
            }
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            return true
        }
    }

    private fun showLimitToast(count: Int) {
        Toast.makeText(
            requireContext(),
            StringUtil.getStringRes(
                requireContext(),
                R.string.select_max_toast,
                count
            ), Toast.LENGTH_SHORT
        ).show()
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