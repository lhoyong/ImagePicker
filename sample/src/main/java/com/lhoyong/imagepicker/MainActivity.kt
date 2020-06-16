package com.lhoyong.imagepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.lhoyong.sample_navigation.NavigationActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private companion object {
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

        nav.setOnClickListener {
            startActivity(Intent(this, NavigationActivity::class.java))
        }
    }

    private fun openImagePicker() {
        ImagePickerView.Builder()
            .setup {
                name { RESULT_NAME }
                max { 5 }
                title { "Image Picker" }
                single { false }
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
