package milkmidi.pipi.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class StreamUtil {
	private static final int	BUFFER_SIZE	= 1024;

	public static void copyAssets(Context context, String assetFilePath, String destDirPath) {
	    AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(assetFilePath);
          File outFile = new File(destDirPath, assetFilePath);
          out = new FileOutputStream(outFile);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + assetFilePath, e);
        }       
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024*16];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public static void copyStream( InputStream is, OutputStream os ) throws IOException {
		int count;
		byte[] bytes = new byte[BUFFER_SIZE];
		for ( ;; ) {
			count = is.read( bytes, 0, BUFFER_SIZE );
			if ( count == -1 )
				break;
			os.write( bytes, 0, count );
		}		
	}
	
	public static ByteArrayOutputStream toByteArrayOutputStream(InputStream is) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		copyStream(is,os);
	    // Fake code simulating the copy
	    // You can generally do better with nio if you need...
	    // And please, unlike me, do something about the Exceptions :D
	   /* byte[] buffer = new byte[1024];
	    int len;
	    while ((len = is.read(buffer)) > -1 ) {
	        baos.write(buffer, 0, len);
	    }
	    baos.flush();*/

	    // Open new InputStreams using the recorded bytes
	    // Can be repeated as many times as you wish
	    //InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
	    //InputStream is2 = new ByteArrayInputStream(baos.toByteArray()); 
	    return os;
	}
	public static void copyStream( InputStream is, OutputStream os, final long lengthOfFile,
			OnCopyStreamProgressListener listener) throws IOException {
		byte bytes[] = new byte[BUFFER_SIZE];
		int count = 0;
		int total = 0;
		int percent = 0;
		while ( (count = is.read( bytes )) != -1 ) {
			total += count;
			percent = (int)(total * 100 / lengthOfFile);
			listener.onCopyStreamProgress( percent );
			os.write( bytes, 0, count );
		}
	}

	public static interface OnCopyStreamProgressListener {
		void onCopyStreamProgress( int percent );
	}
}
