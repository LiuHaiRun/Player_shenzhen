package com.ghts.player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import com.ghts.player.R;

/**
 * 显示模拟时钟的控件
 */
public class MClock extends View {
	
	private int module_type; // 模块类型
	
	private int zOrder; // 模块的ZOrder
	
	private String file_version;	//文件版本
	
	private String module_name;	//控件名
	
	private int module_uid;//控件标识
	
	private int module_gid;//控件组标识
	
	// 时钟盘，分针、秒针、时针对象

	Bitmap mBmpDial;
	Bitmap mBmpHour;
	Bitmap mBmpMinute;
	Bitmap mBmpSecond;

	BitmapDrawable bmdHour;
	BitmapDrawable bmdMinute;
	BitmapDrawable bmdSecond;
	BitmapDrawable bmdDial;

	Paint mPaint;
	Handler tickHandler;

	int mWidth;
	int mHeigh;
	int mTempWidth;

	int mTempHeigh;
	int centerX;
	int centerY;

	int viewWidth;
	int viewHeight;

	/**
	 * 构造方法
	 * @param context Activity的上下文
	 */
	public MClock(Context context) {
		super(context);
		// 构造图片矩阵。
		mBmpHour = BitmapFactory.decodeResource(getResources(),
				R.mipmap.hour_hand);
		bmdHour = new BitmapDrawable(context.getResources(),mBmpHour);

		mBmpMinute = BitmapFactory.decodeResource(getResources(),
				R.mipmap.minute_hand);
		bmdMinute = new BitmapDrawable(context.getResources(),mBmpMinute);

		mBmpSecond = BitmapFactory.decodeResource(getResources(),
				R.mipmap.second_hand);
		bmdSecond = new BitmapDrawable(context.getResources(),mBmpSecond);

		mBmpDial = BitmapFactory.decodeResource(getResources(),
				R.mipmap.clock_dial);
		bmdDial = new BitmapDrawable(context.getResources(),mBmpDial);
		// 获取表盘图片的宽高
		mWidth = mBmpDial.getWidth();
	
		mHeigh = mBmpDial.getHeight();

		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		run();
	}

	public void run() {
		tickHandler = new Handler();
		tickHandler.post(tickRunnable);

	}

	// 开启线程,延迟一秒发消息，刷新界面。
	private Runnable tickRunnable = new Runnable() {
		private boolean hasMeasured = true;
		// 控件的宽高
		public void run() {
			if (hasMeasured) {
				viewWidth = getWidth();
				viewHeight = getHeight();
				// 表盘中心的x、y值。
				centerX = viewWidth / 2;
				centerY = viewHeight / 2;
				hasMeasured = false;
			}
			postInvalidate();
			tickHandler.postDelayed(tickRunnable, 1000);
		}
	};

	/**
	 * 控件画出自己的方法
	 */
	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 获取当前的时、分、秒
		long currentTimeMillis = System.currentTimeMillis();
		Time time = new Time();
		time.set(currentTimeMillis);
		int hour = time.hour;
		int minute = time.minute;
		int second = time.second;
		// 根据当前时间计算时针、分针、秒针的角度。
		float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
		float minuteRotate = minute * 6.0f;
		float secondRotate = second * 6.0f;

		boolean scaled = false;
		// 当外框的宽或高小于表盘的宽高时，按比例缩小表盘（因为容不下整个表盘）。
		if (viewWidth < mWidth || viewHeight < mHeigh) {
			scaled = true;
			float scale = Math.min((float) viewWidth / (float) mWidth,
					(float) viewHeight / (float) mHeigh);
			canvas.save();
			canvas.scale(scale, scale, centerX, centerY);
		}

		// 定义表盘的边界，并将它画出来。
		bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeigh / 2),
				centerX + (mWidth / 2), centerY + (mHeigh / 2));
		bmdDial.draw(canvas);

		mTempWidth = bmdHour.getIntrinsicWidth();
		mTempHeigh = bmdHour.getIntrinsicHeight();

		// 保存状态，画好时针
		canvas.save();
		canvas.rotate(hourRotate, centerX, centerY);
		bmdHour.setBounds(centerX - (mTempWidth * 3 / 4), centerY - mTempHeigh,
				centerX + (mTempWidth / 4), centerY + (mTempHeigh / 4));
		bmdHour.draw(canvas);

		canvas.restore();
		// 恢复画布状态，画好分针
		mTempWidth = bmdMinute.getIntrinsicWidth();
		mTempHeigh = bmdMinute.getIntrinsicHeight();
		canvas.save();
		canvas.rotate(minuteRotate, centerX, centerY);
		bmdMinute.setBounds(centerX - (mTempWidth * 3 / 4), centerY
				- mTempHeigh, centerX + (mTempWidth / 4), centerY
				+ (mTempHeigh / 4));
		bmdMinute.draw(canvas);

		// 恢复画布状态，画好秒针
		canvas.restore();
		mTempWidth = bmdSecond.getIntrinsicWidth();
		mTempHeigh = bmdSecond.getIntrinsicHeight();
		canvas.rotate(secondRotate, centerX, centerY);
		bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY - mTempHeigh,
				centerX + (mTempWidth / 2), centerY + (mTempHeigh / 2));
		bmdSecond.draw(canvas);

		// 如果进行了缩放，恢复画布。
		if (scaled) {
			canvas.restore();
		}
	}
	
	public int getModule_type() {
		return module_type;
	}

	public void setModule_type(int module_type) {
		this.module_type = module_type;
	}

	public int getzOrder() {
		return zOrder;
	}

	public void setzOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	public String getFile_version() {
		return file_version;
	}

	public void setFile_version(String file_version) {
		this.file_version = file_version;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public int getModule_uid() {
		return module_uid;
	}

	public void setModule_uid(int module_uid) {
		this.module_uid = module_uid;
	}

	public int getModule_gid() {
		return module_gid;
	}

	public void setModule_gid(int module_gid) {
		this.module_gid = module_gid;
	}
}