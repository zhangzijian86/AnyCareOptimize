package com.fuer.anycare.common.application;

import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.res.Configuration;

public class MyApplication extends Application{

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	//中文：整个应用程序的开始入口地址
	//English：The entire application start entrance.
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		SDKInitializer.initialize(this);
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}
	
	//当应用程序内存不够时，会在这里杀死应用程序
	//When not enough memory for applications, 
	//will kill the application here
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}

	// 这个函数是模拟一个过程环境，在真机中永远也不会被调用。
	//This function is a process simulation environment, 
	//in the real machine would never be called.
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}

