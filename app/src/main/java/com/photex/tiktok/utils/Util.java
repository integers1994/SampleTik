package com.photex.tiktok.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.photex.tiktok.R;
import com.photex.tiktok.setting.SettingManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {


    public static boolean isNetworkAvailable(Context context) {
        if (context != null && context.getSystemService(Context.CONNECTIVITY_SERVICE) != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (null != activeNetwork) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    }
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void internetNotAvailableDialouge(Context context) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(context.getString(R.string.internet_not_connected_title));
        builder.setMessage(context.getString(R.string.internet_notconnected));
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    public static String getCurrentDateString() {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy'T'hh:mm:ss a");
        String dateString = formatter.format(currentTime);
//        Log.e("dateTime", dateString);
        return dateString;
    }

    public static String getSortedStringDate() {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
//        Log.e("dateTime", dateString);
        return dateString;
    }

    private static int screenWidth = 0;
    private static int screenHeight = 0;


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public static String getPathFromURI(Uri uri, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }

    public static String makeAndGetFolderName(Context context, String folderName) {
//        boolean success = true;
//        String nameFolder;
//
//        if (folderName != null && !folderName.isEmpty()) {
//            nameFolder = Constants.TIKTOKDIR + File.separator + folderName;
//
//        } else {
//            nameFolder = Constants.TIKTOKDIR;
//        }
//        File file;
////        if (EasyPermissions.hasPermissions(context,
////                Constants.PARM_WRITE_EXTERNAL_STORAGE)) {
////            file = new File(Environment.getExternalStorageDirectory(),
////                    nameFolder);
////
////        } else {
//        file = new File(context.getCacheDir(), nameFolder);
//
////        }
//        if (!file.exists()) {
//            success = file.mkdirs();
//        }
//      /*  if (success) {
//            return file.getAbsolutePath();
//        }else {
//
//        }*/
//
//
//   /*     return success ? file.getAbsolutePath() :
//                new File(Environment.getExternalStoragePublicDirectory
//                        (Environment.DIRECTORY_PICTURES), nameFolder).
//                        getAbsolutePath();*/

        File path = new File(context.getFilesDir(), folderName);
        if (!path.exists()) {
            path.mkdir();
        }
        return path.getAbsolutePath();
    }


    public static String getFileNameFromPath(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - 1];
    }

    public static void exceptionDialougeBox(Context context, String title, String message) {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    Log.d("UserProfileActivity", "serice is forground");
                }
                return true;
            }
        }
        return false;
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        //is this device has a camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Creating file uri to store image/video
     */
    public static Uri getOutputMediaFileUri(int type) {
        return getOutputMediaFile(type) != null ? Uri.fromFile(getOutputMediaFile(type)) : null;
    }

    /**
     * returning image / video
     */
    public static File getOutputMediaFile(int type) {
        String IMAGE_DIRECTORY_NAME = "TiktokCamera";
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                Constants.TIKTOKDIR + File.separator + IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static File createImage(Context context, Bitmap bm) throws Exception {

        int ctime = Calendar.getInstance().get(Calendar.MILLISECOND);
        File file, f;

        file = new File(Environment.getExternalStorageDirectory(),
                Constants.TEMP_IMAGE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        f = new File(file.getAbsolutePath() + File.separator + SettingManager.getUserName(context)
                + ".jpeg");

       /* f = new File(file.getAbsolutePath() + File.separator + "Photex" + ctime
                + ".png");*/
        FileOutputStream ostream1;
        try {
            ostream1 = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, ostream1);
            ostream1.flush();
            ostream1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;

    }

    public static boolean isDateAfter(String data1, String date2) {
        try {
            Date dateOne = new SimpleDateFormat("MMM d,yyyy", Locale.ENGLISH).parse(data1);
            Date dateTwo = new SimpleDateFormat("MMM d,yyyy", Locale.ENGLISH).parse(date2);
            return dateTwo.after(dateOne);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

 /*   public static Bitmap getBitmapByUri(Context context, Uri selectedImage, int height, int width) {

        Bitmap photoBitmap;
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
//        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(inputStream, null, bitmapOptions);

        int imageWidth = bitmapOptions.outWidth;
        int imageHeight = bitmapOptions.outHeight;

        float scale = 1.0f;

        if (imageWidth < imageHeight) {
            if (imageHeight > width * 1.0f) {
                scale = width * 1.0f / (imageHeight * 1.0f);
            }

        } else {
            if (imageWidth > width * 1.0f) {
                scale = width * 1.0f / (imageWidth * 1.0f);
            }

        }
        photoBitmap = LoadImage.decodeSampledBitmapFromResource(context,
                selectedImage, (int) (imageWidth * scale),
                (int) (imageHeight * scale));
        return photoBitmap;
    }

*/

    public static File storeBitmap(Context context, Bitmap image, String folderName) {

        File pictureFile = new File(makeAndGetFolderName(context, folderName), System.currentTimeMillis() + ".jpg");
        try {
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pictureFile;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }


    public static File renameFile(String folderPath, String oldName, String newName) {

        File fileToReturn = null;
        File dir = new File(folderPath);
        if (dir.exists()) {
            File from = new File(dir, oldName);

            fileToReturn = new File(dir, newName);
            if (from.exists()) {
                boolean success = from.renameTo(fileToReturn);
                if (!success) return fileToReturn;
            }
        }
        return fileToReturn;
    }


/*
    public static File storeBitmapInCashDir(Context context, Bitmap image, String folderName) {
        File sd = context.getCacheDir();
        File folder = new File(sd, "/tempFolder/");
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("ERROR", "Cannot create a directory!");
            } else {
                folder.mkdirs();
            }
        }


        File pictureFile = new File(folder, System.currentTimeMillis() + "");
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            */
    /*Log.d(TAG, "File not found: " + e.getMessage());*//*

        } catch (IOException e) {
            */
    /*Log.d(TAG, "Error accessing file: " + e.getMessage());*//*

        }
        return pictureFile;
    }
*/

    public static void showToast(Context mContext, int mToastLength, String message) {

        Toast.makeText(mContext, message, mToastLength).show();
    }
}
