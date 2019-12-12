package com.github.lhoyong.imagepickerview.util

import android.app.Activity
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair

fun Activity.toOptionCompat(view: View,resId: Int): ActivityOptionsCompat{
    val imageView = view.findViewById<View>(resId)
    val pair = Pair(imageView, imageView.transitionName)
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair)
}