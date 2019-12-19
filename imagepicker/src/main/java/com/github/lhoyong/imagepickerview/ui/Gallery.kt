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

class Gallery : BaseActivity(R.layout.gallery), ImageLoader, GalleryListener {

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

    private var imageLoader: ImageLoaderImpl? = null

    private val setUp by lazy { intent.getParcelableExtra<SetUp>(EXTRA_SETUP) }

    private var resultName = RESULT_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureToolbar()

        recycler_view.apply {
            adapter = ImagePickerAdapter(this@Gallery)
            addItemDecoration(GridSpacingItemDecoration(3, 1, true))
            setHasFixedSize(true)
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

    override fun onDestroy() {
        super.onDestroy()
        imageLoader = null
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
        if (imageLoader == null) {
            imageLoader = ImageLoaderImpl(this)
        }
        imageLoader?.init(this, this)
    }

    override fun loadFinish(list: List<Image>) {
        if (imageList.isNotEmpty()) {
            return
        }

        imageList.addAll(list)
        updateList(list)
        progress_bar.isVisible = false
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
}