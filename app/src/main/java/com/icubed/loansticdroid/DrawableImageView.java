package com.icubed.loansticdroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ScaleGestureDetector;
import android.view.View;


public class DrawableImageView extends android.support.v7.widget.AppCompatImageView
{

    private static final String TAG = "DrawableImageView";

    private static final int SIZE_CHANGE_SPEED = 2;
      //vars

    private float width = 8f;

    private Activity mHostActivity;


    // Scales
    float mMinWidth = 8f;
    float mMaxWidth = 500f;
    private ScaleGestureDetector mScaleGestureDetector;

    private int mScreenWidth;








    public DrawableImageView(Context context) {
        super(context);
        init(context);
    }

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public DrawableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {

        setDrawingCacheEnabled(true);
        if(context instanceof Activity) {
            mHostActivity = (Activity) context;

            mScaleGestureDetector = new ScaleGestureDetector(mHostActivity, new ScaleListener());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mHostActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mScreenWidth = displayMetrics.widthPixels;

            int screenHeight = displayMetrics.heightPixels;

            float density = displayMetrics.density;
            int bottomMargin = (int)mHostActivity.getResources().getDimension(R.dimen.cam_widget_margin_bottom);


            int bottom = screenHeight;


        }
    }





    public void setWidth(float width) {
        this.width = width;
    }


    private void hideStatusBar() {

        if(mHostActivity != null){
            View decorView = mHostActivity.getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if(scaleFactor > 1.011 || scaleFactor < 0.99) {



                    float prevWidth = width;
                    if(scaleFactor > 1){
                        width += ( (SIZE_CHANGE_SPEED + (width * 0.05)) * scaleFactor );
                    }
                    else{
                        width -= ( (SIZE_CHANGE_SPEED + (width * 0.05)) * scaleFactor );
                    }
                    if ( width > mMaxWidth) {
                        width = prevWidth;
                    }
                    else if (width < mMinWidth) {
                        width = prevWidth;
                    }
                }




            return true;
        }
    }

}








