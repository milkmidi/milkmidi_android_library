package milkmidi.pipi.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 計算公式 pixels = dips * (density / 160)
 * http://blog.csdn.net/feng88724/article/details/6599821
 *
 * @version 1.0.1 2010-12-11
 *
 * @author
 */
public class DensityUtil{
    private static final String TAG = "[DensityUtil]";

    public static float scale = 0.0f;
    // 當前屏幕的densityDpi
    public static float densityDpi = 0.0f;
    public static float density = 0.0f;
    public static int widthPixels;
    public static int heightPixels;

    private static boolean mInited = false;
    public static void init( Context context )	{
        if ( mInited ) {
            return;
        }
        mInited = true;

        // 獲取當前屏幕
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        // 設置DensityDpi
        densityDpi = dm.densityDpi;
        // 密度因子
        density = dm.density;
        scale = densityDpi / 160;
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
        Log.i(TAG, widthPixels + "x" + heightPixels + ", densityDpi:" + densityDpi + " ,desity:" + density);
        dm = null;
    }
    /**
     * 密度轉換像素
     * */
    public static int dip2px( float dipValue )	{
        return (int) (dipValue * scale + 0.5f);
    }
    public static int dip2px( float dipValue, float maxScale )	{
        if (scale >= maxScale) {
            return (int) dipValue;
        }
        return (int) (dipValue * scale + 0.5f);
    }
    /**
     * 像素轉換密度
     * */
    public int px2dip( float pxValue )	{
        return (int) (pxValue / scale + 0.5f);
    }

}
