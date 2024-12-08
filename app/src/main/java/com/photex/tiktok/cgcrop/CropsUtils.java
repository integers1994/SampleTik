package com.photex.tiktok.cgcrop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

@SuppressWarnings("unused")
public class CropsUtils {

	public static final String RES_PREFIX_ASSETS = "assets://";
	public static final String RES_PREFIX_STORAGE = "/";
	public static final String RES_PREFIX_HTTP = "http://";
	public static final String RES_PREFIX_HTTPS = "https://";

	public static final String PREFIX_FOR_LOADER = "SELECT_KEY";

	public static final String THREAD_NAME_COLLAGE_PROCESS = "cpT";

	public static final String THREAD_NAME_MAP = "mapT";

	public static final String THREAD_NAME_SPLASH = "splashT";

	public static final String THREAD_NAME_MEM = "mT";

	public static final int JPEG_QUALITY = 95;

	public static final int PAUSED_RESOURCE_DELAY = 300;

	public static final int COLLAGE_BMP_MAX_LONG_SIDE = 1800;

	public static final int COLLAGE_BMP_MAX_SHORT_SIDE = 720;

	public static final int COLLAGE_READ_MAX_SIDE = 720;

	public static final int COLLAGE_READ_LOW_MAX_SIDE = 640;

	public static final int COLLAGE_MATERIAL_READ_MAX_SIDE = 720;

	public static final int[] LONG_COLLAGE_SAVE_MAX_SIDE = { 640, 560, 480,
			400, 320 };

	public static final int[] STORY_COLLAGE_SAVE_MAX_SIDE = { 960, 720, 640,
			560, 480, 400, 320 };

	private static final float DEFAULT_CAMERA_BRIGHTNESS = 0.7f;

	public static final int BIG_BMP_MAX_LONG_SIDE = 1800;

	public static final int BIG_BMP_MAX_SHORT_SIDE = 1800;

	public static final int SMALL_BMP_MAX_LONG_SIDE = 960;

	public static final int SMALL_BMP_MAX_SHORT_SIDE = 960;

	public static final int APP_TYPE_RELEASE = 0;

	public static final int APP_TYPE_RDM = 1;

	public static final int APP_TYPE_ALPHA = 2;

	public static final int APP_TYPE_HDBM = 3;

	public static final String QQCAMERA_KEY_ENABEL_FACE_DETECT = "enable_face_detect";
	public static final String QQCAMERA_KEY_FACE_RECTF = "face_rectf";
	public static final String QQCAMERA_KEY_LAUNCH_REFER = "launch_refer";
	public static final String QQCAMERA_FLAG_REFER_TTPU_KATONG = "ttpu_katong";
	public static final String QQCAMERA_FLAG_REFER_TTPU_QQCAMERA = "ttpu_qqcamera";
	public static final String QQCAMERA_KEY_FACE_DETECT_HINT = "face_detect_hint";
	public static final String QQCAMERA_KEY_FACE_DETECT_FAIL = "face_detect_fail";
	public static final String QQCAMERA_KEY_PICK_PICTURE_PATH = "pick_picture_path";
	public static final int FACE_DETECT_MIN_SIZE = 50;

	public static final float MAX_SCALE = 6.0f;
	public static final float MIN_SCALE = 1.0f;
	public static final float INIT_SCALE = 1.0f;

	private static final String[] IMAGE_PROJECTION = new String[] {
			MediaStore.Images.ImageColumns.DATE_TAKEN,
			MediaStore.Images.ImageColumns.LATITUDE,
			MediaStore.Images.ImageColumns.LONGITUDE, };

	public static boolean hasEclairMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasIcs() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean hasJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static final boolean HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT = hasField(
			MediaColumns.class, "WIDTH");

