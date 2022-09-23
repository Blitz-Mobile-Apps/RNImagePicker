package com.blitzmobileapp.imagepicker;

import static com.blitzmobileapp.imagepicker.ImagePickerModule.compressionRatio;
import static com.blitzmobileapp.imagepicker.ImagePickerModule.imageUriArray;
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
        if (data.getItemCount() >= selectionLimit) {
            if (data != null) {
                for (int i = 0; i < selectionLimit; i++) {
                    File file = new File(reactContext.getCacheDir(), UUID.randomUUID() + ".png");
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(reactContext.getContentResolver(), data.getItemAt(i).getUri());
                    bitmap.compress(Bitmap.CompressFormat.PNG, (int) compressionRatio * 100, ostream);
                    ostream.close();
                    Uri uri = FileProvider.getUriForFile(
                            reactContext,
                            reactContext.getApplicationContext().getPackageName() + ".provider",
                            file);
                    imageUriArray.pushMap(createObject("uri", uri.toString()));
                }
                callback.invoke(imageUriArray);

            }

        }
    }

    public static WritableMap createObject(String key, String value) {
        WritableMap obj = Arguments.createMap();
        obj.putString(key, value);
        return obj;
    }
}
