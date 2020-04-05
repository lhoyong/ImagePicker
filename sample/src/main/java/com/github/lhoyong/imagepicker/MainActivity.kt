package com.github.lhoyong.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.github.lhoyong.imagepicker.core.config
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private companion object{
        private const val RESULT_NAME = "result"
    }

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
                    name { RESULT_NAME }
                    max { 5 }
                }
            }
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val images = data?.getParcelableArrayListExtra<Uri>(RESULT_NAME)
            images?.let {
                (recycler_view.adapter as ImageAdapter).submitList(it)
            }
        }
    }

}