	private static boolean hasField(Class<?> klass, String fieldName) {
		try {
			klass.getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	// private static String parseExternalStorageDirectory(){
	// String mEnd = "ROOT";
	// String mBasic =
	// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
	// if(mBasic!=null){
	// String[] result = mBasic.split("/");
	// if(result.length>0){
	// mEnd = result[result.length - 1];
	// }
	// }
	// return mEnd;
	// }

	public static String getImagePathByUri(Context context, Uri uri) {
		String path = uri.getPath();

		String finalPath = null;
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {
				finalPath = path;
			}
		}

		// Read from
		if (TextUtils.isEmpty(finalPath)) {
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				if (cursor.getCount() == 0) {
					finalPath = null;
				} else {
					cursor.moveToFirst();
					finalPath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

		}
		return finalPath;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	private static final long ONE_DAY_THRESHOLD = 24 * 60 * 60l;

	public static boolean isTimeToCheck(Date lastDate) {
		Date nowDate = Calendar.getInstance().getTime();
		long diffSeconds = Math.abs(nowDate.getTime() - lastDate.getTime()) / 1000L;
		if (diffSeconds >= ONE_DAY_THRESHOLD) {
			return true;
		}
		return false;
	}

	public static String getFileName(String imagePath) {
		if (TextUtils.isEmpty(imagePath)) {
			return null;
		}
		int index = imagePath.lastIndexOf(File.separator);
		String fileName = imagePath.substring(index);
		return fileName;
	}

	public static String path2Name(String path) {
		if (path == null) {
			return null;
		}
		int lastIndex = path.lastIndexOf("/");
		if (lastIndex == -1) {
			return "/";
		}
		return path.substring(lastIndex + 1);
	}

	public static String path2Dir(String path) {
		if (path == null) {
			return null;
		}
		int lastIndex = path.lastIndexOf("/");
		if (lastIndex == -1) {
			return "/";
		}
		return path.substring(0, lastIndex);
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public static Uri getUriFromPath(String path) {
		return Uri.fromFile(new File(path));
	}

	@SuppressWarnings("deprecation")
	public static void saveURItoClipText(Context context, Uri uri) {
		String path = getImagePathByUri(context, uri);

		android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		try {
			if (path != null && clipboard != null)
				clipboard.setText(path);
		} catch (Exception e) {
			// nothing to be done if some devices failed
		}
	}

	@SuppressLint("DefaultLocale")
	public static int getAppType(String qua) {
		if (!TextUtils.isEmpty(qua)) {
			qua = qua.toLowerCase();
			if (qua.contains("_rdm")) {
				return APP_TYPE_RDM;
			} else if (qua.contains("_alpha")) {
				return APP_TYPE_ALPHA;
			} else if (qua.contains("_hdbm")) {
				return APP_TYPE_HDBM;
			}
		}
		return APP_TYPE_RELEASE;
	}

	public static boolean isTestVersion(String qua) {
		int appType = getAppType(qua);
		switch (appType) {
		case CropsUtils.APP_TYPE_HDBM:
		case CropsUtils.APP_TYPE_ALPHA:
		case CropsUtils.APP_TYPE_RDM:
			return true;
		}
		return false;
	}

	public static String getRealPath(String path) {
		return TextUtils.isEmpty(path) ? path : path
				.startsWith(RES_PREFIX_ASSETS) ? path
				.substring(RES_PREFIX_ASSETS.length()) : path;
	}

	@SuppressLint("DefaultLocale")
	public static byte[] unZip(byte[] data) throws IOException {
		if (null == data)
			return null;

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);
		byte[] output = new byte[0];
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
				data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				byteArrayOutputStream.write(buf, 0, i);
			}
			output = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

	public static boolean isAppInstalled(Context context, String pakName) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager()
					.getPackageInfo(pakName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		}
		return true;
	}

	public interface CaptureDialogListener {
		public void onDialogDismissed(Intent loadIntent);
	}

	public static String getCallerPackage(Activity activity) {
		ComponentName cm = activity.getCallingActivity();
		return cm != null ? cm.getPackageName() : null;
	}

	public static Point toPoint(PointF pointF) {
		return new Point((int) pointF.x, (int) pointF.y);
	}

	@SuppressLint("FloatMath")
	public static float calDistance(PointF a, PointF b) {
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}
}
