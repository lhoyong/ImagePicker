package com.lhoyong.imagepicker.util

import android.app.Activity
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair

internal fun Activity.toOptionCompat(view: View, resId: Int): ActivityOptionsCompat {
    val imageView = view.findViewById<View>(resId)
    val pair = Pair(imageView, imageView.transitionName)
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair)
}
