package com.rattlesnake.criminalintent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        float srcWidth = opts.outWidth;
        float srcHeight = opts.outHeight;

        // Only scale down if necessary
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        return getScaledBitmap(path, inSampleSize);
    }

    public static Bitmap getScaledBitmap(String path, int inSampleSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;
        return rotateBitmap(BitmapFactory.decodeFile(path, opts));
    }

    public static void attachScaledBitmapToView(final ImageView imgView, final File imgFile) {
        imgView.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (imgFile == null || !imgFile.exists()) {
                        imgView.setImageDrawable(null);
                    } else {
                        int height = imgView.getHeight();
                        int width = imgView.getWidth();
                        Bitmap bitmap = PictureUtils.getScaledBitmap(imgFile.getPath(),
                                                                     width,
                                                                     height);
                        imgView.setImageBitmap(bitmap);
                    }

                }
            });
    }

    private static Bitmap rotateBitmap(Bitmap bm) {
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
}
