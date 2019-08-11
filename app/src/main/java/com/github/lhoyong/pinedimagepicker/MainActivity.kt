package com.github.lhoyong.pinedimagepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.lhoyong.imagepicker.gallery.ImagePickerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {
            ImagePickerView().show(supportFragmentManager, null)
        }


    }
}
