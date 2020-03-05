package com.github.lhoyong.imagepickerview.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.github.lhoyong.imagepickerview.base.BaseActivity
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.adapter.GalleryListener
import com.github.lhoyong.imagepickerview.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepickerview.core.ImageLoader
import com.github.lhoyong.imagepickerview.core.ImageLoaderImpl
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.model.SetUp
import com.github.lhoyong.imagepickerview.util.*
import kotlinx.android.synthetic.main.gallery.*

class Gallery : BaseActivity(R.layout.gallery), GalleryListener {

    companion object {
        private const val TAG = "ImagePickerView"
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val MAXIMUM_SELECTION = 30

        fun starterIntent(context: Context, setup: SetUp?): Intent {
            return Intent(context, Gallery::class.java).apply {
                putExtra(EXTRA_SETUP, setup)
            }
        }
    }

    private var maxSize =
        MAXIMUM_SELECTION

    private val imageList = mutableListOf<Image>()
    private val selectedList = mutableListOf<Image>()
    private var selectedText = ""

    private val imageLoader: ImageLoader by lazy {
        ImageLoaderImpl(this)
    }

    private val setUp by lazy { intent.getParcelableExtra<SetUp>(EXTRA_SETUP) }

    private var resultName = RESULT_NAME

    override var isMultipleChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureToolbar()

        recycler_view.apply {
            adapter = ImagePickerAdapter(imageList, this@Gallery)
            addItemDecoration(GridSpacingItemDecoration(3, 1, true))
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun onStart() {
        super.onStart()

        setUp?.let {
            maxSize = it.max
            resultName = it.name
        }

        PermissionUtil.hasGalleryPermissionDenied(this) {
            if (it) {
                PermissionUtil.requestGalleryPermission(this, REQUEST_PERMISSION)
            } else {
                loadImages()
            }
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
                Log.d("uri", uri.toString())
            }
        }
    }

    private fun loadImages() {
        imageLoader.load {
            imageList.addAll(it)
            (recycler_view.adapter as ImagePickerAdapter).notifyDataSetChanged()
            progress_bar.isVisible = false
        }
    }

    private fun configureToolbar() {

        // set left icon , inflate menu
        tool_bar.apply {
            setNavigationIcon(R.drawable.ic_arrow_24dp)
            inflateMenu(R.menu.menu)
        }

        // left icon click event
        tool_bar.setNavigationOnClickListener {
            finish()
        }

        tool_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_done -> {
                    selectedList()?.let { uris -> receiveImages(uris) }
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
                        this,
                        R.string.select_max_toast,
                        maxSize
                    )
                )
            }
        }
        isMultipleChecked = isMultiCheckedChanged()
        (recycler_view.adapter as ImagePickerAdapter).updateItem(image)
        toolbarText(selectedList.size)
    }

    private fun toolbarText(count: Int) {
        selectedText = if (count > 0) {
            count.toString()
        } else {
            ""
        }

        tool_bar.title = selectedText
    }

    override fun onChecked(image: Image) {
        selectedImage(image)
    }

    override fun onClick(view: View, image: Image) {
        startActivity(
            Detail.starterIntent(this, image),
            toOptionCompat(view, R.id.item_image).toBundle()
        )
    }

    private fun receiveImages(uris: List<Uri>) {
        val resultIntent = Intent().apply {
            putParcelableArrayListExtra(resultName, ArrayList(uris))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun isMultiCheckedChanged() = imageList.find { it.selected } != null
}