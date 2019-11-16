package com.github.lhoyong.imagepickerview.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.adapter.ImagePickerAdapter
import com.github.lhoyong.imagepickerview.core.Config
import com.github.lhoyong.imagepickerview.core.ImageCallbackListener
import com.github.lhoyong.imagepickerview.core.ImageLoader
import com.github.lhoyong.imagepickerview.core.ImageLoaderImpl
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.GridSpacingItemDecoration
import com.github.lhoyong.imagepickerview.util.PermissionUtil
import com.github.lhoyong.imagepickerview.util.StringUtil
import kotlinx.android.synthetic.main.fragment_image_picker.*

class ImagePickerView : DialogFragment(), ImageLoader {

    companion object {
        private const val TAG = "ImagePickerView"
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val MAXIMUM_SELECTION = 30
    }

    private var maxSize = MAXIMUM_SELECTION
    private lateinit var listener: ImageCallbackListener

    private val imageList = mutableListOf<Image>()
    private val selectedList = mutableListOf<Image>()
    private var selectedText = ""

    private var imageLoader: ImageLoaderImpl? = null

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
            imageLoader = ImageLoaderImpl(requireContext())
        }
        imageLoader?.init(this, this)
        // LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun loadFinish(list: List<Image>) {
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