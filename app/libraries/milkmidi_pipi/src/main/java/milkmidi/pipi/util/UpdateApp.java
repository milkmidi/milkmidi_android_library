package milkmidi.pipi.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class UpdateApp extends AsyncTask<String, Void, JSONObject> implements
        DialogInterface.OnClickListener {
    private int mHostVersion = -1;
    private String log = "Loading";
    private Context mContext;
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    private String mApkUrl;
    public UpdateApp( Context context ) {
        super();
        this.mContext = context;
    }

    @Override
    protected JSONObject doInBackground( String... params ) {
        mApkUrl = params[1];

//		HttpUtil.getWithURLConnection( urlPath )
        try {
            return downloadHostVersionCode( params[0] );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject downloadHostVersionCode( String sUrl )
            throws IOException, JSONException {
        String result = "";
        URL url = new URL( sUrl );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod( "GET" );
        int rs = conn.getResponseCode();
        if ( rs == HttpURLConnection.HTTP_OK ) {
            InputStreamReader isr = new InputStreamReader(
                    conn.getInputStream() );
            BufferedReader rd = new BufferedReader( isr );
            StringBuffer sb = new StringBuffer();
            String line;
            while ( (line = rd.readLine()) != null ) {
                sb.append( line );
            }
            rd.close();
            result = sb.toString();
        }
        return new JSONObject( result );
    }

    @Override
    protected void onPostExecute( JSONObject result ) {
        if ( result ==null ) {
            return;
        }
        try {
            mHostVersion = result.getInt( "versionCode" );
            log = result.getString( "log" );
        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }
        int appVersion = 99999;
        try {
            appVersion = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0 ).versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        trace( "onPostExecute() host:" + mHostVersion, " local:" + appVersion );
        if ( mHostVersion > appVersion ) {
            new AlertDialog.Builder( mContext )
                    .setTitle( "新版本:" + mHostVersion )
                    .setMessage( log )
                    .setPositiveButton( "確定", this )
                    .setCancelable( false )
                    .setNegativeButton( "晚點", this ).create().show();
        }
    }

    @Override
    public void onClick( DialogInterface dialog, int which ) {
        if ( which == AlertDialog.BUTTON1) {
            new DownloadApk( mContext ).download( mApkUrl,"新版本:" + mHostVersion, log );
        }
    }





    protected void trace( Object... objects ) {
        Log.i( TAG, Arrays.toString( objects ) );
    }

}
