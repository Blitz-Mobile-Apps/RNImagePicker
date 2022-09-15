package com.blitzmobileapp.imagepicker;



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ActivityEventListener;
//import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;


public class ImagePickerModule extends ReactContextBaseJavaModule {
  Boolean check = true;
  static Uri imageUri = null;
  static  Uri ImageUri = null;
  public Promise globalPromise = null;
//  static String tempImageUri = null;
  //  Options options;
  ArrayList<String> returnValue = new ArrayList<>();
  private static final int PICK_FROM_GALLERY = 2;

  private static ImagePicker imagePicker;
  private Promise mPickerPromise;

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

    @Override
    public void onActivityResult(Activity activity ,int requestCode, final int resultCode, Intent data) {
      Log.e("onActivityResult",Integer.toString(requestCode) );
      if(data == null){
        imageUri = Uri.parse("");
      }
      if (resultCode == -1 && requestCode == UCrop.REQUEST_CROP) {
        check = true;
        final Uri resultUri = UCrop.getOutput(data);
        Log.d("croppedImage: ", "onActivityResult: "+ resultUri);

        try {
          File compressedImageFile = new Compressor(reactContext).compressToFile(new File(resultUri.getPath()));

          int un_com_file_size = Integer.parseInt(String.valueOf(new File(resultUri.getPath()).length()/1024));

          int file_size = Integer.parseInt(String.valueOf(compressedImageFile.length()/1024));
          Log.e("UnCompressedImageSize:", Integer.toString(un_com_file_size) );

          Log.e("CompressedImageSize:", Integer.toString(file_size) );
          imageUri = Uri.fromFile(compressedImageFile);
          WritableMap params = Arguments.createMap();


          if (imageUri != null) {
            try {
              final InputStream imageStream = getCurrentActivity().getContentResolver().openInputStream(imageUri);
              final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
              String encodedImage = encodeImage(selectedImage);
              JSONObject image = new JSONObject();
              image.put("uri",imageUri.toString());
              image.put("data",encodedImage);
              params.putString("uri", imageUri.toString());
              params.putString("data", encodedImage);
//              sendEvent(reactContext, "onSuccess", params);
              if(globalPromise != null){
                globalPromise.resolve(params);
                globalPromise = null;
              }

              imageUri = null;
            } catch (FileNotFoundException e) {
              if(globalPromise != null){
                globalPromise.reject(e);
                globalPromise = null;
              }
              e.printStackTrace();
            } catch (JSONException e) {
              e.printStackTrace();
              if(globalPromise != null){
                globalPromise.reject(e);
                globalPromise = null;
              }
            }
          }




        } catch (IOException e) {
          e.printStackTrace();
          WritableMap params = Arguments.createMap();
          params.putString("error", e.toString());
//          sendEvent(reactContext, "onError", params);
          if(globalPromise != null){
            globalPromise.reject(e);
            globalPromise = null;
          }
        }
      } else if (resultCode == UCrop.RESULT_ERROR) {
        check = true;

        final Throwable cropError = UCrop.getError(data);
        Log.d("croppedImage: Error ", cropError.getMessage());
        if(globalPromise != null){
          globalPromise.reject(cropError);
          globalPromise = null;
        }
      }
      if (requestCode == 2 && resultCode == -1) {

//        Bundle bundle = data.getExtras();

//        Log.e("onActivityResult data ", data.toString() );
        Uri imageFile = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
          if(data == null || data.getData() == null){
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
              if (temp.getName().equals("eo.webp")) {
                f = temp;
                break;
              }
            }

            if (!f.exists()) {
              Log.e("onActivityResult error:","Error while capturing image" );
              return;
//            Toast.makeText(getBaseContext(),
//
//                    "Error while capturing image", Toast.LENGTH_LONG)
//
//                    .show();
//
//            return;

            }



            imageFile = Uri.fromFile(f);






          } else {
            imageFile = data.getData();

          }
        } else {

          if(data == null || data.getData() == null){
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
              if (temp.getName().equals("eo.webp")) {
                f = temp;
                break;
              }
            }

            if (!f.exists()) {
              Log.e("onActivityResult error:","Error while capturing image" );
              return;
//            Toast.makeText(getBaseContext(),
//
//                    "Error while capturing image", Toast.LENGTH_LONG)
//
//                    .show();
//
//            return;

            }



            imageFile = Uri.fromFile(f);






          } else {
            imageFile = data.getData();

          }

        }
//      if (imagePicker.shouldHandle(requestCode, resultCode, data)) {
        // Get a list of picked images
//        List<Image> images = ImagePicker.getImages(data);
//        // or get a single image only
//        Image image = ImagePicker.getFirstImageOrNull(data);

