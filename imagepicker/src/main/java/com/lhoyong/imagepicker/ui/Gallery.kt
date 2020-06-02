package com.lhoyong.imagepicker.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lhoyong.imagepicker.R
import com.lhoyong.imagepicker.adapter.GalleryListener
import com.lhoyong.imagepicker.adapter.ImagePickerAdapter
import com.lhoyong.imagepicker.core.ImageLoader
import com.lhoyong.imagepicker.core.ImageLoaderImpl
import com.lhoyong.imagepicker.databinding.GalleryBinding
import com.lhoyong.imagepicker.model.Image
import com.lhoyong.imagepicker.model.SetUp
import com.lhoyong.imagepicker.util.EXTRA_SETUP
import com.lhoyong.imagepicker.util.GridSpacingItemDecoration
import com.lhoyong.imagepicker.util.PermissionUtil
import com.lhoyong.imagepicker.util.RESULT_NAME
import com.lhoyong.imagepicker.util.StringUtil
import com.lhoyong.imagepicker.util.toOptionCompat

internal class Gallery : AppCompatActivity(), GalleryListener {

    private lateinit var binding: GalleryBinding

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

    private val setUp by lazy {
        intent.getParcelableExtra<SetUp>(
            EXTRA_SETUP
        )
    }

    private var resultName = RESULT_NAME

    override var isMultipleChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureToolbar()

        binding.recyclerView.apply {
            adapter = ImagePickerAdapter(
                imageList,
                this@Gallery
            )
            addItemDecoration(
                GridSpacingItemDecoration(
                    3,
                    1,
                    true
                )
            )
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
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
                PermissionUtil.requestGalleryPermission(
                    this,
                    REQUEST_PERMISSION
                )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.gallery_done -> {
                selectedList()?.let { uris -> receiveImages(uris) }
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadImages() {
        imageLoader.load {
            imageList.addAll(it)
            (binding.recyclerView.adapter as ImagePickerAdapter).notifyDataSetChanged()
            binding.progressBar.isVisible = false
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(binding.toolBar)
        supportActionBar?.title = ""
        // set left icon , inflate menu
        binding.toolBar.apply {
            setNavigationIcon(R.drawable.ic_arrow_24dp)
        }

        // left icon click event
        binding.toolBar.setNavigationOnClickListener {
            finish()
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
        (binding.recyclerView.adapter as ImagePickerAdapter).updateItem(image)
        toolbarText(selectedList.size)
    }

    private fun toolbarText(count: Int) {
        selectedText = if (count > 0) {
            count.toString()
        } else {
            ""
        }

        binding.toolBar.title = selectedText
    }

    override fun onChecked(image: Image) {
        selectedImage(image)
    }

    override fun onClick(view: View, image: Image) {
        startActivity(
            Detail.starterIntent(
                this,
                image
            ),
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
