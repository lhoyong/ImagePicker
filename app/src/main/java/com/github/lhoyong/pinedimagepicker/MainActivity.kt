package com.github.lhoyong.pinedimagepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.lhoyong.imagepicker.gallery.ImagePickerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
        val picker = ImagePickerView()
        picker.onImageLoaderListener { (recycler_view.adapter as ImageAdapter).submitList(it) }
        picker.show(supportFragmentManager, null)
    }

}
