package com.fuer.anycare.archives.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuer.anycare.common.tool.DateTool;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;

public class AddArchivesFirstActivity extends Activity {
	 private ImageView gerenxinxiitu;
	 private ImageView gerenduigou;
	 private LoadingProgressDialog dialog;
	 
	 private EditText xingmingEdt;
	 private EditText nichengEdt;
	 private Button nanBtn;
	 private Button nvBtn;
	 private EditText shengriEdt;
	 private EditText shengaoEdt;
	 private EditText tizhongEdt;
	 private Button shengriBtn;
	 private Button kaiqiBtn;
	 private Button guanbiBtn;
	 
	 private String sex="��";
	 private String fanxiang="1";
	 
	 private String archivesId;
	 
	 private int year;
	 private int mouth;
	 private int day;
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	setContentView(R.layout.layout_addarchfirst);
	 	gerenxinxiitu  = (ImageView)findViewById(R.id.gerenxinxiitu);
	 	gerenduigou  = (ImageView)findViewById(R.id.gerenduigou);
	 	//��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
		xingmingEdt = (EditText)findViewById(R.id.xingming);
		nichengEdt = (EditText)findViewById(R.id.nicheng);
		shengriEdt = (EditText)findViewById(R.id.shengri);
		shengriBtn = (Button)findViewById(R.id.shengriselect);
		shengaoEdt = (EditText)findViewById(R.id.shengao);
		tizhongEdt = (EditText)findViewById(R.id.tizhong);
		nanBtn = (Button)findViewById(R.id.nan);
		nvBtn  = (Button)findViewById(R.id.nv);
		kaiqiBtn = (Button)findViewById(R.id.kaiqi);
		guanbiBtn = (Button)findViewById(R.id.guanbi);
	 	gerenxinxiitu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {				
				finish();
			}
		});
	 	gerenduigou.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String userId = readUserId();
				String xingming = xingmingEdt.getText().toString().trim();
				String nicheng = nichengEdt.getText().toString().trim();
				String shengri = shengriEdt.getText().toString().trim();
				String shengao = shengaoEdt.getText().toString().trim();
				String tizhong = tizhongEdt.getText().toString().trim();
				if(TextUtils.isEmpty(xingming)){
					Toast.makeText(getApplicationContext(), "��������Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(nicheng)){
					Toast.makeText(getApplicationContext(), "�ǳƲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(shengri)){
					Toast.makeText(getApplicationContext(), "���ղ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(shengao)){
					Toast.makeText(getApplicationContext(), "��߲���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}else if(TextUtils.isEmpty(tizhong)){
					Toast.makeText(getApplicationContext(), "���ز���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				}else{
					new UserCreateArchivesAsyncTask().execute(new String[]{userId,xingming,nicheng,sex,shengao,tizhong,shengri,fanxiang});
				}
			}
		});
	 	shengriBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showDialog(R.id.shengriselect);
			}
		});
	 	nanBtn.setOnClickListener(new MyOnClickListener());
	 	nvBtn.setOnClickListener(new MyOnClickListener());
	 	kaiqiBtn.setOnClickListener(new MyOnClickListener());
	 	guanbiBtn.setOnClickListener(new MyOnClickListener());
	 	Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		if(archivesId!=null&!"".equals(archivesId)){
			new UserLoadArchivesAsyncTask().execute(new String[]{archivesId});
		}
		//��ʼ������ start
		year=DateTool.getDateYear();
		mouth=DateTool.getDateMonth();
		day=DateTool.getDateDay();
		//��ʼ��end
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
	private class UserCreateArchivesAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("userId", params[0]);
				userInforMap.put("fullName", params[1]);
				userInforMap.put("nickName", params[2]);
				userInforMap.put("sex", params[3]);
				userInforMap.put("height", params[4]);
				userInforMap.put("weight", params[5]);
				userInforMap.put("birthday", params[6]);
				userInforMap.put("fanZouShiUseAble", params[7]);
				
				String jsonResult = "";
				if(archivesId==null||"".equals(archivesId)){
					jsonResult = HttpUtils.doPost("/anyCare/createArchives.action", userInforMap);
				}else{
					userInforMap.put("archivesId", archivesId);
					jsonResult = HttpUtils.doPost("/anyCare/updateArchivesByArchivesId.action", userInforMap);
				}
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
		
		//���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null&&!"".equals(result)&&result.length()==32){
				if(archivesId==null||"".equals(archivesId)){
					Toast.makeText(getApplicationContext(), "������Ϣ����ɹ�", Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",result);
					bundle.putString("updateSign","0");
					startActivity(new Intent(getApplication(), AddArchivesSecondActivity.class).putExtras(bundle));
				}else{
					Toast.makeText(getApplicationContext(), "������Ϣ���³ɹ�", Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",result);
					bundle.putString("updateSign","1");
					startActivity(new Intent(getApplication(), AddArchivesSecondActivity.class).putExtras(bundle));
				}
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "������Ϣ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	
	
	private class MyOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.nan:
					//����Ѿ�ѡ�У����ѡ�а�ť,ȡ��ѡ�в��������ɫ
					sex = "��";
					nanBtn.setBackgroundResource(R.drawable.zuoselectbutton);
					nvBtn.setBackgroundResource(R.drawable.youunselectbutton);
					break;
				case R.id.nv:
					sex = "Ů";
					nanBtn.setBackgroundResource(R.drawable.zuounselectbutton);
					nvBtn.setBackgroundResource(R.drawable.youselectbutton);
					break;
				case R.id.kaiqi:
					//����Ѿ�ѡ�У����ѡ�а�ť,ȡ��ѡ�в��������ɫ
					fanxiang = "1";
					kaiqiBtn.setBackgroundResource(R.drawable.zuoselectbutton);
					guanbiBtn.setBackgroundResource(R.drawable.youunselectbutton);
					break;
				case R.id.guanbi:
					fanxiang = "0";
					kaiqiBtn.setBackgroundResource(R.drawable.zuounselectbutton);
					guanbiBtn.setBackgroundResource(R.drawable.youselectbutton);
					break;
			}
		}
		
	}
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class UserLoadArchivesAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("archivesId", params[0]);
				userInforMap.put("userId",  readUserId());
				String jsonResult = HttpUtils.doPost("/anyCare/viewArchivesInfor.action", userInforMap);
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
		
		//���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null&&!"".equals(result)){
				try {
					JSONObject resultObject=new JSONObject(result);
					String xingming = resultObject.getString("fullName");
					String nicheng = resultObject.getString("nickName");
					String xingbie = resultObject.getString("sex");
					String shengri = resultObject.getString("birthday");
					String shengao = resultObject.getString("height");
					String tizhong = resultObject.getString("weight");
					String fanxiangSign = resultObject.getString("fanZouShiUseable");
					String createSign = resultObject.getString("createSign");
					xingmingEdt.setText(xingming);
					nichengEdt.setText(nicheng);
					shengriEdt.setText(shengri);
					shengaoEdt.setText(shengao);
					tizhongEdt.setText(tizhong);
					if("��".equals(xingbie)){
						sex = "��";
						nanBtn.setBackgroundResource(R.drawable.zuoselectbutton);
						nvBtn.setBackgroundResource(R.drawable.youunselectbutton);
					}else if("Ů".equals(xingbie)){
						sex = "Ů";
						nanBtn.setBackgroundResource(R.drawable.zuounselectbutton);
						nvBtn.setBackgroundResource(R.drawable.youselectbutton);
					}
					if("1".equals(fanxiangSign)){
						fanxiang = "1";
						kaiqiBtn.setBackgroundResource(R.drawable.zuoselectbutton);
						guanbiBtn.setBackgroundResource(R.drawable.youunselectbutton);
					}else if("0".equals(fanxiangSign)){
						fanxiang = "0";
						kaiqiBtn.setBackgroundResource(R.drawable.zuounselectbutton);
						guanbiBtn.setBackgroundResource(R.drawable.youselectbutton);
					}
					if("0".equals(createSign)){
						xingmingEdt.setEnabled(false);
						shengriEdt.setEnabled(false);
						nanBtn.setEnabled(false);
						nvBtn.setEnabled(false);
						shengriBtn.setEnabled(false);
						shengaoEdt.setEnabled(false);
						tizhongEdt.setEnabled(false);
						kaiqiBtn.setEnabled(false);
						guanbiBtn.setEnabled(false);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "������Ϣ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	
	@Override
	 protected Dialog onCreateDialog(int id) {
		switch (id) {
	  		case R.id.shengriselect:
	  			return new DatePickerDialog(this, onDateSetListener, year, mouth, day);
		}
		return null;
	 }
	
	/**
	 * author:ma_yming
	 * dis:��ʾ���ڶԻ���
	 * */
	private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		  @Override
		  public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			  int month=monthOfYear+1;
			  shengriEdt.setText(year + "." + month + "." + dayOfMonth);
		  }
	 };
}
