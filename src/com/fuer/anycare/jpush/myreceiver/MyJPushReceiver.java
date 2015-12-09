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
			//SDK �� JPush Server ע�����õ���ע�� ȫ��Ψһ�� ID ������ͨ���� ID ���Ӧ�Ŀͻ��˷�����Ϣ��֪ͨ��
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
	            //����ȫ��Ψһ��ID����������������������֤�����ù����Ժ�����ӣ�
	        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//���յ������������Զ�����Ϣ
	        	Log.d("MyJPush", "[MyJPushReceiver] ���յ������������Զ�����Ϣ: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
	        	//processCustomMessage(context, bundle);
	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {//���յ�����������֪ͨ
	            Log.d("MyJPush", "[MyJPushReceiver] ���յ�����������֪ͨ");
	            notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
	            Log.d("MyJPush", "[MyJPushReceiver] ���յ�����������֪ͨ��ID: " + notifactionId);
	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//�û��������֪ͨ
	        	//���Զ����Activity
	        	String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	        	Log.d("MyJPush", "[MyJPushReceiver] ���յ�����������֪ͨ������: " + extras);
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
	        	Toast.makeText(context, "δ֪��Ϣ", Toast.LENGTH_SHORT).show();
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
