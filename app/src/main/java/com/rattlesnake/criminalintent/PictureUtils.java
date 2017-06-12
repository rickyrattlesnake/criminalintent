package com.rattlesnake.criminalintent;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

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

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
            .getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    private static Bitmap rotateBitmap(Bitmap bm) {
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
}
