package com.repay.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.repay.android.R;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class RoundedImageView extends ImageView
{
	public static final long ANIMATION_LENGTH = 300;
	private static final String DEFAULT_BACKGROUND = "#34495e";
	private static final double SCALE_FACTOR = 0.92;
	private Paint mBackgroundPaint;
	private boolean mCrop = true, mAnimate = true;

	public RoundedImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		int color = R.color.debt_neutral;
		if (attrs != null)
		{
			TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0);
			try
			{
				mCrop = array.getBoolean(R.styleable.RoundedImageView_cropImage, true);
				mAnimate = array.getBoolean(R.styleable.RoundedImageView_animateChanges, true);
				color = array.getColor(R.styleable.RoundedImageView_outerBackground, R.color.debt_neutral);
			} finally
			{
				array.recycle();
			}
		}

		mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundPaint.setColor(color);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius)
	{
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
		{
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		} else
		{
			sbmp = bmp;
		}
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		return output;
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas)
	{
		if (getDrawable() == null)
		{
			return;
		}

		if (getWidth() == 0 || getHeight() == 0)
		{
			return;
		}
		Bitmap b = ((BitmapDrawable) getDrawable()).getBitmap();
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);

		double w = getWidth() * SCALE_FACTOR;

		double left = getWidth() - w;
		double top = getHeight() - (getHeight() * SCALE_FACTOR);

		if (mCrop)
		{
			Bitmap roundBitmap = getCroppedBitmap(bitmap, (int) w);
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mBackgroundPaint);
			canvas.drawBitmap(roundBitmap, (int) left / 2, (int) top / 2, null);
		} else
		{
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mBackgroundPaint);
			canvas.drawBitmap(bitmap, (int) left / 2, (int) top / 2, null);
		}
	}

	public void setOuterColor(int color)
	{
		mBackgroundPaint.setColor(color);
		invalidate();
		requestLayout();
	}
}
