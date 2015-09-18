package milkmidi.pipi.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;


/**
 * @author milkmidi
 * 2014 07 24
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/ThumbnailUtils.java
 */
public class BitmapUtil {
	private static final int UNCONSTRAINED = -1;
	
	public static Bitmap getCircleBitmap( Bitmap bitmap ) {
		int size = 76;
		int size2 = size / 2;
		Bitmap output = Bitmap.createBitmap(
				 size, size,
	             Config.ARGB_8888
	             );
        Canvas canvas = new Canvas(output); 
        
        final int color = 0xff000000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(
        		0, 
        		0, 
        		size,
        		size
//        		bitmap.getWidth(),
//                bitmap.getHeight()
                );

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(size2, size2 , size2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        
        
//        Rect destRect = new Rect(0,-50,size,size);
//        canvas.drawBitmap(bitmap, destRect, destRect, paint);
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
	}

	
/*
	public static Bitmap getDetailContactCircleBitmap( Bitmap bitmap, int storkeWidth, int storkeColor ) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int size = Math.min( width, height );
		int size2 = size /2;

		Bitmap output = Bitmap.createBitmap( size, size, Config.ARGB_8888 );
		Canvas canvas = new Canvas( output );

		final Paint paint = new Paint();

		paint.setAntiAlias( true );
		canvas.drawARGB( 0, 0, 0, 0 );
		paint.setColor( 0xFF000000 );
		canvas.drawCircle( size2, size2, size2, paint );

		paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN ) );
		canvas.drawBitmap( bitmap, 0, 0, paint );

		paint.setStyle( Style.STROKE );		
		
		paint.setColor( 0x33ffffff );
		paint.setStrokeWidth( storkeWidth*8);
		canvas.drawCircle( size2, size2, size2, paint );
		
		paint.setColor( storkeColor );
		paint.setStrokeWidth( storkeWidth * 2 );
		canvas.drawCircle( size2, size2, size2, paint );
		
		paint.setColor( 0xffffffff );
		paint.setStrokeWidth( storkeWidth  );
		canvas.drawCircle( size2, size2, size2, paint );
		
		

		return output;
	}
	public static Bitmap getCircleBitmap( Bitmap bitmap, int storkeWidth, int storkeColor ) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int size = Math.min( width, height );
		int size2 = size /2;

		Bitmap output = Bitmap.createBitmap( size, size, Config.ARGB_8888 );
		Canvas canvas = new Canvas( output );

		final Paint paint = new Paint();

		paint.setAntiAlias( true );
		canvas.drawARGB( 0, 0, 0, 0 );
		paint.setColor( 0xFF000000 );
		canvas.drawCircle( size2, size2, size2, paint );

		paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN ) );
		canvas.drawBitmap( bitmap, 0, 0, paint );

		paint.setStyle( Style.STROKE );
		
		
		paint.setColor( storkeColor );
		paint.setStrokeWidth( storkeWidth );
		canvas.drawCircle( size2, size2, size2, paint );
		

		return output;
	}
	
	
	public static Bitmap getCircleBitmap( Bitmap bitmap ) {
		return getCircleBitmap( bitmap , 20, 0xffffffff);
	}
	
	public static Bitmap getGroupCircleBitmap( Bitmap bitmap , int size ) {
//		return bitmap
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();		
		int size2 = size / 2;				
		float ratio = (float)size / Math.min( w, h ) ;
		int newW = (int) (ratio * w);
		int newH = (int) (ratio * h);
//		Log.i( "BitmapUtil", w + "x"+h );
//		Log.i( "BitmapUtil", newW + "x"+newH );
		Bitmap targetBitmap = Bitmap.createScaledBitmap( bitmap, newW, newH ,false );
//		bitmap.recycle();
		Bitmap output = Bitmap.createBitmap(size, size,Bitmap.Config.ARGB_8888);
		final Rect rect = new Rect( 0, 0, w, h );
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias( true );
		canvas.drawCircle(size2, size2,size2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap( targetBitmap, rect,  rect, paint);
		targetBitmap.recycle();
		return output;	
	}
	
	
	public static Bitmap getRoundedShape( Bitmap bitmap, int pixels ) {
		Bitmap output = Bitmap.createBitmap( bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888 );
		Canvas canvas = new Canvas( output );

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect( 0, 0, bitmap.getWidth(), bitmap.getHeight() );
		final RectF rectF = new RectF( rect );
		final float roundPx = pixels;

		paint.setAntiAlias( true );
		canvas.drawARGB( 0, 0, 0, 0 );
		paint.setColor( color );
		
		int w2 = bitmap.getWidth() / 2;
		final Rect rect2 = new Rect( w2, 0, bitmap.getWidth(), bitmap.getHeight() );
		canvas.drawBitmap( bitmap, rect2, rect2, paint );
		canvas.drawRoundRect( rectF, roundPx, roundPx, paint );

		paint.setXfermode( new PorterDuffXfermode( Mode.SRC_IN ) );
		canvas.drawBitmap( bitmap, rect, rect, paint );
		return output;
	}*/

}
