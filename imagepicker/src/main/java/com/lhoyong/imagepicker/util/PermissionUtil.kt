package com.lhoyong.imagepicker.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

internal object PermissionUtil {

    fun requestGalleryPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            requestCode
        )
    }

    fun hasGalleryPermissionDenied(context: Context, isDenied: (Boolean) -> Unit) {
        val permission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

        isDenied(permission)
    }
}
