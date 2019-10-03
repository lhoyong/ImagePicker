package com.github.lhoyong.imagepicker.core

import android.net.Uri

interface ImageCallbackListener {

    fun onLoad(uriList: List<Uri>)
}