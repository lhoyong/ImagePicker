package com.lhoyong.imagepicker.util

import android.view.View
import android.view.animation.DecelerateInterpolator

internal inline fun scaleStart(view: View, crossinline finish: () -> Unit) {
    view.animate()
        .scaleX(0.85f)
        .scaleY(0.85f)
        .setDuration(200)
        .setInterpolator(DecelerateInterpolator())
        .withEndAction {
            view.scaleX = 0.85f
            view.scaleY = 0.85f
            finish()
        }.start()
}

internal inline fun scaleRevert(view: View, crossinline finish: () -> Unit) {
    view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(200)
        .setInterpolator(DecelerateInterpolator())
        .withEndAction {
            view.scaleX = 1f
            view.scaleY = 1f
            finish()
        }.start()
}