//        Log.d("SelectedImage", "onActivityResult: "+ getUriFromPath(imageFile.toString()));
        check = false;

        File tempFile = null;
        try {
          tempFile = File.createTempFile("crop", ".png", Environment
                  .getExternalStorageDirectory());
        } catch (IOException e) {
          e.printStackTrace();
        }
        Uri tempUri = null;
        tempUri = Uri.fromFile(tempFile);


        getCurrentActivity().getIntent().putExtra("output", tempUri);
        getCurrentActivity().getIntent().putExtra("outputFormat", "PNG");
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        UCrop.of(imageFile,tempUri).withOptions(options)
                .start(getCurrentActivity());
      }

      super.onActivityResult(getCurrentActivity(), requestCode, resultCode, data);
    }
    //

  };


  private Uri getUriFromPath(String filePath) {
    Log.d("FilePath: ", "getUriFromPath: " + filePath);
    long photoId;
    Uri photoUri = MediaStore.Images.Media.getContentUri("external");
    String[] projection = {MediaStore.Images.ImageColumns._ID};
    // TODO This will break if we have no matching item in the MediaStore.
    Cursor cursor = reactContext.getContentResolver().query(photoUri, projection, MediaStore.Images.ImageColumns.DATA + " LIKE ?", new String[]{filePath}, null);
    cursor.moveToFirst();

    int columnIndex = cursor.getColumnIndex(projection[0]);
    photoId = cursor.getLong(columnIndex);

    cursor.close();
    return Uri.parse(photoUri.toString() + "/" + photoId);
  }
  private ReactApplicationContext reactContext;



  ImagePickerModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @NonNull
  @Override
  public String getName() {
    return "imagePicker";
  }

  @ReactMethod
  public void getImage(Callback callback) {

    callback.invoke(imageUri.toString());

  }
  private String encodeImage(Bitmap bm)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
    return encImage;
  }
  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  private void selectImage(Promise promise) {

    if (ContextCompat.checkSelfPermission(getCurrentActivity(),
            Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getCurrentActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(getCurrentActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

      if(ContextCompat.checkSelfPermission(getCurrentActivity(),
              Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(getCurrentActivity(),
              Manifest.permission.READ_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_DENIED|| ContextCompat.checkSelfPermission(getCurrentActivity(),
              Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_DENIED){
        Toast.makeText(getReactApplicationContext(), "You need to grant all permissions to continue", Toast.LENGTH_LONG).show();
        promise.reject(new Throwable("You need to grant all permissions to continue"));
      }

      ActivityCompat.requestPermissions(getCurrentActivity(),
              new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
              20);
      // Permission is not granted
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(),
              Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(),
              Manifest.permission.READ_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(),
              Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        ActivityCompat.requestPermissions(getCurrentActivity(),
                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                20);
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(getCurrentActivity(),
                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                20);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    } else {
      // Permission has already been granted
      List<Intent> targets = new ArrayList<Intent>();
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
      intent.putExtra(Intent.ACTION_CAMERA_BUTTON,true);
      intent.setAction(Intent.ACTION_GET_CONTENT);
      List<ResolveInfo> candidates = getCurrentActivity().getPackageManager().queryIntentActivities(intent, 0);

      for (ResolveInfo candidate : candidates) {
        String packageName = candidate.activityInfo.packageName;
        Log.e("PackageName: ", packageName);
        if (packageName.equals("com.google.android.apps.photos")) {
          Intent iWantThis = new Intent();
          iWantThis.setType("image/*");
          iWantThis.setAction(Intent.ACTION_GET_CONTENT);
//        iWantThis.putExtra(Intent.ACTION_CAMERA_BUTTON,true);
          iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
          iWantThis.setPackage(packageName);
          targets.add(iWantThis);
        }
      }
      Intent iWantThisCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    iWantThis.setType("image/*");
//    iWantThis.setAction(Intent.ACTION_CAMERA_BUTTON);
//        iWantThis.putExtra(Intent.ACTION_CAMERA_BUTTON,true);
//    iWantThis.setAction(Intent.ACTION_CAMERA_BUTTON);
//    iWantThis.setPackage(packageName);
      File f = new File(android.os.Environment
              .getExternalStorageDirectory(), "eo.webp");
      ImageUri = Uri.fromFile(f);
      iWantThisCamera.putExtra(MediaStore.EXTRA_OUTPUT,
              Uri.fromFile(f));
      targets.add(iWantThisCamera);


      Intent chooser = Intent.createChooser(targets.remove(0), "Select Image");
      chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
//        startActivityForResult(chooser, 1);
//        getCurrentActivity().startActivityForResult(Intent.createChooser(intent,"Select Video"),2);
      globalPromise = promise;
      getCurrentActivity().startActivityForResult(chooser,2);
    }


  }

  @ReactMethod
  public void openImagePicker(Promise promise) {
    imageUri = null;

//    ImagePicker.create(getCurrentActivity())
//            .single()
//            .start();
    selectImage(promise);

  }



}
