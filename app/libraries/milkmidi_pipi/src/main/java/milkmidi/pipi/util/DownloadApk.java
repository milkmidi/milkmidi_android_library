package milkmidi.pipi.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;

import milkmidi.pipi.core.IDestroy;

public class DownloadApk implements IDestroy{

    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DOWNLOAD_ERROR = 3;
    private static final String TAG = "[DownloadApk]";

    private boolean mDestroyed =false;
    @Override
    public boolean getDestroyed() {	return mDestroyed;	}

    private Context mContext;
    private ProgressDialog mDialog;
    private Handler mHandler;
    public DownloadApk(Context context) {
        super();
        this.mContext = context;
    }
    public void download(String urlPath ){
        download( urlPath , "Download", "Please wait for a while...");
    }
    public void download(String urlPath, String title , String message ){
        mDialog = new ProgressDialog( mContext );
        mDialog.setTitle( title );
        mDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
        mDialog.setMessage( message );
        mDialog.show();
        mDialog.setCancelable( true );
//		mDialog.setOnDismissListener( listener )
        new DownloadRunnable( urlPath ).start();
    }


    private class MyHandler extends Handler implements DialogInterface.OnClickListener{
        private MyHandler( Looper looper ) {
            super( looper );
        }
        @Override
        public void handleMessage( Message msg ) {
            switch ( msg.what ) {
                case DOWNLOAD_PROGRESS:
                    mDialog.setProgress( Integer.parseInt( msg.obj.toString()) );
                    break;
                case DOWNLOAD_COMPLETE:
                    mDialog.dismiss();
                    openFile( new File(msg.obj.toString()));
                    break;
                case DOWNLOAD_ERROR:
//					Toast.makeText( mContext, "Error" + msg.obj, Toast.LENGTH_LONG ).show();
                    HttpUtil.DownloadState state = (HttpUtil.DownloadState)msg.obj;
                    final int responseCode = state.responseCode;
                    String message = responseCode+"";
                    if ( responseCode == 404 ) {
                        message = "找不到檔案：" + state.fileUrl;
                    }
                    new AlertDialog.Builder(mContext).setTitle( "Error:"+responseCode )
                            .setMessage( message )
                            .setPositiveButton( "done",  this)
                            .create().show();

                    mDialog.dismiss();
                    break;
            }
        }
        @Override
        public void onClick( DialogInterface dialog, int which ) {
        }

    }
    private void openFile( File file ) {
        Intent intent = new Intent();
        intent.setAction( Intent.ACTION_VIEW );
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType( Uri.fromFile( file ), type );
        mContext.startActivity( intent );
        mHandler.postAtTime( new Runnable() {
            @Override
            public void run() {
                System.exit( 0 );
            }
        }, 500 );
    }



    private class DownloadRunnable extends Thread implements StreamUtil.OnCopyStreamProgressListener{
        private String mApkUrl;
        private DownloadRunnable(String apkUrl) {
            super();
            this.mApkUrl = apkUrl;
        }
        @Override
        public void run() {
            Looper.prepare();
            mHandler = new MyHandler( Looper.getMainLooper() );
            HttpUtil.DownloadState state = null;
            try {
                state = HttpUtil.download( mApkUrl , this );
            } catch ( Exception err ) {
                Log.i( TAG, err.getMessage() );
                err.printStackTrace();
            }
            if ( state.responseCode == HttpURLConnection.HTTP_OK ) {
                mHandler.obtainMessage( DOWNLOAD_COMPLETE, state.fileAbsolutePath ).sendToTarget();
            }else{
                mHandler.obtainMessage( DOWNLOAD_ERROR , state ).sendToTarget();
            }
            Looper.loop();
        }
        @Override // OnCopyStreamProgressListener
        public void onCopyStreamProgress( int percent ) {
            mHandler.sendMessage( mHandler.obtainMessage( 1, percent ) );
        }

    }

    @Override
    public void destroy() {
        if ( mDestroyed ) {
            return;
        }
        mContext = null;
        mDestroyed = true;
    }



}
