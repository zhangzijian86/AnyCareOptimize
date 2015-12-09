package com.fuer.anycare.register.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;

public class AnyCareRegister1Activity extends Activity {

	private Button btqueding;
	private Button yanzhengmaBtn;
	private EditText shoujihaoma;
	private EditText yanzhengma;
	private ImageView tu;
	private LoadingProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_register1);
		btqueding = (Button) findViewById(R.id.queding);
		yanzhengmaBtn = (Button) findViewById(R.id.yanzhengmaBtn);
		btqueding.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String phoneNumber = shoujihaoma.getText().toString().trim();
				String yanZhengMa = yanzhengma.getText().toString().trim();
				if(!TextUtils.isEmpty(phoneNumber)&&!TextUtils.isEmpty(yanZhengMa)){
					new UserRegisterAsyncTask().execute(new String[]{phoneNumber,yanZhengMa});
				}else{
					Toast.makeText(getApplicationContext(), "�ֻ��Ż�����֤�벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		yanzhengmaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String phoneNumber = shoujihaoma.getText().toString().trim();
				new UserRegisterYanZhengMaAsyncTask().execute(new String[]{phoneNumber});
			}
		});
		
		shoujihaoma = (EditText) findViewById(R.id.shoujihaoma);
		yanzhengma = (EditText) findViewById(R.id.yanzhengma);
		tu = (ImageView) findViewById(R.id.tu);
		tu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
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
	private class UserRegisterAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("phoneNumber", params[0]);
				userInforMap.put("yanZhengCode", params[1]);
				String jsonResult = HttpUtils.doPost("/anyCare/userRegister.action", userInforMap);
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
			if(result.length()==32){
				Toast.makeText(getApplicationContext(), "ע��ɹ���", Toast.LENGTH_SHORT).show();
				saveUserInfor(result);
				startActivity(new Intent(getApplication(), AnyCareRegister2Activity.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}else if("0".equals(result)){
				Toast.makeText(getApplicationContext(), "�ֻ�����ע�ᣡ", Toast.LENGTH_SHORT).show();
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "ע��ʧ�ܣ�������ע�ᣡ", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	
	private void saveUserInfor(String userId){
		//��ȡSharedPreferences����·����/data/data/cn.itcast.preferences/shared_pref/paramater.xml
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��ȡ�༭��
		Editor editor=sp.edit();
		//ͨ��editor��������
		editor.putString("userId", userId);
		//�ύ�޸ģ�������д���ļ�
		editor.commit();
	}
	
	private String readUserId(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��û�����ݣ�����Ĭ��ֵ""
		String userId=sp.getString("userId", "");
		return userId;
	}
	
	private String readUserPhone(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��û�����ݣ�����Ĭ��ֵ""
		String phoneNumber=sp.getString("phoneNumber", "");
		return phoneNumber;
	}
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class UserRegisterYanZhengMaAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("phoneNumber", params[0]);
				String jsonResult = HttpUtils.doPost("/anyCare/userCaptchaByPhone.action", userInforMap);
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
				Toast.makeText(getApplicationContext(), "����ɹ����ȴ�������֤�뷢�ͣ�", Toast.LENGTH_SHORT).show();
			}else if("repeatcommit".equals(result)){
				Toast.makeText(getApplicationContext(), "5����֮�ڲ����ظ���ȡ��֤�룡", Toast.LENGTH_SHORT).show();
			}else if("false".equals(result)){
				Toast.makeText(getApplicationContext(), "��֤������ʧ�ܣ�����������", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
}
