package com.example.loadanim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class LoadView extends View {

	private LoadView thiss;

	private int vWidth;
	private int vHeight;
	// 绘画小球
	private Paint bPaint;
	private int bRadius = 10;
	private int bDis = 60;
	private int[] bPos = new int[4];
	private int[] cPos = new int[4];
	private boolean isFirst = true;
	// 绘画大嘴
	private Paint mPaint;
	private int mRadius = 50;
	private int startAngle = 45;
	// 大嘴的眼睛
	private Paint ePaint;
	private int eRadius = 10;
	private Paint rPaint;
	private int rRadius = 3;
	// 文字
	private int textWidth;
	private Paint tPaint;
	private int textIndex = 0;
	private String[] loadsText = { "加载中.", "加载中..", "加载中...", "加载中...." };
	private String text = loadsText[textIndex];
	private MouthThread mouthThread;
	private BallThread ballThread;
	private TextThread textThread;

	public LoadView(Context context) {
		this(context, null);
	}

	public LoadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化参数
	 */
	private void init() {
		thiss = this;
		// 大嘴的参数
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);

		// 小球的参数
		bPaint = new Paint();
		bPaint.setColor(Color.RED);
		bPaint.setAntiAlias(true);
		bPaint.setStyle(Paint.Style.FILL);

		// 眼睛的参数
		ePaint = new Paint();
		ePaint.setColor(Color.WHITE);
		ePaint.setAntiAlias(true);
		ePaint.setStyle(Paint.Style.FILL);
		rPaint = new Paint();
		rPaint.setColor(Color.BLACK);
		rPaint.setStyle(Paint.Style.FILL);


		//
		tPaint = new Paint();
		tPaint.setColor(Color.BLACK);
		tPaint.setTextSize(30);
		textWidth = (int) tPaint.measureText("加载中..");

		// 开启嘴动画
		mouthThread = new MouthThread();
		mouthThread.start();
		ballThread = new BallThread();
		ballThread.start();
		textThread = new TextThread();
		textThread.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RectF rectF = new RectF();
		rectF.left = vWidth / 2 - 50 - 2 * mRadius;
		rectF.top = vHeight / 2 - mRadius;
		rectF.bottom = vHeight / 2 + mRadius;
		rectF.right = vWidth / 2 - 50;
		canvas.drawArc(rectF, startAngle, 360 - startAngle * 2, true, mPaint);
		// eye
		canvas.drawCircle(vWidth / 2 - 50 - mRadius, vHeight / 2-20, eRadius,
				ePaint);
		canvas.drawCircle(vWidth / 2 - 50 - mRadius , vHeight / 2-20, rRadius,
				rPaint);
		// 小球
		if (isFirst) {
			for (int i = 0; i < 4; i++) {
				bPos[i] = vWidth / 2 + i * bDis;
				cPos[i] = vWidth / 2 + i * bDis;
				canvas.drawCircle(bPos[i], vHeight / 2, bRadius, bPaint);
			}
			isFirst=false;
		} else {
			for (int i = 0; i < 4; i++)
				canvas.drawCircle(bPos[i], vHeight / 2, bRadius, bPaint);
		}
		// load 文字
		canvas.drawText(text, vWidth / 2 - textWidth / 2, vHeight/2 + mRadius+30, tPaint);
	}

	class BallThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(true){
				for(int i=0;i<4;i++){
					bPos[i]-=3;
					if(bPos[i]<vWidth/2-50-bRadius*1.5){
						bPos[i]=cPos[3]-bRadius-5;
					}
				}
				try {
					Thread.sleep(90);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class TextThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (true) {
				text = loadsText[textIndex];
				textIndex = (++textIndex) % 4;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class MouthThread extends Thread {
		boolean openMouth = false;
		@Override
		public void run() {
			while (true) {
				if (openMouth) {
					while (startAngle <= 45) {
						startAngle++;
						try {
							Thread.sleep(18);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						postInvalidate();
					}
					openMouth = false;
				} else {
					while (startAngle >= 0) {
						startAngle--;
						try {
							Thread.sleep(18);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						postInvalidate();
					}
					openMouth = true;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		vWidth = w;
		vHeight = h;
	}

	public void stopLoad() {
		if (mouthThread != null)
			mouthThread.interrupt();
		if (ballThread != null)
			ballThread.interrupt();
		if (textThread != null)
			textThread.interrupt();
		/**
		 * 隐藏loadview
		 */
		Animation hide_anim = AnimationUtils.loadAnimation(getContext(),
				R.anim.load_hide_anim);
		hide_anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				System.out.println("start");
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				thiss.setVisibility(View.GONE);
				((ViewGroup) thiss.getParent()).removeView(thiss);
			}
		});
		this.startAnimation(hide_anim);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		if (mouthThread != null)
			mouthThread.interrupt();
		if (ballThread != null)
			ballThread.interrupt();
		if (textThread != null)
			textThread.interrupt();
	}
}
