package milkmidi.pipi.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkWatcher {
    private static NetworkWatcher sInstance;
    private OnNetworkWatcherCallback mCallback;
    private Context mContext;
    private BroadcastReceiver mReceiver;
    private boolean mWifiConnected;
    
    private NetworkWatcher(Context context, OnNetworkWatcherCallback callback) {
        super();
        this.mContext = context.getApplicationContext();
        this.mCallback = callback;
        this.mReceiver = new NetworkBroadcastReceiver();      
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        this.mWifiConnected = isWifiConnected(context);
    }
    
    class NetworkBroadcastReceiver extends BroadcastReceiver{
    	@Override
        public void onReceive(Context context, Intent intent) {
            final NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
            final boolean b = networkInfo != null && networkInfo.isConnected();
            if ( mWifiConnected != b) {
                 mWifiConnected = b;                 
                 if ( mCallback !=null ) {
                	 if (b) 
                		 mCallback.onWifiConnected();
                	 else
                		 mCallback.onWifiDisconnected();
				}                 
            }
        }    	
    }
    
   /* private static synchronized void access(NetworkWatcher networkWatcher, boolean wifiConnected) {
        networkWatcher.mWifiConnected = wifiConnected;
    }*/
    
    /*public static HttpHost getProxy() {
        return new HttpHost(Proxy.getDefaultHost(), Proxy.getDefaultPort());
    }
    
    public static boolean hasProxy() {
        return !TextUtils.isEmpty((CharSequence)Proxy.getDefaultHost());
    }
    */
    public static boolean isWifiConnected() {
        return NetworkWatcher.sInstance != null && NetworkWatcher.sInstance.mWifiConnected;
    }
    
    private static boolean isWifiConnected(final Context context) {
        final WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
        boolean b = false;
        if (wifiManager != null) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            b = false;
            if (connectionInfo != null) {
                final SupplicantState supplicantState = connectionInfo.getSupplicantState();
                final SupplicantState completed = SupplicantState.COMPLETED;
                b = false;
                if (supplicantState == completed) {
                    b = true;
                }
            }
        }
        return b;
    }
    
    public static void destroy() {
        if ( sInstance != null) {
             sInstance.mContext.unregisterReceiver( sInstance.mReceiver);
             sInstance.mContext = null;
             sInstance.mCallback = null;
             sInstance = null;
        }
    }
    
    
    public static void init(Context context, OnNetworkWatcherCallback networkWatcherCallback) {
        if ( sInstance == null) {
             sInstance = new NetworkWatcher(context, networkWatcherCallback);
        }
    }
    
    public interface OnNetworkWatcherCallback {
        void onWifiConnected();        
        void onWifiDisconnected();
    }
}
