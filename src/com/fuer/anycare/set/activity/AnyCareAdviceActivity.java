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
					Toast.makeText(getApplicationContext(), "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
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
	private class UserAdviceAsyncTask extends AsyncTask<String, String, String>{
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
		
		//���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if("1".equals(result)){
				Toast.makeText(getApplicationContext(), "�����ύ�ɹ���", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getApplicationContext(), "�����ύʧ�ܣ�������", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
}
