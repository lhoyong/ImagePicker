# ImagePickerView

![DOWNLOAD](https://jitpack.io/v/lhoyong/ImagePickerView.svg)



This is Simple Android ImagePicker Library.

Support DayNight Mode.



## OverView

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/overview.gif" width = "264" height = "464"/>



## ScreenShot

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/1.png" width = "264" height = "464"/>

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/2.png" width = "264" height = "464"/>

<img src="https://github.com/lhoyong/ImagePickerView/blob/master/art/3.png" width = "264" height = "464"/>



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
val config = Config.Builder()
                   .max(5)
                   .build()

ImagePickerView.Builder()
   .config(config)
   .onImageLoaderListener(this)
   .build(supportFragmentManager)
~~~~



Finish image select task, update ui for `fun onLoad(uriList: List<Uri>)` 

~~~~kotlin
override fun onLoad(uriList: List<Uri>) {
        (recycler_view.adapter as ImageAdapter).submitList(uriList)
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
