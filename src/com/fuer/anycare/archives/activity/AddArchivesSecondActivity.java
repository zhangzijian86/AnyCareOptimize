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
					medicalHistory.append("����Ѫ�ܲ�");
				}
				if(jingzhuibingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��׵��");
					}else{
						medicalHistory.append(",��׵��");
					}
				}
				if(feiyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("����");
					}else{
						medicalHistory.append(",����");
					}
				}
				if(ganbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("�β�");
					}else{
						medicalHistory.append(",�β�");
					}
				}
				if(yaozhuibingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��׵��");
					}else{
						medicalHistory.append(",��׵��");
					}
				}
				if(tangniaobingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("����");
					}else{
						medicalHistory.append(",����");
					}
				}
				if(guzhishusongSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��������");
					}else{
						medicalHistory.append(",��������");
					}
				}
				if(jingmaiquzhangSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��������");
					}else{
						medicalHistory.append(",��������");
					}
				}
				if(laonianchidaiSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("����մ�");
					}else{
						medicalHistory.append(",����մ�");
					}
				}
				if(jianzhouyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("������");
					}else{
						medicalHistory.append(",������");
					}
				}
				if(gaoxueyaSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��Ѫѹ");
					}else{
						medicalHistory.append(",��Ѫѹ");
					}
				}
				if(manxingwenyanSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("����θ��");
					}else{
						medicalHistory.append(",����θ��");
					}
				}
				if(shenbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("����");
					}else{
						medicalHistory.append(",����");
					}
				}
				if(gaoxuezhiSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("��Ѫ֬");
					}else{
						medicalHistory.append(",��Ѫ֬");
					}
				}
				if(tongfengbingSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("ʹ�粡");
					}else{
						medicalHistory.append(",ʹ�粡");
					}
				}
				if(guanjieyaSign==true){
					if(medicalHistory.length()==0){
						medicalHistory.append("�ؽ���");
					}else{
						medicalHistory.append(",�ؽ���");
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
	 	
	 	//��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
		
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
		 * dis��AsyncTask�������ͣ�
		 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
		 * �ڶ���������ʾ���ȵĿ̶�
		 * ������������ʾ���صĽ������
		 * */
		private class ArchivesUpdateMedicalHistoryAsyncTask extends AsyncTask<String, String, String>{
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
			
			//���ݴ�����Ϻ����UI����
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result!=null&&!"".equals(result)&&result.length()==32){
					Toast.makeText(getApplicationContext(), "������Ϣ���³ɹ�", Toast.LENGTH_SHORT).show();
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",result);
					startActivity(new Intent(getApplication(), ArchivesShowActivity.class).putExtras(bundle));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}else{
					Toast.makeText(getApplicationContext(), "������Ϣ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();//dialog�رգ����ݴ������
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
						//����Ѿ�ѡ�У����ѡ�а�ť,ȡ��ѡ�в��������ɫ
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
						String medicalHistory = resultObject.getString("medicalHistory");
						String createSign = resultObject.getString("createSign");
						if(medicalHistory!=null&&!"".equals(medicalHistory)){
							String sicks[] = medicalHistory.split(",");
							for(int i = 0;i<sicks.length;i++){
								if("����Ѫ�ܲ�".equals(sicks[i])){
									xinnaoxueguanbingSign =true;
									xinnaoxueguanbingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("��׵��".equals(sicks[i])){
									jingzhuibingSign=true;
									jingzhuibingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("����".equals(sicks[i])){
									feiyanSign=true;
									feiyanBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("�β�".equals(sicks[i])){
									ganbingSign=true;
									ganbingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("��׵��".equals(sicks[i])){
									yaozhuibingSign=true;
									yaozhuibingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("����".equals(sicks[i])){
									tangniaobingSign=true;
									tangniaobingBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("��������".equals(sicks[i])){
									guzhishusongSign=true;
									guzhishusongBtn.setBackgroundResource(R.drawable.zuohong);
								}else if("��������".equals(sicks[i])){
									jingmaiquzhangSign=true;
									jingmaiquzhangBtn.setBackgroundResource(R.drawable.youhong);
								}else if("����մ�".equals(sicks[i])){
									laonianchidaiSign=true;
									laonianchidaiBtn.setBackgroundResource(R.drawable.youhong);
								}else if("����θ��".equals(sicks[i])){
									manxingwenyanSign=true;
									manxingwenyanBtn.setBackgroundResource(R.drawable.youhong);
								}else if("������".equals(sicks[i])){
									jianzhouyanSign=true;
									jianzhouyanBtn.setBackgroundResource(R.drawable.youhong);
								}else if("��Ѫѹ".equals(sicks[i])){
									gaoxueyaSign=true;
									gaoxueyaBtn.setBackgroundResource(R.drawable.youhong);
								}else if("����".equals(sicks[i])){
									shenbingSign=true;
									shenbingBtn.setBackgroundResource(R.drawable.youhong);
								}else if("��Ѫ֬".equals(sicks[i])){
									gaoxuezhiSign=true;
									gaoxuezhiBtn.setBackgroundResource(R.drawable.youhong);
								}else if("ʹ�粡".equals(sicks[i])){
									tongfengbingSign=true;
									tongfengbingBtn.setBackgroundResource(R.drawable.youhong);
								}else if("�ؽ���".equals(sicks[i])){
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
					Toast.makeText(getApplicationContext(), "������Ϣ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		private String readUserId(){
			SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
			//��û�����ݣ�����Ĭ��ֵ""
			String userId=sp.getString("userId", "");
			return userId;
		}
}
