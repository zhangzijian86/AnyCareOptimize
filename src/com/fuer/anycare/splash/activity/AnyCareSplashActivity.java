package com.fuer.anycare.splash.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import cn.jpush.android.api.JPushInterface;

import com.fuer.anycare.common.application.MyApplication;
import com.fuer.anycare.initial.activity.GuideViewActivity;
import com.fuer.anycare.login.activity.AnyCareLoginActivity;
import com.fuer.anycare.main.activity.AnyCareMainActivity;
import com.fuer.main.anycare.R;

public class AnyCareSplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String firstOpen = readFirstOpenName();
		if("".equals(firstOpen)){
			startActivity(new Intent(getApplication(), GuideViewActivity.class));
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			AnyCareSplashActivity.this.finish();
		}else{
			setContentView(R.layout.layout_splash);
			Handler x = new Handler();
			x.postDelayed(new splashhandler(), 2000);
		}
	}
	class splashhandler implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String openParam = readOpenParam();
			if(openParam != null && !"".equals(openParam)){
				startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
			}else{
				startActivity(new Intent(getApplication(), AnyCareLoginActivity.class));
			}
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			AnyCareSplashActivity.this.finish();
		}
	}
	
	private String readOpenParam(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String openParam=sp.getString("openParam", "");
		return openParam;
	}
	
	private String readFirstOpenName(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String firstOpen=sp.getString("firstOpen", "");
		return firstOpen;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
	}
	
	
	
}

