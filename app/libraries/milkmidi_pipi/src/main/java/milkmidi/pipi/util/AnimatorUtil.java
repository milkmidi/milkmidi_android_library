package milkmidi.pipi.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * 2014 07 15
 * version 1.0.1 
 */
public class AnimatorUtil {
	
	

	
	public static Animator fadeIn(final View view ){
		view.setAlpha( 0.0f );		
		ObjectAnimator ani = ObjectAnimator.ofFloat( view, "alpha", 0.1f , 1.0f );
		ani.setDuration( 500 );
		ani.setInterpolator( new DecelerateInterpolator  ( 1.5f ) );
		ani.start();			
		return ani;
		
	}
	public static Animator fadeOut(final View view ){
		ObjectAnimator ani = ObjectAnimator.ofFloat( view, "alpha", 1.0f , 0.0f );
		ani.setDuration( 400 );
		ani.setInterpolator( new AccelerateInterpolator  ( 1.5f ) );
		ani.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            	view.setVisibility(View.GONE);
            }
        } );
		ani.start();			
		return ani;		
	}
	

	public static AnimatorSet in(View view , int delay,Animator... ani){
		AnimatorSet set = new AnimatorSet();
		set.setDuration( 500 );		
		set.playTogether( ani );
		set.setStartDelay( delay );
		set.setInterpolator( new DecelerateInterpolator  ( 1.5f ) );
		set.start();
		return set;
	}
	public static AnimatorSet in(View view , Animator... ani){		
		return in( view , 0 , ani);
	}
	
	
	public static AnimatorSet out(View view , int delay,Animator... ani){
		AnimatorSet set = new AnimatorSet();
		set.setDuration( 400 );		
		set.playTogether( ani );
		set.setStartDelay( delay );
		set.setInterpolator( new AccelerateInterpolator  ( 1.5f ) );
		set.start();		
		return set;
	}
	public static AnimatorSet out(View view , Animator... ani){		
		return out( view , 0 , ani);
	}

	
	
	public static AnimatorSet animatorIn(View view ){
		return animatorIn(view, 0);
	}
	public static AnimatorSet animatorIn(View view , int delay){
		float targetX = view.getX();
		float startX = targetX - 130.0f;		
		return in( view , delay ,
				ObjectAnimator.ofFloat( view, "x", startX , targetX ),
				ObjectAnimator.ofFloat( view, "alpha", 0.2f , 1.0f )
				);	
	}
	public static AnimatorSet animatorOut(View view ){
		return animatorOut(view, 0);
	}
	public static AnimatorSet animatorOut(View view , int delay){
		float startX = view.getX();
		float targetX = startX - 130.0f;		
		return out( view , delay ,
				ObjectAnimator.ofFloat( view, "x", startX , targetX ),
				ObjectAnimator.ofFloat( view, "alpha", 1.0f , 0.0f )
				);	
	}
	
	
	

}
