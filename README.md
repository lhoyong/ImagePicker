# ImagePickerView

![DOWNLOAD](https://jitpack.io/v/lhoyong/ImagePickerView.svg)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePickerView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7976)


This is Simple Android ImagePicker Library.

Support DayNight Mode.



## OverView

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/overview.gif" width = "264" height = "464"/><img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/detail.gif" width = "264" height = "464"/>



## ScreenShot

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/1.png?raw=true" width = "264" height = "464"/><img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/2.png?raw=true" width = "264" height = "464"/><img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/3.png?raw=true" width = "264" height = "464"/>

## Setup

Add root build.gradle

~~~~xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~~

Add the dependency

~~~~xml
dependencies {
	        implementation 'com.github.lhoyong:ImagePickerView:latestVersion'
	}
~~~~



## Usage

Add ImagePickerView your Activity or Fragments  [Example](https://github.com/lhoyong/ImagePickerView/blob/master/app/src/main/java/com/github/lhoyong/imagepickerview/MainActivity.kt).


~~~~kotlin
ImagePickerView.Builder()
            .setup {
                config {
                    name { RESULT_NAME }
                    max { 5 }
                }
            }
            .start(this)	// if used fragment, .start(requireContext())
~~~~



Finish image select task, update ui for `onActivityResult received data` 

For Example
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

	Copyright (C) 2019 lhoyong.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
