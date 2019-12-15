package com.github.lhoyong.imagepickerview.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
            throw IllegalArgumentException("Missing Image")
        }

        image?.let {
            GlideApp.with(this)
                .load(it.path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                })
                .into(detail_image)
        }

    }
}