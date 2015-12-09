package com.fuer.anycare.archives.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;

public class AddArchivesSecondActivity extends Activity {
	 private ImageView xuanzejibingtu;
	 private ImageView xuanzejibingduigou;
	 
	 private Button xinnaoxueguanbingBtn;
	 private Button jingzhuibingBtn;
	 private Button feiyanBtn;
	 private Button ganbingBtn;
	 private Button yaozhuibingBtn;
	 private Button tangniaobingBtn;
	 private Button guzhishusongBtn;
	 private Button jingmaiquzhangBtn;
	 
	 private Button laonianchidaiBtn;
	 private Button jianzhouyanBtn;
	 private Button gaoxueyaBtn;
	 private Button manxingwenyanBtn;
	 private Button shenbingBtn;
	 private Button gaoxuezhiBtn;
	 private Button tongfengbingBtn;
	 private Button guanjieyaBtn;
	 
	 private LoadingProgressDialog dialog;
	 
	 private String archivesId;
	 private String updateSign;
	 
	 private boolean xinnaoxueguanbingSign=false;
	 private boolean jingzhuibingSign=false;
	 private boolean feiyanSign=false;
	 private boolean ganbingSign=false;
	 private boolean yaozhuibingSign=false;
	 private boolean tangniaobingSign=false;
	 private boolean guzhishusongSign=false;
	 private boolean jingmaiquzhangSign=false;
	 private boolean laonianchidaiSign=false;
	 private boolean jianzhouyanSign=false;
	 private boolean gaoxueyaSign=false;
	 private boolean manxingwenyanSign=false;
	 private boolean shenbingSign=false;
	 private boolean gaoxuezhiSign=false;
	 private boolean tongfengbingSign=false;
	 private boolean guanjieyaSign=false;
	 
	 private List<String> titles;
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	setContentView(R.layout.layout_addarchsecond);
	 	xuanzejibingtu = (ImageView)findViewById(R.id.xuanzejibingtu);
	 	xuanzejibingduigou  = (ImageView)findViewById(R.id.xuanzejibingduigou);
	 	xuanzejibingtu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {		
				finish();
			}
		});
	 	xuanzejibingduigou.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
