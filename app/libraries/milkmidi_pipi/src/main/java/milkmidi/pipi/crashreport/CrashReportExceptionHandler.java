package milkmidi.pipi.crashreport;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import milkmidi.pipi.util.DeviceUtil;
import milkmidi.pipi.util.HttpUtil;

public class CrashReportExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultEXCHandler;
    private String localPath;
    private String url;
    private Context mContext;

    private static String CRASH_REPORTER_EXTENSION = ".txt";
    private static final String TAG = "[CrashReportExceptionHandler]";
    private static final String NEW_LINE = System.getProperty( "line.separator" );

    private static Application mApplication;

    private static StringBuilder mSB;
    public static void init(Application app){
        if (mApplication != null) {
            throw new IllegalStateException("init called more than once");
        }
        mApplication = app;
        CrashReport crashReport = mApplication.getClass().getAnnotation( CrashReport.class );
        if ( crashReport == null) {
            Log.e( TAG,
                    "CrashReportExceptionHandler#init called but no CrashReport annotation on Application " + mApplication.getPackageName() );
        }

        CRASH_REPORTER_EXTENSION = crashReport.reportExtension();

        mSB = new StringBuilder();
        String hostUrl = crashReport.hostURL();
        String localUrl = Environment.getExternalStorageDirectory().getPath()+"/"+crashReport.localFordName();
        Thread.setDefaultUncaughtExceptionHandler( new CrashReportExceptionHandler(
                mApplication.getApplicationContext(), localUrl, hostUrl ) );
    }

    public static StringBuilder append(Object... objects){
        return mSB.append( Arrays.toString( objects ) +NEW_LINE);
    }

    public CrashReportExceptionHandler(Context context,String localPath, String url) {
        this.mContext = context;
        this.localPath = localPath;
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.url = url;
        this.mDefaultEXCHandler = Thread.getDefaultUncaughtExceptionHandler();
        trace( "localPath:"+localPath );
        trace( "host:"+url);
        sendCrashFileToServer();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultEXCHandler != null) {
            mDefaultEXCHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException er) {
                trace("Error : ", ex);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.toString();
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "喔!不,錯誤發生了^(T_T)^ 代碼:\n" + msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        Log.e( TAG,result.toString());
        String stacktrace =
                mSB.toString() + NEW_LINE +
                        result.toString() + NEW_LINE +
                        loadDeviceInfo().toString();
        stacktrace += DeviceUtil.getDeviceInfo( mContext );
        printWriter.close();

        String timestamp = DateFormat.format("yyyy_MM_dd_hh_mm_ss", new Date()).toString();
        String filename = timestamp + CRASH_REPORTER_EXTENSION;

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }

        new Thread( new Runnable() {
            @Override
            public void run() {
                sendCrashFileToServer();
            }
        } ).start();


        return true;
    }


    private String[] getCrashReportFiles() {
        File filesDir = new File(localPath);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }  

    /*private void sendErrorMail(String result){
    	  Intent sendIntent = new Intent(Intent.ACTION_SEND);
  		String subject = "Error report";
  		String body =
  		"Mail this to readerscope@altcanvas.com: "+
  		"\n\n"+
  		result+
  		"\n\n";

  		sendIntent.putExtra(Intent.EXTRA_EMAIL,
  		new String[] {"readerscope@altcanvas.com"});
  		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
  		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
  		sendIntent.setType("message/rfc822");

  		mContext.startActivity(Intent.createChooser(sendIntent, "Title:"));
    }*/

    private void writeToFile(String stacktrace, String filename) {
        trace("writeToFile()");
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCrashFileToServer(){
        String[] fileNaems = getCrashReportFiles();
        trace("sendCrashFileToServer length:"+fileNaems.length);
        if (fileNaems != null && fileNaems.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(fileNaems));
            for (String fileName : sortedFiles) {
                File cr = new File( localPath, fileName);
                sendToServer(cr);
                cr.delete();// 刪除已發送的報告
            }
        }
    }
    private void sendToServer(File file){
        trace("sendToServer()",file.getName());
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            sendToServer(text.toString(), file.getName() );
        }
        catch (IOException e) {
        }
    }
    private void sendToServer(String stacktrace, String filename) {
        Bundle b = new Bundle();
        b.putString("filename", filename);
        b.putString("stracktrace", stacktrace);
        HttpUtil.post( url, b);
        trace("sendToServer()",filename);
    }

    private DeviceInfoVO loadDeviceInfo() {
        PackageManager pm = mContext.getPackageManager();
        DeviceInfoVO vo = new DeviceInfoVO();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            vo.versionName = pi.versionName;
            // Package name
            vo.packageName = pi.packageName;
            // Files dir for storing the stack traces
            vo.filePath = mContext.getFilesDir().getAbsolutePath();
            // Device model
            vo.phoneModel = android.os.Build.MODEL;
            // Android version
            vo.androidVersion = android.os.Build.VERSION.RELEASE;

            vo.board = android.os.Build.BOARD;
            vo.brand = android.os.Build.BRAND;
            //CPU_ABI = android.os.Build.;
            vo.device = android.os.Build.DEVICE;
            vo.display = android.os.Build.DISPLAY;
//            vo.fingerPrint = android.os.Build.FINGERPRINT;
            vo.host = android.os.Build.HOST;
            vo.id = android.os.Build.ID;
            //Manufacturer = android.os.Build.;
            vo.model = android.os.Build.MODEL;
            vo.product = android.os.Build.PRODUCT;
            vo.tags = android.os.Build.TAGS;
            vo.time = android.os.Build.TIME;
            vo.type = android.os.Build.TYPE;
            vo.user = android.os.Build.USER;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        return vo;
    }
    class DeviceInfoVO{
        String versionName;
        String packageName;
        String filePath;
        String phoneModel;
        String androidVersion;
        String board;
        String brand;
        String device;
        String display;
        String fingerPrint;
        String host;
        String id;
        String model;
        String product;
        String tags;
        long time;
        String type;
        String user;
        public String toString(){
            StringBuffer returnVal = new StringBuffer();
            returnVal.append("Version : ").append(versionName).append("\n");
            returnVal.append("Package : ").append(packageName).append("\n");
            returnVal.append("FilePath : ").append(filePath).append("\n");
            returnVal.append("Phone Model : ").append(phoneModel).append("\n");
            returnVal.append("Android Version : ").append(androidVersion).append("\n");
            returnVal.append("Board : ").append(board).append("\n");
            returnVal.append("Brand : ").append(brand).append("\n");
            returnVal.append("Device : ").append(device).append("\n");
            returnVal.append("Display : ").append(display).append("\n");
            returnVal.append("Finger Print : ").append(fingerPrint).append("\n");
            returnVal.append("Host : ").append(host).append("\n");
            returnVal.append("ID : ").append(id).append("\n");
            returnVal.append("Model : ").append(model).append("\n");
            returnVal.append("Product : ").append(product).append("\n");
            returnVal.append("Tags : ").append(tags).append("\n");
            returnVal.append("Time : ").append(time).append("\n");
            returnVal.append("Type : ").append(type).append("\n");
            returnVal.append("User : ").append(user).append("\n");
            returnVal.append("Total Internal memory : ").append(getTotalInternalMemorySize()).append("\n");
            returnVal.append("Available Internal memory : ").append(getAvailableInternalMemorySize()).append("\n");
            return returnVal.toString();
        }
        private long getAvailableInternalMemorySize() {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        private long getTotalInternalMemorySize() {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
    }

    final protected void trace(Object... objects) {
        Log.i(TAG, Arrays.toString( objects ));
    }
}