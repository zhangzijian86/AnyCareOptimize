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

	//���ģ�����Ӧ�ó���Ŀ�ʼ��ڵ�ַ
	//English��The entire application start entrance.
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		SDKInitializer.initialize(this);
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}
	
	//��Ӧ�ó����ڴ治��ʱ����������ɱ��Ӧ�ó���
	//When not enough memory for applications, 
	//will kill the application here
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}

	// ���������ģ��һ�����̻��������������ԶҲ���ᱻ���á�
	//This function is a process simulation environment, 
	//in the real machine would never be called.
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