//				Intent intent = new Intent(AddArchivesSecondActivity.this,ArchivesShowActivity.class);
//				startActivity(intent);
				StringBuffer medicalHistory = new StringBuffer();
				if(xinnaoxueguanbingSign==true){
					medicalHistory.append("心脑血管病");
				}
				if(jingzhuibingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("颈椎病");
					}else{
						medicalHistory.append(",颈椎病");
					}
				}
				if(feiyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("肺炎");
					}else{
						medicalHistory.append(",肺炎");
					}
				}
				if(ganbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("肝病");
					}else{
						medicalHistory.append(",肝病");
					}
				}
				if(yaozhuibingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("腰椎病");
					}else{
						medicalHistory.append(",腰椎病");
					}
				}
				if(tangniaobingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("糖尿病");
					}else{
						medicalHistory.append(",糖尿病");
					}
				}
				if(guzhishusongSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("骨质疏松");
					}else{
						medicalHistory.append(",骨质疏松");
					}
				}
				if(jingmaiquzhangSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("静脉曲张");
					}else{
						medicalHistory.append(",静脉曲张");
					}
				}
				if(laonianchidaiSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("老年痴呆");
					}else{
						medicalHistory.append(",老年痴呆");
					}
				}
				if(jianzhouyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("肩周炎");
					}else{
						medicalHistory.append(",肩周炎");
					}
				}
				if(gaoxueyaSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("高血压");
					}else{
						medicalHistory.append(",高血压");
					}
				}
				if(manxingwenyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("慢性胃炎");
					}else{
						medicalHistory.append(",慢性胃炎");
					}
				}
				if(shenbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("肾病");
					}else{
						medicalHistory.append(",肾病");
					}
				}
				if(gaoxuezhiSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("高血脂");
					}else{
						medicalHistory.append(",高血脂");
					}
				}
				if(tongfengbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("痛风病");
					}else{
						medicalHistory.append(",痛风病");
					}
				}
				if(guanjieyaSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("关节炎");
					}else{
						medicalHistory.append(",关节炎");
					}
				}
				new ArchivesUpdateMedicalHistoryAsyncTask().execute(new String[]{archivesId,medicalHistory.toString()});
			}
		});
		xinnaoxueguanbingBtn = (Button)findViewById(R.id.xinnaoxueguanbing);
		jingzhuibingBtn = (Button)findViewById(R.id.jingzhuibing);
	 	feiyanBtn = (Button)findViewById(R.id.feiyan);
	 	ganbingBtn = (Button)findViewById(R.id.ganbing);
	 	yaozhuibingBtn = (Button)findViewById(R.id.yaozhuibing);
	 	tangniaobingBtn = (Button)findViewById(R.id.tangniaobing);
	 	guzhishusongBtn = (Button)findViewById(R.id.guzhishusong);
	 	jingmaiquzhangBtn = (Button)findViewById(R.id.jingmaiquzhang);
	 	
	 	laonianchidaiBtn = (Button)findViewById(R.id.laonianchidai);
	 	jianzhouyanBtn = (Button)findViewById(R.id.jianzhouyan);
	 	gaoxueyaBtn = (Button)findViewById(R.id.gaoxueya);
	 	manxingwenyanBtn = (Button)findViewById(R.id.manxingwenyan);
	 	shenbingBtn = (Button)findViewById(R.id.shenbing);
	 	gaoxuezhiBtn = (Button)findViewById(R.id.gaoxuezhi);
	 	tongfengbingBtn = (Button)findViewById(R.id.tongfengbing);
	 	guanjieyaBtn = (Button)findViewById(R.id.guanjieya);
	 	
	 	//初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
		
		 xinnaoxueguanbingBtn.setOnClickListener(new MyOnClickListener());
		 jingzhuibingBtn.setOnClickListener(new MyOnClickListener());
		 feiyanBtn.setOnClickListener(new MyOnClickListener());
		 ganbingBtn.setOnClickListener(new MyOnClickListener());
		 yaozhuibingBtn.setOnClickListener(new MyOnClickListener());
		 tangniaobingBtn.setOnClickListener(new MyOnClickListener());
		 guzhishusongBtn.setOnClickListener(new MyOnClickListener());
		 jingmaiquzhangBtn.setOnClickListener(new MyOnClickListener());
		 laonianchidaiBtn.setOnClickListener(new MyOnClickListener());
		 jianzhouyanBtn.setOnClickListener(new MyOnClickListener());
		 gaoxueyaBtn.setOnClickListener(new MyOnClickListener());
		 manxingwenyanBtn.setOnClickListener(new MyOnClickListener());
		 shenbingBtn.setOnClickListener(new MyOnClickListener());
		 gaoxuezhiBtn.setOnClickListener(new MyOnClickListener());
		 tongfengbingBtn.setOnClickListener(new MyOnClickListener());
		 guanjieyaBtn.setOnClickListener(new MyOnClickListener());
		Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		updateSign = bundle.getString("updateSign");
		new UserLoadArchivesAsyncTask().execute(new String[]{archivesId});
	 }
	 /**
		 * dis：AsyncTask参数类型：
		 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
		 * 第二个参数表示进度的刻度
		 * 第三个参数表示返回的结果类型
		 * */
		private class ArchivesUpdateMedicalHistoryAsyncTask extends AsyncTask<String, String, String>{
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
					userInforMap.put("medicalHistory", params[1]);
					userInforMap.put("userId",  readUserId());
					String jsonResult = "";
					if("0".equals(updateSign)){
						jsonResult = HttpUtils.doPost("/anyCare/updateArchivesMedicalHistory.action", userInforMap);
					}else if("1".equals(updateSign)){
						jsonResult = HttpUtils.doPost("/anyCare/updateArchivesMedicalHistoryByArchivesId.action", userInforMap);
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
			
			//数据处理完毕后更新UI操作
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result!=null&&!"".equals(result)&&result.length()==32){
					Toast.makeText(getApplicationContext(), "个人信息更新成功", Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",result);
					startActivity(new Intent(getApplication(), ArchivesShowActivity.class).putExtras(bundle));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}else{
					Toast.makeText(getApplicationContext(), "个人信息更新失败！", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();//dialog关闭，数据处理完毕
			}
		}
		
		private class MyOnClickListener implements OnClickListener{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(titles==null){
					titles=new ArrayList<String>();
				}
				
				switch (v.getId()) {
					case R.id.xinnaoxueguanbing:
						//如果已经选中，点击选中按钮,取消选中操作，变白色
						if(xinnaoxueguanbingSign==true){
							xinnaoxueguanbingSign =false;
							xinnaoxueguanbingBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							xinnaoxueguanbingSign =true;
							xinnaoxueguanbingBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.jingzhuibing:
						if(jingzhuibingSign==true){
							jingzhuibingSign=false;
							jingzhuibingBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							jingzhuibingSign=true;
							jingzhuibingBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.feiyan:
						if(feiyanSign==true){
							feiyanSign=false;
							feiyanBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							feiyanSign=true;
							feiyanBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;	
					case R.id.ganbing:
						if(ganbingSign==true){
							ganbingSign=false;
							ganbingBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							ganbingSign=true;
							ganbingBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.yaozhuibing:
						if(yaozhuibingSign==true){
							yaozhuibingSign=false;
							yaozhuibingBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							yaozhuibingSign=true;
							yaozhuibingBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.tangniaobing:
						if(tangniaobingSign==true){
							tangniaobingSign=false;
							tangniaobingBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							tangniaobingSign=true;
							tangniaobingBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.guzhishusong:
						if(guzhishusongSign==true){
							guzhishusongSign=false;
							guzhishusongBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							guzhishusongSign=true;
							guzhishusongBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;	
					case R.id.jingmaiquzhang:
						if(jingmaiquzhangSign==true){
							jingmaiquzhangSign=false;
							jingmaiquzhangBtn.setBackgroundResource(R.drawable.zuobai);
						}else{
							jingmaiquzhangSign=true;
							jingmaiquzhangBtn.setBackgroundResource(R.drawable.zuohong);
						}
						break;
					case R.id.laonianchidai:
						if(laonianchidaiSign==true){
							laonianchidaiSign=false;
							laonianchidaiBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							laonianchidaiSign=true;
							laonianchidaiBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;
					case R.id.jianzhouyan:
						if(jianzhouyanSign==true){
							jianzhouyanSign=false;
							jianzhouyanBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							jianzhouyanSign=true;
							jianzhouyanBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;
					case R.id.gaoxueya:
						if(gaoxueyaSign==true){
							gaoxueyaSign=false;
							gaoxueyaBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							gaoxueyaSign=true;
							gaoxueyaBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;	
					case R.id.manxingwenyan:
						if(manxingwenyanSign==true){
							manxingwenyanSign=false;
							manxingwenyanBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							manxingwenyanSign=true;
							manxingwenyanBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;	
					case R.id.shenbing:
						if(shenbingSign==true){
							shenbingSign=false;
							shenbingBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							shenbingSign=true;
							shenbingBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;	
					case R.id.gaoxuezhi:
						if(gaoxuezhiSign==true){
							gaoxuezhiSign=false;
							gaoxuezhiBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							gaoxuezhiSign=true;
							gaoxuezhiBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;
					case R.id.tongfengbing:
						if(tongfengbingSign==true){
							tongfengbingSign=false;
							tongfengbingBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							tongfengbingSign=true;
							tongfengbingBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;
					case R.id.guanjieya:
						if(guanjieyaSign==true){
							guanjieyaSign=false;
							guanjieyaBtn.setBackgroundResource(R.drawable.youbai);
						}else{
							guanjieyaSign=true;
							guanjieyaBtn.setBackgroundResource(R.drawable.youhong);
						}
						break;
				}
			}
			
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
						String medicalHistory = resultObject.getString("medicalHistory");
						String createSign = resultObject.getString("createSign");
						if(medicalHistory!=null&&!"".equals(medicalHistory)){
							String sicks[] = medicalHistory.split(",");
							for(int i = 0;i<sicks.length;i++){
								if("心脑血管病".equals(sicks[i])){
									xinnaoxueguanbingSign =true;
									xinnaoxueguanbingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("颈椎病".equals(sicks[i])){
									jingzhuibingSign=true;
									jingzhuibingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("肺炎".equals(sicks[i])){
									feiyanSign=true;
									feiyanBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("肝病".equals(sicks[i])){
									ganbingSign=true;
									ganbingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("腰椎病".equals(sicks[i])){
									yaozhuibingSign=true;
									yaozhuibingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("糖尿病".equals(sicks[i])){
									tangniaobingSign=true;
									tangniaobingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("骨质疏松".equals(sicks[i])){
									guzhishusongSign=true;
									guzhishusongBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("静脉曲张".equals(sicks[i])){
									jingmaiquzhangSign=true;
									jingmaiquzhangBtn.setBackgroundResource(R.drawable.youhong);
								}else if("老年痴呆".equals(sicks[i])){
									laonianchidaiSign=true;
									laonianchidaiBtn.setBackgroundResource(R.drawable.youhong);
								}else if("慢性胃炎".equals(sicks[i])){
									manxingwenyanSign=true;
									manxingwenyanBtn.setBackgroundResource(R.drawable.youhong);
								}else if("肩周炎".equals(sicks[i])){
									jianzhouyanSign=true;
									jianzhouyanBtn.setBackgroundResource(R.drawable.youhong);
								}else if("高血压".equals(sicks[i])){
									gaoxueyaSign=true;
									gaoxueyaBtn.setBackgroundResource(R.drawable.youhong);
								}else if("肾病".equals(sicks[i])){
									shenbingSign=true;
									shenbingBtn.setBackgroundResource(R.drawable.youhong);
								}else if("高血脂".equals(sicks[i])){
									gaoxuezhiSign=true;
									gaoxuezhiBtn.setBackgroundResource(R.drawable.youhong);
								}else if("痛风病".equals(sicks[i])){
									tongfengbingSign=true;
									tongfengbingBtn.setBackgroundResource(R.drawable.youhong);
								}else if("关节炎".equals(sicks[i])){
									guanjieyaSign=true;
									guanjieyaBtn.setBackgroundResource(R.drawable.youhong);
								}
							}
						}
						if("0".equals(createSign)){
							xinnaoxueguanbingBtn.setEnabled(false);
							jingzhuibingBtn.setEnabled(false);
							feiyanBtn.setEnabled(false);
							ganbingBtn.setEnabled(false);
							yaozhuibingBtn.setEnabled(false);
							tangniaobingBtn.setEnabled(false);
							guzhishusongBtn.setEnabled(false);
							jingmaiquzhangBtn.setEnabled(false);
							laonianchidaiBtn.setEnabled(false);
							manxingwenyanBtn.setEnabled(false);
							jianzhouyanBtn.setEnabled(false);
							gaoxueyaBtn.setEnabled(false);
							shenbingBtn.setEnabled(false);
							gaoxuezhiBtn.setEnabled(false);
							tongfengbingBtn.setEnabled(false);
							guanjieyaBtn.setEnabled(false);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if("".equals(result)){
					Toast.makeText(getApplicationContext(), "个人信息加载失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		private String readUserId(){
			SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
			//若没有数据，返回默认值""
			String userId=sp.getString("userId", "");
			return userId;
		}
}

