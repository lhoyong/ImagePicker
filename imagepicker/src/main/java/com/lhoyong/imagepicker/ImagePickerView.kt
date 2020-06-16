package com.lhoyong.imagepicker

import android.app.Activity
import androidx.fragment.app.Fragment
import com.lhoyong.imagepicker.model.SetUp
import com.lhoyong.imagepicker.ui.Gallery

@DslMarker
annotation class ImagePickerViewDSL

class ImagePickerView {

    @ImagePickerViewDSL
    class Builder {

        private var setup: SetUp? = null

        fun setup(action: SetUp.() -> Unit) = apply { setup = SetUp().apply(action).build() }

        fun start(activity: Activity, requestCode: Int? = null) {
            activity.startActivityForResult(
                Gallery.starterIntent(
                    activity,
                    setup
                ),
                requestCode ?: REQUEST_CODE
            )
        }

        fun start(fragment: Fragment, requestCode: Int? = null) {
            fragment.startActivityForResult(
                Gallery.starterIntent(
                    fragment.requireContext(),
                    setup
                ),
                requestCode ?: REQUEST_CODE
            )
        }
    }

    companion object {
        private const val REQUEST_CODE = 3030
    }
}
