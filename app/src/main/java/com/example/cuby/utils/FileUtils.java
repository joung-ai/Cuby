package com.example.cuby.utils;

import android.content.Context;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {
    public static String saveBitmap(Context context, Bitmap bitmap, String filename) {
        File directory = context.getDir("drawings", Context.MODE_PRIVATE);
        File file = new File(directory, filename);
        if (file.exists()) file.delete();
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
