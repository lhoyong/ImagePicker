package com.lhoyong.sample_navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lhoyong.imagepicker.ImagePickerView
import com.lhoyong.imagepicker.core.config
import kotlinx.android.synthetic.main.fragment_navigation.open
import kotlinx.android.synthetic.main.fragment_navigation.recycler_view

class NavigationFragment : Fragment() {

    private companion object {
        private const val RESULT_NAME = "result"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
            adapter = NavigationAdapter()
        }

        open.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        ImagePickerView.Builder()
            .setup {
                config {
                    name { RESULT_NAME }
                    max { 5 }
                }
            }
            .start(this, 33)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 33) {
                val images = data?.getParcelableArrayListExtra<Uri>(RESULT_NAME)
                images?.let {
                    println(it)
                    (recycler_view.adapter as NavigationAdapter).submitList(it.toList())
                }
            }

        }
    }
}
