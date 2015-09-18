package milkmidi.pipi.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public final class NetUtil {
//	private static final String TAG = "[NetUtil]";

	public static NetworkInfo getActiveNetworkInfo( Context context )	{
		ConnectivityManager cm = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	public static String getNetworkType( Context context )	{
		return getActiveNetworkInfo(context).getTypeName();
	}

	public static String getIPAddress( Context context )	{
		WifiManager wifiManager = 
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return intToIp(ipAddress);
	}

	private static String intToIp( int i )	{
		return 
				(i & 0xFF) + "." + 
				((i >> 8) & 0xFF) + "." + 
				((i >> 16) & 0xFF)	+ "." + 
				((i >> 24) & 0xFF);
	}
	
	public static boolean isNetworkAvailable( Context context )	{
		ConnectivityManager cm = 
				(ConnectivityManager) context.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			final NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
			/*NetworkInfo[] infos = cm.getAllNetworkInfo();			
			if (infos != null) {				
				for (NetworkInfo networkInfo : infos) {
					if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}				
			}*/
		}
		return false;
	}
	
	 public static WifiStateVO getWifiState( Context context){    	
    	ConnectivityManager cm = 
    			(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo myNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
		
		WifiStateVO vo = new WifiStateVO();		
		vo.mac = myWifiInfo.getMacAddress();
		
    	if (myNetworkInfo.isConnected()){
    		int myIp = myWifiInfo.getIpAddress();        
        
    		int intMyIp3 = myIp/0x1000000;
    		int intMyIp3mod = myIp%0x1000000;
        
    		int intMyIp2 = intMyIp3mod/0x10000;
    		int intMyIp2mod = intMyIp3mod%0x10000;
        
    		int intMyIp1 = intMyIp2mod/0x100;
    		int intMyIp0 = intMyIp2mod%0x100;
        
    		vo.ip = String.valueOf(intMyIp0)
    				+ "." + String.valueOf(intMyIp1)
    				+ "." + String.valueOf(intMyIp2)
    				+ "." + String.valueOf(intMyIp3)
    				;
        
    		vo.speed = String.valueOf(myWifiInfo.getLinkSpeed()) + " " + WifiInfo.LINK_SPEED_UNITS;
    	}
    	else{
    		
    	}
	    return vo;	    	
	}
	public static class WifiStateVO{
		public String ip;
		public String mac;
		public String speed;
	}
	
	public static Intent getActionWirelessSettingsIntent(){
		return new Intent(Settings.ACTION_WIRELESS_SETTINGS); 
	}
}
