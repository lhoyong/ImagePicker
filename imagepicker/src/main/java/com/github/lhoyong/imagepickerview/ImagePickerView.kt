package com.github.lhoyong.imagepickerview

import android.app.Activity
import android.content.Context
import com.github.lhoyong.imagepickerview.core.Config
import com.github.lhoyong.imagepickerview.core.toSetup
import com.github.lhoyong.imagepickerview.ui.Gallery

class ImagePickerView {

    class Builder {

        private companion object {
            private const val REQUEST_CODE = 3030
        }

        private var config: Config? = null

        fun setup(action: () -> Config) = apply { config = action() }

        fun start(context: Context, requestCode: Int? = null) {
            check(context is Activity) { "Check for context is Activity" }
            context.startActivityForResult(
                Gallery.starterIntent(
                    context,
                    config?.toSetup()
                ),
                requestCode ?: REQUEST_CODE
            )

        }
    }
}