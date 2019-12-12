package com.github.lhoyong.imagepickerview

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.github.lhoyong.imagepickerview.core.ImageCallbackListener
import com.github.lhoyong.imagepickerview.core.config
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
        ImagePickerView.Builder()
            .setup {
                config {
                    max { 5 }
                }
            }
            .onImageLoaderListener(this)
            .start(this)
    }

    override fun onLoad(uriList: List<Uri>) {
        (recycler_view.adapter as ImageAdapter).submitList(uriList)
    }

}
