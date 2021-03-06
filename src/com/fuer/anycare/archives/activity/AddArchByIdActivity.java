package com.fuer.anycare.archives.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuer.anycare.common.application.MyApplication;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.main.activity.AnyCareMainActivity;
import com.fuer.main.anycare.R;

public class AddArchByIdActivity extends Activity {
	private ImageView tianjianguanaitu;
	private Button meiyouid;
	private Button bangding;
	private EditText shuruid;
	private LoadingProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_addarchbyid);
        tianjianguanaitu  = (ImageView)findViewById(R.id.tianjianguanaitu);
        meiyouid = (Button)findViewById(R.id.meiyouid);
        bangding = (Button)findViewById(R.id.bangding);
        shuruid = (EditText)findViewById(R.id.shuruid);
        tianjianguanaitu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {	
				startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				AddArchByIdActivity.this.finish();
			}
		});
        meiyouid.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				Bundle bundle = new Bundle();
				bundle.putString("archivesId","");
				Intent intent = new Intent(AddArchByIdActivity.this,AddArchivesFirstActivity.class);
				intent.putExtras(bundle);
				startActivity(intent); 
			}
		});
        //根据ID号绑定设备信息
        bangding.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {	
				String userId = readUserId();
				String id = shuruid.getText().toString().trim();
				if(!TextUtils.isEmpty(id)){
					new UserBindArchivesByIdAsyncTask().execute(new String[]{userId,id});
				}else{
					Toast.makeText(getApplicationContext(), "ID不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
        //初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
	}
	
	private String readUserId(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String userId=sp.getString("userId", "");
		return userId;
	}
	
	private String readUserPhone(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String phoneNumber=sp.getString("phoneNumber", "");
		return phoneNumber;
	}
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class UserBindArchivesByIdAsyncTask extends AsyncTask<String, String, String>{
		//任务执行之前的操作
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();//显示dialog，数据正在处理....
		}
		//完成耗时操作
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{
				Map<String,String> userInforMap = new HashMap<String,String>();
				userInforMap.put("userId", params[0]);
				userInforMap.put("archivesCode", params[1]);
				String jsonResult = HttpUtils.doPost("/anyCare/createArchivesByArchivesCode.action", userInforMap);
				return jsonResult;
			}catch(Exception e){
				e.printStackTrace();
				return "";
			}
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
		}
		
		//数据处理完毕后更新UI操作
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null&&!"".equals(result)){
				if(result.length()==32){
					Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}else if("noarchives".equals(result)){
					Toast.makeText(getApplicationContext(), "无此ID信息", Toast.LENGTH_SHORT).show();
				}
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "数据绑定失败！", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	/** 
	 * author:ma_yming
	 * dis:显示dialog 
	 * */
	private void dialog() {
		startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		AddArchByIdActivity.this.finish();
	}
	
}

