package com.fuer.anycare.set.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.fuer.main.anycare.R;

public class AnyCareAlterPasswordActivity extends Activity{
	private EditText mimaET;
	private EditText newmimaET;
	private EditText newmimarepeatET;
	private Button quedingBtn;
	private ImageView tu;
	private LoadingProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_alter);
        
        mimaET = (EditText)findViewById(R.id.mima);
        newmimaET = (EditText)findViewById(R.id.newmima);
        newmimarepeatET = (EditText)findViewById(R.id.newmimarepeat);
        quedingBtn  = (Button)findViewById(R.id.queding);
        tu = (ImageView) findViewById(R.id.tu);
		tu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
        quedingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String mima = mimaET.getText().toString().trim();
				String newmima = newmimaET.getText().toString().trim();
				String newmimarepeat = newmimarepeatET.getText().toString().trim();
				if(!TextUtils.isEmpty(mima)&&!TextUtils.isEmpty(newmima)&&!TextUtils.isEmpty(newmimarepeat)){
					if(newmima.equals(newmimarepeat)){
						new UserAlterAsyncTask().execute(new String[]{mima,newmima});
					}else{
						Toast.makeText(getApplicationContext(), "新密码、确认密码不一致！", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "原始密码、新密码、确认密码都不能为空！", Toast.LENGTH_SHORT).show();
				}
			}
		});
        //初始化dialog
       dialog=new LoadingProgressDialog(this,"正在加载...");
       //初始化dialog end
	}
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class UserAlterAsyncTask extends AsyncTask<String, String, String>{
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
				MyApplication myApplication = (MyApplication) getApplication();
				userInforMap.put("userId", readUserId());
				userInforMap.put("userPassword", params[0]);
				userInforMap.put("userNewPassword", params[1]);
				String jsonResult = HttpUtils.doPost("/anyCare/userAlterPassword.action", userInforMap);
				if(jsonResult!=null&&!"".equals(jsonResult)){
					return jsonResult;
				}else{
					return "";
				}
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
			if("true".equals(result)){
				Toast.makeText(getApplicationContext(), "密码修改成功！", Toast.LENGTH_SHORT).show();
			}else if("false".equals(result)){
				Toast.makeText(getApplicationContext(), "密码修改失败，请重新修改！", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
	private String readUserId(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String userId=sp.getString("userId", "");
		return userId;
	}
	
}

