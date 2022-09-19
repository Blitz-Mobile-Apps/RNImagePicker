package com.blitzmobileapp.imagepicker;


import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.blitzmobileapp.imagepicker.Utils.PICK_FROM_CAMERA;
import static com.blitzmobileapp.imagepicker.Utils.PICK_FROM_GALLERY;
import static com.blitzmobileapp.imagepicker.Utils.encodeImage;
import static com.blitzmobileapp.imagepicker.Utils.setSelectionType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;


public class ImagePickerModule extends ReactContextBaseJavaModule {

    //Written By Ayesh
    public Callback callback;
    static Uri imageUri = null;
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
    public void openImagePicker(ReadableMap options, Callback callback) {

        //Written By Ayesh
        imageUri = null;
        this.callback = callback;
        try {
            if (options.hasKey("selection")) {
                if (setSelectionType(Objects.requireNonNull(options.getString("selection"))) == 1) {
                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(reactContext.getCacheDir(), new Date().toString() + ".jpg");

                    imageUri = FileProvider.getUriForFile(
                            reactContext,
                            getReactApplicationContext().getPackageName() + ".provider",
                            file);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    startActivityForResult(
                            getCurrentActivity(),
                            captureIntent,
                            PICK_FROM_CAMERA,
                            null);
                    if (imageUri == null) {
                        callback.invoke("Some Error Occured");
                    }
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(getCurrentActivity(), galleryIntent, PICK_FROM_GALLERY, null);
                }
            }
        } catch (Exception ex) {
            Log.d("RNIMAGE_PICKER", ex.toString());
        }
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);
            switch (requestCode) {
                case PICK_FROM_CAMERA:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Log.d("BASE_64", encodeImage(imageUri, reactContext));
                            callback.invoke(imageUri.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case PICK_FROM_GALLERY:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            imageUri = data.getData();
                            Log.d("BASE_64", encodeImage(imageUri, reactContext));
                            UCrop uCrop = UCrop
                                    .of(imageUri, Uri.fromFile(new File(reactContext.getCacheDir(), new Date().toString() + ".jpg")));
                            uCrop.start(activity);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case UCrop.REQUEST_CROP:
                    imageUri = UCrop.getOutput(data);
                    callback.invoke(imageUri.toString());

            }
        }
    };
}

