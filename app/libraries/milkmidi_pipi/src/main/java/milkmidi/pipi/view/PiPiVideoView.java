package milkmidi.pipi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

//http://marshal.easymorse.com/archives/3160
//http://clseto.mysinablog.com/index.php?op=ViewArticle&articleId=2992625
public class PiPiVideoView extends VideoView {

	public enum ScaleType {
		ORIGINAL, // original aspect ratio
		CENTER_INSIDE,
		CENTER,
		CENTER_CROP 
	};

	private static final String	TAG	= "[MilkVideoView]";

	public PiPiVideoView( Context context ) {
		super( context );
		init( context );
	}

	public PiPiVideoView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		init( context );
	}

	public PiPiVideoView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		init( context );
//		ImageView.ScaleType.CENTER_CROP
	}

	private void init( Context pContext ) {
		mScreenMode = ScaleType.ORIGINAL;
	}

	private int			mVideoWidth		= 0;
	private int			mVideoHeight	= 0;

	private ScaleType	mScreenMode;

	public void setScreenMode( ScaleType pMode ) {
		this.mScreenMode = pMode;
	}

	public void setOriginVideoSize( int pWidth, int pHeight ) {
		this.mVideoWidth = pWidth;
		this.mVideoHeight = pHeight;
		// getHolder().setFixedSize(mVideoWidth/2, mVideoHeight/2);
		getHolder().setFixedSize( mVideoWidth, mVideoHeight );
		forceLayout();
		requestLayout();
		invalidate();
		Log.i( TAG, "setOriginVideoSize:   " + mVideoWidth + "x" + mVideoHeight );		
	}

	private void trace( Object... o ) {
//		Log.i( TAG, Arrays.toString( o ) );
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
		// Log.i("@@@@", "onMeasure");

		trace( MeasureSpec.toString( widthMeasureSpec ) );
		trace( MeasureSpec.toString( heightMeasureSpec ) );
		// super.onMeasure( widthMeasureSpec, heightMeasureSpec );

		int width = getDefaultSize( mVideoWidth, widthMeasureSpec );
		int height = getDefaultSize( mVideoHeight, heightMeasureSpec );
		
		trace("1: onMeasure:" + width + "x" + height,"displayModeL:" + this.mScreenMode );
		trace("2: videoSize:" + this.mVideoWidth + "x" + this.mVideoHeight );		

		if ( mVideoWidth > 0 && mVideoHeight > 0 ) {			
			switch ( mScreenMode ) {
				case CENTER:
//					width = mVideoWidth;
//					height = mVideoHeight;
					width = MeasureSpec.makeMeasureSpec( mVideoWidth, MeasureSpec.EXACTLY );
					height = MeasureSpec.makeMeasureSpec( mVideoHeight, MeasureSpec.EXACTLY );
					break;
					
				case CENTER_INSIDE:
				case ORIGINAL:
					if ( mVideoWidth * height > width * mVideoHeight ) {
						trace( "3: image too tall, correcting" );
						height = width * mVideoHeight / mVideoWidth;
					}
					else if ( mVideoWidth * height < width * mVideoHeight ) {
						trace( "3: image too wide, correcting" );
						width = height * mVideoWidth / mVideoHeight;
					}
					else {
						trace( "3: aspect ratio is correct: " + width + "/" + height
								+ "=" + mVideoWidth + "/" + mVideoHeight );
					}
					break;

				case CENTER_CROP:
					if ( mVideoWidth * height > width * mVideoHeight ) {
						trace( "3: image too tall, correcting" );
						width = height * mVideoWidth / mVideoHeight;
					}
					else if ( mVideoWidth * height < width * mVideoHeight ) {
						trace( "3: image too wide, correcting" );
						height = width * mVideoHeight / mVideoWidth;
					}
					else {
						trace( "3: aspect ratio is correct: " + width + "/" + height
								+ "=" + mVideoWidth + "/" + mVideoHeight );
					}
					break;
			}
			trace( "4: " + width + "x" + height );
			setMeasuredDimension( width, height );
		}else{
			super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		}	

	}
	
	

	@Override
	protected void onLayout( boolean changed, int left, int top, int right,
			int bottom ) {
		trace( "onLayout:   " + left + "," + top + "," + right + "," + bottom );
//		super.onLayout( changed, left, 5100, right, bottom );
	}

}
