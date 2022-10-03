package com.blitzmobileapp.imagepicker;


import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.blitzmobileapp.imagepicker.HelperFunctions.PICK_FROM_CAMERA;
import static com.blitzmobileapp.imagepicker.HelperFunctions.PICK_FROM_GALLERY;
import static com.blitzmobileapp.imagepicker.HelperFunctions.createObject;
import static com.blitzmobileapp.imagepicker.HelperFunctions.encodeImage;
import static com.blitzmobileapp.imagepicker.HelperFunctions.mapOptions;
import static com.blitzmobileapp.imagepicker.HelperFunctions.mapToArray;
import static com.blitzmobileapp.imagepicker.HelperFunctions.setSelectionType;
import static com.blitzmobileapp.imagepicker.HelperFunctions.storeInCacheWithUri;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


public class ImagePickerModule extends ReactContextBaseJavaModule {

    //Written By Ayesh
    public Callback callback;
    static Uri imageUri = null;
    public static WritableArray imageUriArray = Arguments.createArray();
    public static String selection;
    public static Boolean includeBase64;
    public static Boolean selectMultiple;
    public static int selectionLimit;
    public static float compressionRatio;
    public static ReactApplicationContext reactContext;

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
//        selection = [options valueForKey:@"selection"];
//        includeBase64 = [[options valueForKey:@"includeBase64" ]boolValue];
//        selectMultiple = [[options valueForKey:@"selectMultiple" ]boolValue];
//        selectionLimit = [[options valueForKey:@"selectionLimit"] intValue];
//        compressionRatio = [[options valueForKey:@"compressionRatio"] floatValue];

        try {
            mapOptions(options);
            if (setSelectionType(Objects.requireNonNull(selection)) == 1) {
                Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(reactContext.getCacheDir(), UUID.randomUUID() + ".png");

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
                if (selectMultiple) galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(getCurrentActivity(), galleryIntent, PICK_FROM_GALLERY, null);
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
                            callback.invoke(imageUri.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case PICK_FROM_GALLERY:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            if (selectMultiple) {
                                mapToArray(Objects.requireNonNull(Objects.requireNonNull(data).getClipData()), callback, reactContext);
                            } else {
                                imageUri = data.getData();
                                UCrop uCrop = UCrop
                                        .of(imageUri, Uri.fromFile(new File(reactContext.getCacheDir(), new Date().toString() + ".png")));
                                uCrop.start(reactContext.getCurrentActivity());
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case UCrop.REQUEST_CROP:
                    imageUri = UCrop.getOutput(data);
                    callback.invoke(createObject(storeInCacheWithUri(imageUri)));


            }
        }
    };
    @ReactMethod
    public static void uploadImage(Uri uri){
        File image = new File(uri.getPath());
        FileBody fileBody = new FileBody(image);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addTextBody("params", "{....}")
                .addPart("my_file", fileBody);
        HttpEntity multiPartEntity = builder.build();

        String url = "....";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(multiPartEntity);

    }


}

