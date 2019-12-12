package com.github.lhoyong.imagepickerview

import android.content.Context
import com.github.lhoyong.imagepickerview.core.Config
import com.github.lhoyong.imagepickerview.core.ImageCallbackListener
import com.github.lhoyong.imagepickerview.core.toSetup
import com.github.lhoyong.imagepickerview.ui.Gallery

class ImagePickerView {

    class Builder {
        private var config: Config? = null
        private var builderListener: ImageCallbackListener? = null

        fun setup(action: () -> Config) = apply { config = action() }

        fun onImageLoaderListener(l: ImageCallbackListener?) =
            apply { l?.let { builderListener = it } }

        fun start(context: Context) {
            context.startActivity(Gallery.starterIntent(context, config?.toSetup()))
        }
    }
}