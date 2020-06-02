package com.lhoyong.imagepicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  @param max Selected Image Max Count
 *  @param name used `onActivityResult` intent name default : "images"
 * */
@Parcelize
class SetUp(val max: Int, val name: String) : Parcelable
