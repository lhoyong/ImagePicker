package com.github.lhoyong.imagepicker.core

import com.github.lhoyong.imagepicker.model.SetUp
import com.github.lhoyong.imagepicker.util.RESULT_NAME

/**
 * ImagePickerView Configure
 * max()  [com.github.lhoyong.imagepicker.core.Config]
 * name : used `onActivityResult` intent name default : "images"
 */
class Config {
    var max: Int = 30 // default

    var name: String = RESULT_NAME

    fun max(action: () -> Int) {
        max = action()
    }

    fun name(action: () -> String) {
        name = action()
    }

    fun build(): Config = this

}

fun config(action: Config.() -> Unit) = Config()
    .apply(action).build()

fun Config?.toSetup() =
    SetUp(
        max = this?.max ?: 30,
        name = this?.name ?: RESULT_NAME
    )