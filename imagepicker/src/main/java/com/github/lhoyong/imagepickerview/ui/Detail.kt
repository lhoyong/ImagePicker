package com.github.lhoyong.imagepickerview.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.lhoyong.imagepickerview.base.BaseActivity
import com.github.lhoyong.imagepickerview.R
import com.github.lhoyong.imagepickerview.model.Image
import com.github.lhoyong.imagepickerview.util.EXTRA_IMAGE
import com.github.lhoyong.imagepickerview.util.GlideApp
import kotlinx.android.synthetic.main.detail.*

class Detail : BaseActivity(R.layout.detail) {

    companion object {

        fun starterIntent(context: Context, image: Image): Intent {
            return Intent(context, Detail::class.java).apply {
                putExtra(EXTRA_IMAGE, image)
            }
        }
    }

    private val image by lazy { intent.getParcelableExtra<Image>(EXTRA_IMAGE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (image == null) {
            finish()
            return
        }

        image?.let {
            GlideApp.with(this)
                .load(it.path)
                .into(detail_image)
        }

    }
}