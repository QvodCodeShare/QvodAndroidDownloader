package com.qvod.lib.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
/**
 * 
 *  重写onMeasure 以达到 CircleProgressBar可以使用 wrap_content属性
 * @author  xujiao
 * @data:  2014-10-21 下午5:18:33 
 * @version:  V1.0
 */
public class UpgradeProgressBar extends CircleProgressBar {

    public UpgradeProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UpgradeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpgradeProgressBar(Context context) {
        super(context);
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));  
    }  
  
    private int measureWidth(int measureSpec) {  
        int result = 0;  
        int specMode = MeasureSpec.getMode(measureSpec);  
        int specSize = MeasureSpec.getSize(measureSpec);  
  
        if (specMode == MeasureSpec.EXACTLY) {  
            result = specSize;  
        } else {  
            result = (int) getBackground().getIntrinsicWidth() + getPaddingLeft() + getPaddingRight();  
            if (specMode == MeasureSpec.AT_MOST) {  
                result = Math.min(result, specSize); 
            }  
        }  
  
        return result;  
    }  
  
    private int measureHeight(int measureSpec) {  
        int result = 0;  
        int specMode = MeasureSpec.getMode(measureSpec);  
        int specSize = MeasureSpec.getSize(measureSpec);  
  
       
        if (specMode == MeasureSpec.EXACTLY) {  
            result = specSize;  
        } else {  
            result = (int) getBackground().getIntrinsicHeight()+ getPaddingTop() + getPaddingBottom();  
            if (specMode == MeasureSpec.AT_MOST) {  
                result = Math.min(result, specSize);  
            }  
        }  
        return result;  
    }  
}  

