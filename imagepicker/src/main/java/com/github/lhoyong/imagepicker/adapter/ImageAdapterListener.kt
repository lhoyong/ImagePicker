package com.github.lhoyong.imagepicker.adapter

import androidx.recyclerview.selection.SelectionTracker

interface ImageAdapterListener {
    fun  setSelectedTracker(tracker: SelectionTracker<Long>)
}