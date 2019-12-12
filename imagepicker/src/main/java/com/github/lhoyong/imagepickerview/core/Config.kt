package com.github.lhoyong.imagepickerview.core

import com.github.lhoyong.imagepickerview.model.SetUp

/**
 * ImagePickerView Configure
 * max()  [com.github.lhoyong.imagepickerview.core.Config]
 */

class Config {
    var max: Int = 30 // default

    fun max(action: () -> Int) {
        max = action()
    }

    fun build(): Config = this

}

fun config(action: Config.() -> Unit) = Config().apply(action).build()

fun Config?.toSetup() = SetUp(max = this?.max ?: 30)