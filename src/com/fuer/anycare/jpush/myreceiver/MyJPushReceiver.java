package com.fuer.anycare.jpush.myreceiver;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.fuer.anycare.map.activity.AnyCareJpushMapActivity;
import com.fuer.anycare.splash.activity.AnyCareSplashActivity;

public class MyJPushReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		int notifactionId = 0;
		try{
			//SDK 向 JPush Server 注册所得到的注册 全局唯一的 ID ，可以通过此 ID 向对应的客户端发送消息和通知。
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
	            //发送全局唯一的ID到服务器，做服务器的认证（有用功能以后再添加）
	        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//接收到推送下来的自定义消息
	        	Log.d("MyJPush", "[MyJPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
	        	//processCustomMessage(context, bundle);
	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {//接收到推送下来的通知
	            Log.d("MyJPush", "[MyJPushReceiver] 接收到推送下来的通知");
	            notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
	            Log.d("MyJPush", "[MyJPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//用户点击打开了通知
	        	//打开自定义的Activity
	        	String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	        	Log.d("MyJPush", "[MyJPushReceiver] 接收到推送下来的通知的内容: " + extras);
	        	JSONObject jsonObject = new JSONObject(extras);
	        	String fuerSign = jsonObject.getString("fuerSign");
	        	Intent i = null;
	        	if("qiujiu".equals(fuerSign)){
	        		Bundle b = new Bundle();
	        		String uuId = jsonObject.getString("uuId");
	        		String deviceType = jsonObject.getString("deviceType");
	    			b.putString("uuId",uuId);
	    			b.putString("deviceType",deviceType);
	        		i = new Intent(context, AnyCareJpushMapActivity.class);
	        		i.putExtras(b);
	        	}else if("fanxiang".equals(fuerSign)){
	        		Bundle b = new Bundle();
	        		String uuId = jsonObject.getString("uuId");
	        		String deviceType = jsonObject.getString("deviceType");
	    			b.putString("uuId",uuId);
	    			b.putString("deviceType",deviceType);
	        		i = new Intent(context, AnyCareJpushMapActivity.class);
	        		i.putExtras(b);
	        	}
	        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	context.startActivity(i);
	        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
	        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
	        } else {
	        	Toast.makeText(context, "未知信息", Toast.LENGTH_SHORT).show();
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

