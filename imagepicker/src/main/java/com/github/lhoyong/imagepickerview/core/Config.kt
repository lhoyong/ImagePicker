package com.github.lhoyong.imagepickerview.core

/**
 * ImagePickerView Configure
 * max()  [com.github.lhoyong.imagepickerview.core.Config]
 */
class Config(val maximumSize: Int? = null) {


    data class Builder(
        var maximumSize: Int? = null
    ) {
        /**
         *  @param max Select Size
         *
         */
        fun max(max: Int) = apply { this.maximumSize = max }

        fun build() = Config(maximumSize)
    }
}