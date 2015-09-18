package milkmidi.pipi.util;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author milkmidi
 * @date 2014 07 09
 */
public final class DeviceUtil {
	public static final String PLATVERSION = Build.VERSION.RELEASE;
//	public static final String UID = Build.MODEL;
	public static final int SDK_VERSION = Build.VERSION.SDK_INT;	
	private static final String TAG = "[DeviceUtil]";	
	
	
	public static String getDeviceInfo(Context context){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Build.MANUFACTURER:" + Build.MANUFACTURER+"\n")
			.append("Build.MODEL:" + Build.MODEL+"\n")		
			.append("VersionName:" + getVersionName(context)+"\n")
			.append("VersionCode:" + getVersionCode(context)+"\n")
			.append("PLATVERSION:" + PLATVERSION+"\n")
			.append("SDK_VERSION:" + SDK_VERSION+"\n")
			.append("MemoryAvailable:" + getMemoryAvailable()+"\n")
			.append("MemoryTotalSize:" + getMemoryTotalSize()+"\n");

		DisplayMetrics dms = context.getResources().getDisplayMetrics();
		
		sb.append( "Resolution:"+ dms.widthPixels + "x" + dms.heightPixels + "\n")
			.append( "DensityDpi:"+ dms.densityDpi + "\n")	
			.append( "drawable:"+ parseDensityDpi( dms.densityDpi ) );
		return sb.toString();
	}
	private static String parseDensityDpi(int densityDpi){
		if (densityDpi < 160) {
			return "ldpi";
		}else if (densityDpi == 160) {
			return "mdpi";
		}else if (densityDpi == 240) {
			return "hdpi";
		}else if( densityDpi == 320){
			return "xhdpi";
		}else if( densityDpi == 480){
			return "xxhdpi";
		}else if( densityDpi > 480){
			return "xxxhdpi";
		}
		return "unknow";
	}

	private static String mUUID;
	public static String getUUID( Activity context){
		if ( mUUID == null ) {
			try {
				final TelephonyManager tm = (TelephonyManager) context.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
				final String tmDevice, tmSerial, androidId;
		 	    tmDevice = "" + tm.getDeviceId();
		 	    tmSerial = "" + tm.getSimSerialNumber();
		 	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
				UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
				mUUID = deviceUuid.toString();
			}
			catch (Exception e) {
			}
		}	
		return mUUID;	
	}
	
	public static String getVersionName(Context context) {		
	    PackageManager packageMng = context.getPackageManager();
	    String packageName = context.getPackageName();
	    try {
			PackageInfo info =  packageMng.getPackageInfo(packageName, 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}	  
	}	
	public static int getVersionCode(Context context) {
		try {
			PackageManager packageMng = context.getPackageManager();
			String packageName = context.getPackageName();
			PackageInfo info = packageMng.getPackageInfo(packageName, 1);
			if (info != null) {
				return info.versionCode;
			}
		} catch (Exception localException) {
		}
		return 0;
	}

	public static String getLocaleLanguage(){
		Locale l = Locale.getDefault();
		return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}


	/*

	public static final DisplayMetrics getMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService( Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(dm);
		return dm;		
	}

*/

	public static final String getExternalStoragePath() {
		String exStorageState = Environment.getExternalStorageState();
		if (!"mounted".equals(exStorageState)) {

		} else {
			exStorageState = Environment.getExternalStorageDirectory()
					.getPath();
		}
		return exStorageState;
	}
	
	public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        return stat.getAvailableBlocks() * blockSize;
    }
	public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        return stat.getBlockCount() * blockSize;
    }

	public static final long getMemoryAvailable() {
		String path = Environment.getRootDirectory().getPath();
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		return stat.getAvailableBlocks() * blockSize;
	}

	public static final long getMemoryTotalSize() {
		String path = Environment.getRootDirectory().getPath();
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		return stat.getBlockCount() * blockSize;
	}

	public static String getAndroidID( Context context){
		String android_id = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase();
		return deviceId;
	}
	public static String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG,e.getMessage());
		}
		return "";
	}


	private static void trace(Object... objects) {		
		Log.i( TAG, Arrays.toString(objects ));
	}
}
