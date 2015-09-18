package milkmidi.pipi.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FitImageView extends ImageView {

	public FitImageView( Context context ) {
		super( context );
	}

	public FitImageView( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}

	public FitImageView( Context context, AttributeSet attrs, int defStyleAttr ) {
		super( context, attrs, defStyleAttr );
	}

	@Override
	protected void onMeasure( final int widthMeasureSpec,	final int heightMeasureSpec ) {
		final Drawable d = this.getDrawable();

		if (d != null) {
			// ceil not round - avoid thin vertical gaps along the left/right
			// edges
			
			final int drawableW = d.getIntrinsicWidth();
			final int drawableH = d.getIntrinsicHeight();
			float ratio =  (float) drawableH / drawableW;			
			final int width = MeasureSpec.getSize( widthMeasureSpec );
			final int height = (int) Math.ceil( width * ratio );
			
			/*
			float scale=height/drawableH;

	          Matrix m = getImageMatrix();

	          float[] f = new float[9];
	          m.getValues(f);

	          f[Matrix.MSCALE_X]=scale;
	          f[Matrix.MSCALE_Y]=scale;

	          m.setValues(f);  
	          requestLayout();*/
			
			this.setMeasuredDimension( width, height );
		}
		else {
			super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		}
	}

}
