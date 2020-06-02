package com.lhoyong.imagepicker

import android.app.Activity
import androidx.fragment.app.Fragment
import com.lhoyong.imagepicker.core.Config
import com.lhoyong.imagepicker.core.toSetup
import com.lhoyong.imagepicker.ui.Gallery

class ImagePickerView {

    class Builder {

        private var config: Config? = null

        fun setup(action: () -> Config) = apply { config = action() }

        fun start(activity: Activity, requestCode: Int? = null) {
            activity.startActivityForResult(
                Gallery.starterIntent(
                    activity,
                    config?.toSetup()
                ),
                requestCode ?: REQUEST_CODE
            )
        }

        fun start(fragment: Fragment, requestCode: Int? = null) {
            fragment.startActivityForResult(
                Gallery.starterIntent(
                    fragment.requireContext(),
                    config?.toSetup()
                ),
                requestCode ?: REQUEST_CODE
            )
        }
    }

    companion object {
        private const val REQUEST_CODE = 3030
    }
}
