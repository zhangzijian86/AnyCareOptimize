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
						Toast.makeText(getApplicationContext(), "�����롢ȷ�����벻һ�£�", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "ԭʼ���롢�����롢ȷ�����붼����Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
        //��ʼ��dialog
       dialog=new LoadingProgressDialog(this,"���ڼ���...");
       //��ʼ��dialog end
	}
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class UserAlterAsyncTask extends AsyncTask<String, String, String>{
		//����ִ��֮ǰ�Ĳ���
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();//��ʾdialog���������ڴ���....
		}
		//��ɺ�ʱ����
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
		
		//���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if("true".equals(result)){
				Toast.makeText(getApplicationContext(), "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
			}else if("false".equals(result)){
				Toast.makeText(getApplicationContext(), "�����޸�ʧ�ܣ��������޸ģ�", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	private String readUserId(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��û�����ݣ�����Ĭ��ֵ""
		String userId=sp.getString("userId", "");
		return userId;
	}
	
}
