# ImagePickerView

![DOWNLOAD](https://img.shields.io/bintray/v/lhoyong/maven/com.lhoyong:imagepicker)
![Build](https://github.com/lhoyong/ImagePicker/workflows/Build/badge.svg)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePickerView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7976)


This is Simple Android ImagePicker Library.

Support DayNight Mode.



## OverView

<img src="https://github.com/lhoyong/ImagePicker/blob/master/art/anim.gif" width = "264" height = "464"/>



## ScreenShot

<img src="https://github.com/lhoyong/ImagePicker/blob/master/art/3.png?raw=true" width = "264" height = "464"/><img src="https://github.com/lhoyong/ImagePicker/blob/master/art/4.png?raw=true" width = "264" height = "464"/>

## Setup

Add the dependency

~~~~xml
dependencies {
    implementation "com.lhoyong:imagepicker:$latestVersion"
}
~~~~



## Usage

Add ImagePickerView your Activity or Fragments  [Example](https://github.com/lhoyong/ImagePicker/blob/master/sample/src/main/java/com/lhoyong/imagepicker/MainActivity.kt).

When you [Click on the Image](https://github.com/lhoyong/ImagePicker/blob/master/imagepicker/src/main/java/com/lhoyong/imagepicker/adapter/GalleryAdapter.kt#L49), it will move to [Detail](https://github.com/lhoyong/ImagePicker/blob/master/imagepicker/src/main/java/com/lhoyong/imagepicker/ui/Detail.kt) Screen.
If [Long Click](https://github.com/lhoyong/ImagePicker/blob/master/imagepicker/src/main/java/com/lhoyong/imagepicker/adapter/GalleryAdapter.kt#L57) Image, start scale Animations and visible checkbox. Bul Already Clicked Images, can not move to Detail Screen.




- Single Select

~~~~kotlin
ImagePickerView.Builder()
    .setup {
        name { RESULT_NAME }
        title { "Image Picker" } // optional
        single { true } 
    }
    .start(this)
~~~~


- Multiple Select

~~~~kotlin
ImagePickerView.Builder()
    .setup {
        max { 5} // default 30
        name { RESULT_NAME }
        title { "Image Picker" } // optional, if not used toolbar title display selected image count
        single { false }  // optional, single = false
    }
    .start(this)
~~~~


Finish image select task, update ui for `onActivityResult` received data. 

~~~~kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val images = data?.getParcelableArrayListExtra<Uri>(RESULT_NAME)
            images?.let {
                (recycler_view.adapter as ImageAdapter).submitList(it)
            }
        }
    }
~~~~



## License

	Copyright (C) 2020 lhoyong.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
