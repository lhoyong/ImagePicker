package com.lhoyong.imagepicker.util

import android.content.Context

object StringUtil {

    fun getStringRes(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }

    fun getStringRes(context: Context, resId: Int, count: Int): String {
        return String.format(context.resources.getString(resId), count)
    }

    fun getStringRes(context: Context, resId: Int, count: Int, max: Int): String {
        return String.format(context.resources.getString(resId), count, max)
    }
}
