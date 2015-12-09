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
	 	//��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
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
						String medicalhistory = resultObject.getString("medicalHistory");
						xingmingTV.setText("������"+xingming);
						nichengTV.setText("�ǳƣ�"+nicheng);
						xingbieTV.setText("�Ա�"+xingbie);
						shengriTV.setText("���գ�"+shengri);
						shengaoTV.setText("��ߣ�"+shengao+"cm");
						tizhongTV.setText("���أ�"+tizhong+"KG");
						if("1".equals(fanxiangSign)){
							fanxiangTV.setText("��λ�����ѯ������");
						}else if("1".equals(fanxiangSign)){
							fanxiangTV.setText("��λ�����ѯ���ر�");
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
					Toast.makeText(getApplicationContext(), "������Ϣ����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
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
