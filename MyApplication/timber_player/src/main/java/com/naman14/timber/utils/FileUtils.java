package com.naman14.timber.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 宋尧
 * date on 2016/9/18.
 * des：
 */
public class FileUtils {
    public static String copyFile(Context context,String outfilepath,String infilename) {
        // File filesDir = getFilesDir();
        File destFile = new File(outfilepath);// 要拷贝的目标地址

        if (destFile.exists()) {
            return destFile.getPath();
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            in = context.getAssets().open(infilename);
            out = new FileOutputStream(destFile);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return destFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean deleteFile(String filePath){
        File destFile = new File(filePath);
        if (destFile.exists()) {
            return destFile.delete();
        }
        return false;
    }

    public static void saveBitmap(Bitmap photo,String filePath) {
        File f = new File(filePath);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
