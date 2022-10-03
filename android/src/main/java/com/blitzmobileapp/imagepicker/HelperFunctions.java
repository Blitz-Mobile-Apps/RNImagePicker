package com.blitzmobileapp.imagepicker;

import static com.blitzmobileapp.imagepicker.ImagePickerModule.compressionRatio;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.imageUriArray;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.reactContext;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.selectionLimit;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.selection;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.includeBase64;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.selectMultiple;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class HelperFunctions {
    public static final int PICK_FROM_CAMERA = 1;
    public static final int PICK_FROM_GALLERY = 2;

    public static int setSelectionType(String SelectionTypeString) {
        if (SelectionTypeString.equals("camera")) {
            return PICK_FROM_CAMERA;
        } else {
            return PICK_FROM_GALLERY;
        }
    }

    public static String encodeImage(Uri imageUri, Context context) throws FileNotFoundException {

        final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;

    }

    public static void mapOptions(ReadableMap options) {
        if (options.hasKey("selection")) {
            selection = options.getString("selection");
        } else {
            selection = "camera";
        }

        if (options.hasKey("includeBase64")) {
            includeBase64 = options.getBoolean("includeBase64");
        } else {
            includeBase64 = false;
        }

        if (options.hasKey("selectMultiple")) {
            selectMultiple = options.getBoolean("selectMultiple");
        } else {
            selectMultiple = false;
        }

        if (options.hasKey("selectionLimit")) {
            selectionLimit = options.getInt("selectionLimit");
        } else {
            selectionLimit = 4;
        }

        if (options.hasKey("compressionRatio")) {
            compressionRatio = (float) options.getDouble("compressionRatio");
        } else {
            compressionRatio = 0;
        }
    }

    public static void mapToArray(ClipData data, Callback callback, Context reactContext) throws IOException {
        imageUriArray = Arguments.createArray();
        if (data.getItemCount() <= selectionLimit) {
            if (data != null) {
                for (int i = 0; i < data.getItemCount(); i++) {
                    imageUriArray.pushMap(createObject(Objects.requireNonNull(storeInCacheWithUri(data.getItemAt(i).getUri()))));
                }
                Log.d("ARRAY",imageUriArray.size()+"");
                Log.d("ARRAY",imageUriArray.toString()+"");
                callback.invoke(imageUriArray);

            }

        }
    }

    public static WritableMap createObject( Uri uri) {
        WritableMap obj = Arguments.createMap();
        obj.putString("uri", uri.toString());
        if(includeBase64){
            try {
                obj.putString("base64", encodeImage(uri,reactContext));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static Uri storeInCacheWithUri(Uri imageUri){
        File file = new File(reactContext.getCacheDir(), UUID.randomUUID() + ".webp");
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(reactContext.getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.WEBP, (int) compressionRatio * 100, ostream);
            ostream.close();
            Uri uri = FileProvider.getUriForFile(
                    reactContext,
                    reactContext.getApplicationContext().getPackageName() + ".provider",
                    file);
            return  uri;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
