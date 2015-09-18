package milkmidi.pipi.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

public final class FileUtil{

	public enum ImageType{
		JPG , PNG
	}
	
	public static String getDataDir(Context context) throws Exception     {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }
	
	public static String readRawResourceTxt( Context pContext, int pResId )	{
		InputStream is = pContext.getResources().openRawResource(pResId);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			for (int i = is.read();; i = is.read()) {
				if (i == -1) {
					is.close();
					return os.toString();
				}
				os.write(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean mExternalStorageAvailable = false;
	public static boolean getExternalStorageAvailable(){ 
		return mExternalStorageAvailable;
	}
	
	private static boolean mExternalStorageWriteable = false;	
	public static boolean getExternalStorageWriteable(){ return mExternalStorageWriteable; }
	public static boolean checkSDCard()	{
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
	/*	if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}*/
		return mExternalStorageAvailable;
	}

	public static boolean createFolderToSDCard( String folderName ) 	{
		File file = new File(getSDCardPath(folderName));
		if (!file.exists()) {
			file.mkdirs();
			return true;
		}
		return false;
	}

	public static String getSDCardPath( String folderName )	{
		return Environment.getExternalStorageDirectory() + "/" + folderName;
	}

	public static File getFileFromSDCard( String folderName, String fileName )	{
		return new File(Environment.getExternalStorageDirectory() + "/"
				+ folderName + "/" + fileName);
	}
	
	public static boolean fileExists( String folderName, String fileName ){
		File file = getFileFromSDCard( folderName, fileName );
		return file.exists();
	}

	public static String readFileFromSDCard( String pFolderPath,String pFileName )	{
		File file = getFileFromSDCard(pFolderPath, pFileName);
		return readFile(file);
	}
	public static String readFile(File file)	{		
		FileInputStream is = null;
		BufferedReader buf = null;
		String rs = null;
		if (file.exists()) {
			try {
				is = new FileInputStream(file);
				buf = new BufferedReader(new InputStreamReader(is));
				String readStr = new String();
				StringBuilder text = new StringBuilder();
				while ((readStr = buf.readLine()) != null) {
					text.append(readStr);
				}
				rs = text.toString();
				text = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
					if (buf != null) {
						buf.close();
						buf = null;
					}
				} catch (Exception e2) {
				}
			}

		}
		return rs;
	}

	public static boolean saveFileToSDCard( String folderName,
			String fileName, String data )	{
		createFolderToSDCard(folderName);
		FileOutputStream fOut = null;
		OutputStreamWriter writer = null;
		boolean rs = false;
		try {
			fOut = new FileOutputStream(getSDCardPath(folderName + "/"
					+ fileName));
			writer = new OutputStreamWriter(fOut);
			writer.write(data);
			writer.flush();
			rs = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (fOut != null) {
					fOut.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return rs;
	}

	public static void saveBitmap( String folderName, Bitmap bitmap,
			String fileName, ImageType imageType ) throws IOException	{

		String folderPath = folderName;
		if (folderPath.lastIndexOf("/") == -1) {
			folderPath += "/";
		}
		File file = new File(folderPath + fileName);
		OutputStream outStream = new FileOutputStream(file);

		if (imageType.equals(ImageType.JPG)) {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
		} else if (imageType.equals(ImageType.PNG)) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, outStream);
		}
		outStream.flush();
		outStream.close();
	}

	public static void saveBitmap( String pFolderPath, Bitmap pBitmap,
			String pFileName ) throws IOException	{
		FileUtil.saveBitmap(pFolderPath, pBitmap, pFileName, ImageType.JPG);
	}

	public static void saveBitmap( String pFolderPath, Bitmap pBitmap )
			throws IOException	{
		FileUtil.saveBitmap(pFolderPath, pBitmap, getTimeName() + ".jpg");
	}

	public static String getTimeName(boolean addRandom){
		Date _date = new Date(System.currentTimeMillis());
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String name =  _sdf.format(_date);
		Random _random = new Random();
		
		return addRandom ? name+"_" + _random.nextInt(999) : name;
	}
	public static String getTimeName(){		
		return getTimeName(false);
	}
	
}


