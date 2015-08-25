package com.qvod.lib.demo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class CircleProgressBar extends ProgressBar {

	private float mStartAngle = -90.0f;
	
	private float mAngle = 0.0f;
	
	private Paint mPaint;
	
	private PorterDuffXfermode mPorter;

	private DrawFilter mAliasDrawFilter;
	
	public CircleProgressBar(Context context) {
		this(context, null);
	}

	public CircleProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.progressBarStyle);
	}

	@SuppressLint("NewApi") 
	public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPorter = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		mAliasDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public void setStartAngle(float startAngle) {
		mStartAngle = startAngle;
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		float per = (float) progress / (float) getMax();
		mAngle = 360.0f * per;
		invalidate();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		Drawable d = getIndeterminateDrawable();
		if (d != null) {
			Rect outRect = new Rect();
			getDrawingRect(outRect);
			
			RectF outRectF = new RectF(outRect);
			//图片抗锯齿
			canvas.setDrawFilter(mAliasDrawFilter); 
			canvas.saveLayer(outRectF, null, Canvas.ALL_SAVE_FLAG);

			mPaint.setXfermode(null);
			canvas.translate(getPaddingLeft(), getPaddingTop());
			d.setBounds(outRect);
			d.draw(canvas);

			mPaint.setXfermode(mPorter);
			RectF rectF = new RectF(outRect);
			rectF.left -= rectF.right;
			rectF.top -= rectF.bottom;
			rectF.right *= 2;
			rectF.bottom *= 2;
			canvas.drawArc(rectF, mStartAngle + mAngle, 360 - mAngle, true, mPaint);

			canvas.restore();
		}
	}
}
