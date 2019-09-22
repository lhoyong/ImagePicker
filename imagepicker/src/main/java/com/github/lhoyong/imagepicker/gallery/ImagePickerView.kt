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
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.github.lhoyong.imagepicker.R
import com.github.lhoyong.imagepicker.adapter.ImageDetailLookup
import com.github.lhoyong.imagepicker.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepicker.model.Image
import com.github.lhoyong.imagepicker.util.GridSpacingItemDecoration
import com.github.lhoyong.imagepicker.util.PermissionUtil
import kotlinx.android.synthetic.main.fragment_image_picker.*
import java.io.File

class ImagePickerView : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private companion object {
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val LOADER_ID = 3327

        private const val MAXIMUM_SELECTION = 30
    }

    private val imageList = mutableListOf<Image>()
    private var tracker: SelectionTracker<Long>? = null

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

        return CursorLoader(requireContext(), uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data ?: return

        val columnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA)

        while (data.moveToNext()) {
            val str = data.getString(columnIndex)
            val path = Uri.fromFile(File(str))
            imageList.add(Image(path.toString(), false))
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

                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
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
            return if (tracker?.selection?.size() ?: 0 >= MAXIMUM_SELECTION && nextState) {
                Toast.makeText(
                    requireContext(),
                    "You can only select $MAXIMUM_SELECTION items in the list.", Toast.LENGTH_SHORT
                ).show()
                false
            } else {
                true
            }
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            return true
        }
    }


}