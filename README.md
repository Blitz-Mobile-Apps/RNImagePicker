
# Getting Started

  

## Installation

`yarn add @blitzmobileapps/react-native-advanced-image-picker`
**--OR--**
`npm i @blitzmobileapps/react-native-advanced-image-picker`

> Automatic Linking enabled for both ios and android, but we need to do a few additional platform specific steps

  

### Android

> Add the following permissions in AndroidManifest.xml file

```
<uses-feature  android:name="android.hardware.camera"/>
<uses-feature  android:name="android.hardware.camera.autofocus"/>
<uses-permission  android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
<uses-permission  android:name="android.permission.RECORD_AUDIO"/>
<uses-permission  android:name="android.permission.FLASHLIGHT"/>
<uses-permission  android:name="android.permission.ACCESS_FINE_LOCATION"  />
<uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission  android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission  android:name="android.permission.CAMERA"/>
```

> Add the following activity in AndroidManifest.xml file inside application tag
``` 
<activity
android:name="com.yalantis.ucrop.UCropActivity"
android:screenOrientation="portrait"
android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>
```

> Add the following provider in AndroidManifest.xml file inside application tag
``` 
<provider
android:name="androidx.core.content.FileProvider"
android:authorities="com.blitzapp.animaladoption.android.provider"
android:exported="false"
android:grantUriPermissions="true">
	<meta-data
	android:name="android.support.FILE_PROVIDER_PATHS"
	android:resource="@xml/provider_paths"  />
</provider>
```  

> Create a file  **provider_paths.xml** in res/xml. If xml is not available in the res folder then create a folder **xml**.
> Add this code in ***provider_paths.xml***
``` 
<?xml  version="1.0"  encoding="utf-8"?>

<paths>
	<files-path
	name="share"
	path="external_files"/>
	<cache-path  name="cache"  path="."  />
</paths>
```  

> Add the following depandency in app/build.gradle file as dependency
`implementation 'com.github.yalantis:ucrop:2.2.4-native'`

  
  

### IOS

> Add the following line in your project's podfile

  

`'rn-image-picker', :path => '../node_modules/react-native-toast/rn-image-picker.podspec'`

  

> Add **NSCameraUsageDescription** in info.plist

  

> run cd ios in terminal

  

> run pod install in terminal

  
  
  

## Example

  

```
import {openPicker, open} from  '@blitzmobileapps/react-native-advanced-image-picker'

const options = { 
includeBase64: false, 
selectMultiple: false, 
compressionRatio: 0.001, 
}; 

try { 
	const respsonse = await openPicker(options); 
	const tempImage = respsonse?.uri?.split("."); 
	const type = tempImage[1]; 
	const imageObj = { 
		uri: `${respsonse?.uri}`, 
		type: `image/${type}`, 
		name: `image${Date.now()}.${type}`, 
	};
	
}catch(ex){
	console.log("Image Picker Exception",ex)
}
```

## API Reference
### Methods

####  openPicker()
Opens a dialog where the user can choose between **CAMERA** and **GALLERY**.
```
try { 
	// response can be handled here
	const respsonse = await openPicker(options); 
}catch(ex){
	// errors can be handled in the catch block.
}
```
####  openPicker()
Opens directly the **CAMERA** and **GALLERY** whatever is passed in options object.
```
try { 
	// response can be handled here
	const respsonse = await openPicker(options); 
}catch(ex){
	// errors can be handled in the catch block.
}
```

See [Options](#options) for further information on `options`.

### Options
| Name         |  Description                                                                                                                         
| - | - |
| selection|'camera' or 'gallery'.|
|mediaType|Only 'photo' can be used for now.|
| selectMultiple|multiple images can be selected only when selectMultiple is set to true.|
| selectionLimit|number of images to be selected. Only works when selectMultiple is true.|
| compressionRatio|0.1 means the lowest and 1 means the original size of image.|
| includeBase64|If it is set true then the response will contain base64 and uri both.|
