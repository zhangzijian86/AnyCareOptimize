package com.fuer.anycare.register.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.login.activity.AnyCareLoginActivity;
import com.fuer.main.anycare.R;

public class AnyCareRegister2Activity extends Activity {

	private Button btqueding;
	private EditText mima;
	private EditText mimaqueren;
	private ImageView tu;
	private LoadingProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_register2);
		btqueding = (Button) findViewById(R.id.queding);
		btqueding.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String mimaStr = mima.getText().toString().trim();
				String mimaquerenStr = mimaqueren.getText().toString().trim();
				if(!"".equals(mimaStr)&&!"".equals(mimaquerenStr)&&mimaStr.equals(mimaquerenStr)){
					String userId = readUserId();
					new UserRegisterAsyncTask().execute(new String[]{userId,mimaStr});
				}else{
					Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mima = (EditText) findViewById(R.id.mima);
		mimaqueren = (EditText) findViewById(R.id.mimaqueren);
		tu = (ImageView) findViewById(R.id.tu);
		tu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		mima.addTextChangedListener(textWatcher);
		mimaqueren.addTextChangedListener(textWatcher);
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
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class UserRegisterAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("userPassword", params[1]);
				String jsonResult = HttpUtils.doPost("/anyCare/userRegisterPassword.action", userInforMap);
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
				Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getApplication(), AnyCareLoginActivity.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}else if("false".equals(result)){
				Toast.makeText(getApplicationContext(), "注册失败！", Toast.LENGTH_SHORT).show();
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "通信故障！", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
}
