package com.rahul.media.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.rahul.media.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by rahul on 6/5/2016.
 */

public class MediaUtility {
    public static String getUserImageDir(Context mContext) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/MultipleImageCache/data/" + mContext.getPackageName() + "/images";
    }

    public static File initializeImageLoader(Context mContext) {
        File cacheDir = new File(getUserImageDir(mContext));
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir;
    }

    public static File createImageFile2(Context mContext) throws IOException {

        File image = null;

        // Create an image file name
        String timeStamp = convertNumbersToEnglish(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()));

        String imageFileName = "JPEG_S_" + timeStamp + "_";

//        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            File storageDir = new File(Environment.getExternalStorageDirectory(),
//                    mContext.getString(R.string.imagepicker_parent));
//            boolean parentCreationResult = storageDir.mkdirs();
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        } else {

            File storageDir = mContext.getCacheDir();
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

     //   }

//        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        } else {
//
//            File storageDir = mContext.getFilesDir();
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        }

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public static Uri createImageFile(Context mContext) throws IOException {

        File image = null;

        // Create an image file name
        String timeStamp = convertNumbersToEnglish(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()));

        String imageFileName = "JPEG_S_" + timeStamp + "_";

//        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            File storageDir = new File(Environment.getExternalStorageDirectory(),
//                    mContext.getString(R.string.imagepicker_parent));
//            boolean parentCreationResult = storageDir.mkdirs();
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        } else {

            File storageDir = mContext.getCacheDir();
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

       // }

//        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        } else {
//
//            File storageDir = mContext.getFilesDir();
//            image = File.createTempFile(
//                    imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
//
//        }

        // Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static String convertNumbersToEnglish(String str) {
        String answer = str;
        answer = answer.replace("١", "1");
        answer = answer.replace("٢", "2");
        answer = answer.replace("٣", "3");
        answer = answer.replace("٤", "4");
        answer = answer.replace("٥", "5");
        answer = answer.replace("٦", "6");
        answer = answer.replace("٧", "7");
        answer = answer.replace("٨", "8");
        answer = answer.replace("٩", "9");
        answer = answer.replace("٠", "0");
        return answer;
    }

    public static File createImageFile3(Context mContext) throws IOException {
        File image = null;
        String timeStamp = convertNumbersToEnglish((new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)).format(new Date()));
        String imageFileName = "JPEG_S_" + timeStamp + "_";
        File storageDir = mContext.getCacheDir();
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        Throwable var3 = null;

        try {
            OutputStream out = new FileOutputStream(dst);
            Throwable var5 = null;

            try {
                byte[] buf = new byte[1024];

                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (Throwable var29) {
                var5 = var29;
                throw var29;
            } finally {
                if (out != null) {
                    if (var5 != null) {
                        try {
                            out.close();
                        } catch (Throwable var28) {
                            var5.addSuppressed(var28);
                        }
                    } else {
                        out.close();
                    }
                }

            }
        } catch (Throwable var31) {
            var3 = var31;
            throw var31;
        } finally {
            if (in != null) {
                if (var3 != null) {
                    try {
                        in.close();
                    } catch (Throwable var27) {
                        var3.addSuppressed(var27);
                    }
                } else {
                    in.close();
                }
            }

        }

    }
}
