package com.photex.tiktok.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.photex.tiktok.R;

import java.security.SecureRandom;


public class CommonUtils {

    public static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams winParams = window.getAttributes();
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(winParams);
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }



    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }

    public static void showToast(Context context, String messsage) {
        Toast.makeText(context, messsage, Toast.LENGTH_SHORT).show();
    }

    public static byte[] generateRandom(int numBytes) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    public static int pxToDp(int px, Context c) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        return Math.round(px * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getScreenWidth(Context c) {

        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        return size.x;
    }

    public static void notifyMediaStoreScannerService(Context mContext, final Uri uri) {
        try {
         //   File file = new File(RealPathUtil.getPathFromUri(mContext, uri));
          /*  MediaScannerConnection.scanFile(mContext,
                    new String[]{file.toString()}, null, null
                 *//*   new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    }*//*);*/

            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                  Uri.parse(RealPathUtil.getPathFromUri(mContext, uri))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static boolean isMyServiceRunning(Context mContext,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
