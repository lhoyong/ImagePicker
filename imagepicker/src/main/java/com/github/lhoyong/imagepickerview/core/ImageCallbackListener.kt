package com.github.lhoyong.imagepickerview.core

import android.net.Uri

interface ImageCallbackListener {

    fun onLoad(uriList: List<Uri>)
}