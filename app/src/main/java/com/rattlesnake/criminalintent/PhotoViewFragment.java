package com.rattlesnake.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PhotoViewFragment extends DialogFragment {
    private static String ARG_IMAGE_FILE = "image_file_arg";
    private ImageView mPhotoView;
    private File mPhotoFile;

    public static PhotoViewFragment newInstance(File date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE_FILE, date);

        PhotoViewFragment pvf = new PhotoViewFragment();
        pvf.setArguments(args);
        return pvf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPhotoFile = (File) getArguments().getSerializable(ARG_IMAGE_FILE);

        View v = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_photo, null);
        mPhotoView = (ImageView) v.findViewById(R.id.dialog_crime_photo);

        renderPhoto();

        return new AlertDialog.Builder(getActivity())
            .setView(v)
            .setPositiveButton(android.R.string.ok, null)
            .create();
    }

    private void renderPhoto() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {

            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), 1);
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
