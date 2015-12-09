package com.fuer.anycare.set.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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

public class AnyCareAdviceActivity extends Activity{
	
	private EditText adviceET;
	private Button quedingBtn;
	private ImageView tu;
	private LoadingProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_advice);
        adviceET = (EditText)findViewById(R.id.advice);
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
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String advice = adviceET.getText().toString().trim();
				if(!TextUtils.isEmpty(advice)){
					new UserAdviceAsyncTask().execute(new String[]{advice});
				}else{
					Toast.makeText(getApplicationContext(), "建议内容不能为空！", Toast.LENGTH_SHORT).show();
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
	private class UserAdviceAsyncTask extends AsyncTask<String, String, String>{
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
				String jsonResult = HttpUtils.doPost("/anyCare/adviceRequest.action", userInforMap);
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
			if("1".equals(result)){
				Toast.makeText(getApplicationContext(), "建议提交成功！", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "建议提交失败，请重试", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
}

