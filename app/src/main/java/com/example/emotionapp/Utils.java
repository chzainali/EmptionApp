package com.example.emotionapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String formatDate(Long time){
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(time));
    }

    public static String formatDateMonth(Long time) {
        return new SimpleDateFormat("dd MMM").format(new Date(time));
    }

    public static Drawable getIcon(Context context, int mood) {
        switch (mood) {
            case 0:
                return ContextCompat.getDrawable(context, R.drawable.happy);
            case 1:
                return ContextCompat.getDrawable(context, R.drawable.smile);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.neutral);
            case 3:
                return ContextCompat.getDrawable(context, R.drawable.sad);
            case 4:
                return ContextCompat.getDrawable(context, R.drawable.angry);
            default:
                return ContextCompat.getDrawable(context, R.drawable.neutral);
        }
    }


    public static File saveBitmapToTempFile(Context context, Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("emotion_image", ".png", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
