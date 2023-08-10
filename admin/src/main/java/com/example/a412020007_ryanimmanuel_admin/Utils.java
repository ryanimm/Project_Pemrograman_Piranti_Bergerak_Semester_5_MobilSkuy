package com.example.a412020007_ryanimmanuel_admin;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class Utils {

    public static void move(Context context, Class destination) {
        Intent i = new Intent(context, destination);
        context.startActivity(i);
    }

    public static void moveWithExtra(Context context, Class destination, String key, String extra) {
        Intent i = new Intent(context, destination);
        i.putExtra(key, extra);
        context.startActivity(i);
    }

    public static void moveWithExtra(Context context, Class destination, String key, HashMap<String, Object> extra) {
        Intent i = new Intent(context, destination);
        i.putExtra(key, extra);
        context.startActivity(i);
    }

    public static void moveClearBackstack(Context context, Class destination) {
        Intent i = new Intent(context, destination);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
