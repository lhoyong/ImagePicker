package com.lhoyong.imagepicker.model

import android.os.Parcelable
import com.lhoyong.imagepicker.util.RESULT_NAME
import kotlinx.android.parcel.Parcelize

/**
 *  @param max Selected Image Max Count
 *  @param name used `onActivityResult` intent name default : "images"
 *  @param title toolbar title text, default =  selected image size
 *  @param single selected one image
 * */
@Parcelize
data class SetUp(
    var max: Int = 30,
    var name: String = RESULT_NAME,
    var title: String? = null,
    var single: Boolean = false
) : Parcelable {

    fun max(action: () -> Int) {
        max = action()
    }

    fun name(action: () -> String) {
        name = action()
    }

    fun title(action: () -> String?) {
        title = action()
    }

    fun single(action: () -> Boolean) {
        single = action()
    }

    fun build(): SetUp = this
}
