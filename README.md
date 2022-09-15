# Getting Started

###### Installation
`yarn add git+https://salsoftmobile@bitbucket.org/salsoftmobileapphq/rn-image-picker.git` 

**--OR--** 

`npm install --save git+https://salsoftmobile@bitbucket.org/salsoftmobileapphq/rn-image-picker.git` 

   
> Automatic Linking enabled for both ios and android, but we need to do a few additional platform specific steps

###### Android
> Add the following permissions in AndroidManifest.xml file


   
   `
   <uses-feature android:name="android.hardware.camera"/>
   `
   <br />
    `
    <uses-feature android:name="android.hardware.camera.autofocus"/>`
     <br />
    `<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    `
     <br />
    `<uses-permission android:name="android.permission.RECORD_AUDIO"/>`
     <br />
    `<uses-permission android:name="android.permission.FLASHLIGHT"/>`
     <br />
    `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />`
     <br />
    `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     <br />
    `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
     <br />
	`<uses-permission android:name="android.permission.CAMERA"/>`
    
> Add the following activity in AndroidManifest.xml file inside application tag
> 
`       <activity
            android:name="com.yalantis.ucrop.UCropActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>`

> Add the following depandency in app/build.gradle file as dependency
> 
`implementation 'com.github.yalantis:ucrop:2.2.4-native'`


###### IOS
> Add the following line in your project's podfile

`'rn-image-picker', :path => '../node_modules/react-native-toast/rn-image-picker.podspec'`

> Add **NSCameraUsageDescription** in info.plist

> run cd ios in terminal

> run pod install in terminal



# Usage

`import imagePicker from 'rn-image-picker`
 <br />
 `
 imagePicker.open(success=>{
 	// do something with image
 },error=>{
 // error handling
 })
 
 `# RNImagePicker
