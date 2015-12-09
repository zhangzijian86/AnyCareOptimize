package com.fuer.anycare.chart.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.fuer.main.anycare.R;

public class SlideSwitch extends View {
	
	private int mBmpWidth = 0;//控件高度
	private int mBmpHeight = 0;//控件高度
	private int mThumbWidth = 0;//圆心宽度
	//开关状态图  
	Bitmap mSwitch_Back, mSwitch_Slide;
	
	//构造方法
	public SlideSwitch(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	//构造方法
	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	//构造方法
	public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}
	
	//初始化两幅图片  
	private void init(){
		Resources res = getResources();  
		mSwitch_Back = BitmapFactory.decodeResource(res, R.drawable.slide_back);  
		mSwitch_Slide = BitmapFactory.decodeResource(res, R.drawable.circle);  
		mBmpWidth = mSwitch_Back.getWidth();	//背景的宽度
		mBmpHeight = mSwitch_Back.getHeight();  //背景的高度
		mThumbWidth = mSwitch_Slide.getWidth();//圆圈的宽度  
	}
	
	@Override
	public void setLayoutParams(LayoutParams params) {
		// TODO Auto-generated method stub
		params.width = mBmpWidth;
		params.height = mBmpHeight;
		super.setLayoutParams(params);
	}
	
	/**
	 * 
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	/**
	 * 
	 * */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
	}
}
