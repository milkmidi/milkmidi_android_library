package milkmidi.pipi.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import milkmidi.pipi.util.StreamUtil.OnCopyStreamProgressListener;

public final class HttpUtil {
    private static final String TAG = "[HttpUtil]";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY =  "*****";


    public static class DownloadState{
        public int responseCode;
        public String fileAbsolutePath;
        public String fileUrl;
        public String toString(){
            return "[DownloadState responseCode:"+this.responseCode+"]";
        }
    }

    public static DownloadState download( String urlPath,File file , OnCopyStreamProgressListener listener ) throws Exception {
        if ( !URLUtil.isNetworkUrl( urlPath ) ) {
            throw new Exception( "無效的 url 連結" );
        }
        HttpURLConnection con = null;
        FileOutputStream os = null;
        InputStream is = null;
        DownloadState state = new DownloadState();
        state.fileUrl = urlPath;
        try {
            URL url = new URL( urlPath );
            con = (HttpURLConnection)url.openConnection();
            con.connect();

            int responseCode = con.getResponseCode();
            state.responseCode = responseCode;
            if(responseCode == HttpURLConnection.HTTP_OK){
                is = con.getInputStream();
			/*	if ( is == null ) {
					throw new RuntimeException( "Stream is null" );
				}		*/
                state.fileAbsolutePath = file.getAbsolutePath();
                os = new FileOutputStream( file );
                if ( listener != null ) {
                    final long lenghtOfFile = con.getContentLength();
                    StreamUtil.copyStream( is , os,lenghtOfFile,listener );
                }
                else{
                    StreamUtil.copyStream( is , os );
                }
            }
            Log.i( TAG, "getResponseCode()"+responseCode );
        } catch ( MalformedURLException err ) {
            err.printStackTrace();
        } catch ( IOException err ) {
            err.printStackTrace();
        } finally{

            con = null;

            if ( is!=null ) {
                is.close();
            }
            if ( os != null ) {
                os.flush();
                os.close();
                os = null;
            }


        }
        return state;
    }
    public static DownloadState download( String fileUrl , OnCopyStreamProgressListener listener ) throws Exception {
        String fileName = fileUrl.substring(
                fileUrl.lastIndexOf( "/" )+1,
                fileUrl.lastIndexOf( "." ) );
        String fileEx =  fileUrl.substring(
                fileUrl.lastIndexOf( "." )+1,
                fileUrl.length() ).toLowerCase();
        String fullName = fileName+"_"+ FileUtil.getTimeName() + "." + fileEx;

        //File tmpFile = File.createTempFile( fileName, fileEx );
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if ( !file.exists() ) {
            file.mkdir();
        }
        File tmpFile = new File( file.getAbsoluteFile() + "/" + fullName);
        return download( fileUrl , tmpFile , listener);
    }
    public static DownloadState download( String fileUrl ) throws Exception {
        return download( fileUrl , null);
    }


    public static String upload(String uploadUrl , File file, String fileNewName){
        String result = null;
        trace( "upload() " + uploadUrl +" file:"+file+" newName:"+fileNewName );
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+BOUNDARY);
            conn.setRequestProperty("Charset", "UTF-8");

            DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file1\";filename=\"" +
                    fileNewName +"\"" + LINE_END);
            ds.writeBytes(LINE_END);

            FileInputStream fStream = new FileInputStream( file );
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length= fStream.read(buffer)) !=-1) {
                ds.write(buffer,0,length);
            }

            ds.writeBytes(LINE_END);
            ds.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

            fStream.close();
            ds.flush();


            int resCode = conn.getResponseCode();
            String resMessage = conn.getResponseMessage();


            trace("getResponseCode:"+resCode );
            trace("getResponseMessage:"+resMessage );


            if ( resCode == HttpStatus.SC_OK ) {
                InputStream is = conn.getInputStream();
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = is.read()) !=-1) {
                    sb.append((char) ch);
                }
                result = sb.toString();
            }


            ds.close();
        } catch (IOException err) {
            err.printStackTrace();
            Log.e( TAG , err.toString() );
        }
        return result;
    }
    public static String upload(String uploadUrl , String filePath, String fileNewName){
        return upload( uploadUrl , new File( filePath ) , fileNewName );
    }






    /**
     * 使用 HttpURLConnection
     * @param urlPath
     * @return null 或是有值
     */
    public static String getWithURLConnection(String urlPath){
        String result = null;
        try {
            URL url = new URL( urlPath );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout( 5 * 1000 );
            conn.setRequestMethod( "GET" );
            final int responseCode = conn.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK ) {
                InputStreamReader isr = new InputStreamReader(	conn.getInputStream() );
                BufferedReader rd = new BufferedReader( isr );
                StringBuffer sb = new StringBuffer();
                String line;
                while ( (line = rd.readLine()) != null ) {
                    sb.append( line );
                }
                rd.close();
                result = sb.toString();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 使用 HttpGet
     * @param urlPath
     * @return null 或是有值
     */
    public static String get(String urlPath ) {
        HttpGet http = new HttpGet( urlPath );
        return doSendGetOrPost( http );
    }
    public static String post(String url , Map<String, String> map){
        HttpPost http = new HttpPost(url);
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> pairs = it.next();
            param.add(new BasicNameValuePair(pairs.getKey(), pairs.getValue()));
        }
        String result = null;
        try {
            http.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
            result = doSendGetOrPost(http);
        } catch (UnsupportedEncodingException err) {
            err.printStackTrace();
            Log.e( TAG , err.toString() );
        }
        return result;
    }
    public static String post(String url , Bundle params){
        HttpPost http = new HttpPost(url);
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            String value = params.getString(key);
            if (value != null) {
                param.add(new BasicNameValuePair(key, value));
            }
        }
        String result = null;
        try {
            http.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
            result = doSendGetOrPost(http);
        } catch (UnsupportedEncodingException err) {
            err.printStackTrace();
            Log.e( TAG , err.toString() );
        }
        return result;
    }

    private static String doSendGetOrPost(HttpRequestBase http){
        String result = null;
        try {
//			HttpResponse rs = new DefaultHttpClient().execute( http );			
            final HttpResponse rs = genHttpClient().execute( http );
            final int statusCode = rs.getStatusLine().getStatusCode();
            if ( statusCode == HttpStatus.SC_OK ) {
                result = EntityUtils.toString( rs.getEntity() , HTTP.UTF_8 );
            } else {
                Log.i(TAG, "HTTP request fails in method doSendGetOrPost, resonse code :" + statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception happens when deal with http request in method doSendGetOrPost:" + e.getCause());
        }
        return result;
    }
    private static HttpClient genHttpClient() {
        final BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        HttpConnectionParams.setSoTimeout(params, 5000);
        HttpConnectionParams.setSocketBufferSize(params, 1024);
        return new DefaultHttpClient(params);
    }


    public static Drawable loadDrawable(String urlPath){
        InputStream i = null;
        try {
            URL url = new URL(urlPath);
            i = (InputStream) url.getContent();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }
    public static Bitmap loadBitmap(String urlPath){
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//			conn.setConnectTimeout( 5 * 1000 );
            conn.setRequestMethod("GET");
            final int responseCode = conn.getResponseCode();
            if ( responseCode == HttpStatus.SC_OK) {
                trace("loadBitmap urlPath:"+urlPath+" Success"+" ,length"+conn.getContentLength());
            }else{
                trace("loadBitmap urlPath:"+urlPath+" Fault");
            }
//			conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            //*/
			/* 方法二
			InputStream is = url.openStream();
			//*/
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            trace(e);
            e.printStackTrace();
        }
        return bitmap;
    }

    private static void trace(Object o){
        Log.i( TAG, o + "" );
    }
}
