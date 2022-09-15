package com.blitzmobileapp.imagepicker;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerFragment extends Fragment {
    public static ImagePicker imagePicker;


    private Uri getUriFromPath(String filePath) {
        Log.d("FilePath: ", "getUriFromPath: " + filePath);
        long photoId;
        Uri photoUri = MediaStore.Images.Media.getContentUri("external");
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        // TODO This will break if we have no matching item in the MediaStore.
        Cursor cursor = getActivity().getContentResolver().query(photoUri, projection, MediaStore.Images.ImageColumns.DATA + " LIKE ?", new String[]{filePath}, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        photoId = cursor.getLong(columnIndex);

        cursor.close();
        return Uri.parse(photoUri.toString() + "/" + photoId);
    }
    private void selectVideo() {
        List<Intent> targets = new ArrayList<Intent>();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.putExtra(Intent.ACTION_CAMERA_BUTTON,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        List<ResolveInfo> candidates = this.getActivity().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            Log.e("PackageName: ", packageName);
            if (packageName.equals("com.google.android.apps.photos")) {
                Intent iWantThis = new Intent();
                iWantThis.setType("image/*");
                iWantThis.setAction(Intent.ACTION_GET_CONTENT);
                iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                iWantThis.setPackage(packageName);
                targets.add(iWantThis);
            }
        }
        Intent chooser = Intent.createChooser(targets.remove(0), "Select Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
//        startActivityForResult(chooser, 1);
//        getCurrentActivity().startActivityForResult(Intent.createChooser(intent,"Select Video"),2);

        this.startActivityForResult(chooser,2);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
//                        imagePicker.create(childFragment.getActivity())// Activity or Fragment
//                        .start();
        selectVideo();
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (imagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
//            openCropper(image);

        }
    }
}
