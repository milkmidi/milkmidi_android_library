package milkmidi.pipi.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

import android.os.Bundle;
/**
 * @author milkmidi
 * */
public class StringUtil {
    /**
     * 判斷串字是否為 null 或是 空字元
     * @param value
     * @return Boolean
     * */
    public static boolean isNullOrEmpty(String value){
        return value == null || value.equals("") ;
    }
	
	/*private static String addCommon(String pValue) {
		final String[] splitStrs = pValue.split("");
		List<String> splitList = Arrays.asList(splitStrs);
		Collections.reverse(splitList);

		final int length = splitStrs.length;

		final List<String> results = new ArrayList<String>();

		for (int i = 0; i < length; i++) {
			results.add(splitList.get(i));
			if (i % 3 == 2) {
				results.add(",");
			}
		}
		Collections.reverse(results);

		Iterator<String> it = results.iterator();
		final StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			String value = (String) it.next();
			sb.append(value);
		}
		return sb.toString();
	}*/


    /**
     * 把  Url 的 String 轉成 Bundle 物件
     * @param string 要轉換的字串, RS=OK&NAME=奶綠茶
     * @return Bundle 物件
     * */
    public static Bundle decodeUrl(String string) {
        Bundle params = null;
        if (string != null) {
            params = new Bundle();
            String array[] = string.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (v.length == 2) {
                    params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }
    /**
     * 把 Bundle 物件編碼成 Url 格式
     * @param parameters 要編碼的 Bundle 物件
     * @param preventCache 是否避免快取
     * @return 編碼後的String
     * */
    public static String encodeUrl(Bundle parameters , boolean preventCache) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) first = false; else sb.append("&");

            String value = parameters.getString(key);
            if (value != null) {
                sb.append(URLEncoder.encode(key) + "=" +
                        URLEncoder.encode(value));
            }

        }
        if (preventCache) {
            sb.append("&preventcache="+System.currentTimeMillis());
        }
        return sb.toString();
    }
    /**
     * 把 Bundle 物件編碼成 Url 格式, 預設是 prevent cache
     * @param pParameters 要編碼的 Bundle 物件
     * @return encoded String
     * */
    public static String encodeUrl(Bundle parameters) {
        return StringUtil.encodeUrl(parameters,true);
    }



}
