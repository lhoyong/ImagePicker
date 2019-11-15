package com.github.lhoyong.imagepickerview

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.github.lhoyong.imagepickerview.core.Config
import com.github.lhoyong.imagepickerview.core.ImageCallbackListener
import com.github.lhoyong.imagepickerview.gallery.ImagePickerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ImageCallbackListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            setHasFixedSize(true)
            adapter = ImageAdapter()
        }

        button.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {

        val config = Config.Builder().max(5).build()

        ImagePickerView.Builder()
            .config(config)
            .onImageLoaderListener(this)
            .build(supportFragmentManager)
    }

    override fun onLoad(uriList: List<Uri>) {
        (recycler_view.adapter as ImageAdapter).submitList(uriList)
    }

}
