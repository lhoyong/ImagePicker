package com.github.lhoyong.pinedimagepicker

import android.net.Uri
import android.os.Bundle
import android.security.ConfirmationPrompt
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.lhoyong.imagepicker.core.Config
import com.github.lhoyong.imagepicker.core.ImageCallbackListener
import com.github.lhoyong.imagepicker.gallery.ImagePickerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ImageCallbackListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = ImageAdapter()
        }

        button.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {

        val config = Config.Builder().max(11).build()

        ImagePickerView.Builder()
            .config(config)
            .onImageLoaderListener(this)
            .build(supportFragmentManager)
    }

    override fun onLoad(uriList: List<Uri>) {
        (recycler_view.adapter as ImageAdapter).submitList(uriList)
    }

}
