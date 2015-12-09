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
	
	private int mBmpWidth = 0;//�ؼ��߶�
	private int mBmpHeight = 0;//�ؼ��߶�
	private int mThumbWidth = 0;//Բ�Ŀ��
	//����״̬ͼ  
	Bitmap mSwitch_Back, mSwitch_Slide;
	
	//���췽��
	public SlideSwitch(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	//���췽��
	public SlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	//���췽��
	public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}
	
	//��ʼ������ͼƬ  
	private void init(){
		Resources res = getResources();  
		mSwitch_Back = BitmapFactory.decodeResource(res, R.drawable.slide_back);  
		mSwitch_Slide = BitmapFactory.decodeResource(res, R.drawable.circle);  
		mBmpWidth = mSwitch_Back.getWidth();	//�����Ŀ��
		mBmpHeight = mSwitch_Back.getHeight();  //�����ĸ߶�
		mThumbWidth = mSwitch_Slide.getWidth();//ԲȦ�Ŀ��  
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
