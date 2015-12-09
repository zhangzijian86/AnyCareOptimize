package com.fuer.anycare.archives.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.main.activity.AnyCareMainActivity;
import com.fuer.main.anycare.R;

public class ArchivesShowActivity extends Activity {
	 
	private ImageView gerenxinxitu;
	 private ImageView gerenxinxiduigou;
	 private String archivesId;
	 private LoadingProgressDialog dialog;
	 private TextView xingmingTV;
	 private TextView nichengTV;
	 private TextView xingbieTV;
	 private TextView shengriTV;
	 private TextView shengaoTV;
	 private TextView tizhongTV;
	 private TextView fanxiangTV;
	 private TextView medicalhistoryTV[];
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	setContentView(R.layout.layout_addarchshow);
	 	gerenxinxitu  = (ImageView)findViewById(R.id.gerenxinxitu);
	 	gerenxinxiduigou  = (ImageView)findViewById(R.id.gerenxinxiduigou);
	 	gerenxinxitu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {				
				finish();
			}
		});
	 	gerenxinxiduigou.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {		
				startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});
	 	//初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
	 	xingmingTV= (TextView)findViewById(R.id.xingming);
	 	nichengTV= (TextView)findViewById(R.id.nicheng);
	 	xingbieTV= (TextView)findViewById(R.id.xingbie);
	 	shengriTV= (TextView)findViewById(R.id.shengri);
	 	shengaoTV= (TextView)findViewById(R.id.shengao);
	 	tizhongTV= (TextView)findViewById(R.id.tizhong);
	 	fanxiangTV= (TextView)findViewById(R.id.fanxiang);
	 	medicalhistoryTV = new TextView[12];
	 	medicalhistoryTV[0]= (TextView)findViewById(R.id.medicalhistory1);
	 	medicalhistoryTV[1]= (TextView)findViewById(R.id.medicalhistory2);
	 	medicalhistoryTV[2]= (TextView)findViewById(R.id.medicalhistory3);
	 	medicalhistoryTV[3]= (TextView)findViewById(R.id.medicalhistory4);
	 	medicalhistoryTV[4]= (TextView)findViewById(R.id.medicalhistory5);
	 	medicalhistoryTV[5]= (TextView)findViewById(R.id.medicalhistory6);
	 	medicalhistoryTV[6]= (TextView)findViewById(R.id.medicalhistory7);
	 	medicalhistoryTV[7]= (TextView)findViewById(R.id.medicalhistory8);
	 	medicalhistoryTV[8]= (TextView)findViewById(R.id.medicalhistory9);
	 	medicalhistoryTV[9]= (TextView)findViewById(R.id.medicalhistory10);
	 	medicalhistoryTV[10]= (TextView)findViewById(R.id.medicalhistory11);
	 	medicalhistoryTV[11]= (TextView)findViewById(R.id.medicalhistory12);
	 	Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		new UserLoadArchivesAsyncTask().execute(new String[]{archivesId});
	 }
	 
	 /**
		 * dis：AsyncTask参数类型：
		 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
		 * 第二个参数表示进度的刻度
		 * 第三个参数表示返回的结果类型
		 * */
		private class UserLoadArchivesAsyncTask extends AsyncTask<String, String, String>{
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
			
			//数据处理完毕后更新UI操作
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
						String medicalhistory = resultObject.getString("medicalHistory");
						xingmingTV.setText("姓名："+xingming);
						nichengTV.setText("昵称："+nicheng);
						xingbieTV.setText("性别："+xingbie);
						shengriTV.setText("生日："+shengri);
						shengaoTV.setText("身高："+shengao+"cm");
						tizhongTV.setText("体重："+tizhong+"KG");
						if("1".equals(fanxiangSign)){
							fanxiangTV.setText("定位反向查询：开启");
						}else if("1".equals(fanxiangSign)){
							fanxiangTV.setText("定位反向查询：关闭");
						}
						String medical[] = medicalhistory.split(",");
						for(int i=0;i<medical.length;i++){
							medicalhistoryTV[i].setText(medical[i]);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if("".equals(result)){
					Toast.makeText(getApplicationContext(), "个人信息加载失败！", Toast.LENGTH_SHORT).show();
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
