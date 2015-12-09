package com.fuer.anycare.bind.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;
import com.zbar.lib.CaptureActivity;

public class AnyCareBindActivity extends Activity {
	 
	 private ImageView bangdingshebeitu;
	 private ImageView tianjiatubiao;
	 private List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();//存放数据的List
	 private String archivesId;
	 private String createSign;
	 private LoadingProgressDialog dialog;
	 private MyAdapter myAdapter;
	 private ListView bind_listview;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 	super.onCreate(savedInstanceState);
	 	requestWindowFeature(Window.FEATURE_NO_TITLE);
	 	setContentView(R.layout.layout_bind);	 
	 	bangdingshebeitu  = (ImageView)findViewById(R.id.bangdingshebeitu);
	 	tianjiatubiao  = (ImageView)findViewById(R.id.tianjiatubiao);
	 	bangdingshebeitu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {				
				finish();
			}
		});
	 	tianjiatubiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if("1".equals(createSign)){
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",archivesId);
					Intent intent = new Intent(AnyCareBindActivity.this,CaptureActivity.class);
					intent.putExtras(bundle);
					startActivity(intent); 
					AnyCareBindActivity.this.finish();
				}else if("0".equals(createSign)){
					Toast.makeText(getApplicationContext(), "无权限增加设备", Toast.LENGTH_SHORT).show();
				}
			}
		});
	 	Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		createSign = bundle.getString("createSign");
		//初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
		bind_listview =  (ListView) findViewById(R.id.bind_listview);
		if(!"".equals(archivesId)){
			new UserLoadDeviceByArchIdAsyncTask().execute(new String[]{archivesId});
		}else{
			Toast.makeText(getApplicationContext(), "加载错误！", Toast.LENGTH_SHORT).show();
		}
	 }
	 
	 /**
	 * dis:自定义adapter，实现listView 按钮事件
	 * */
	private class MyAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// 这里我就返回10了，也就是一共有10项数据项
			return dataList.size();
		}

		@Override
		public Object getItem(int view) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bind_list_style,null);
			ImageView shebeitupian = (ImageView)convertView.findViewById(R.id.shebeitupian);
			ImageView dianliangImage  = (ImageView)convertView.findViewById(R.id.dianliang_imageview);
			TextView binddevicename= (TextView)convertView.findViewById(R.id.binddevicename_textview);
			TextView devicenumber_textview= (TextView)convertView.findViewById(R.id.devicenumber_textview);
			Button delete_button=(Button)convertView.findViewById(R.id.delete_button);
			final Map<String,String> map=(Map<String,String>)dataList.get(position);
			String deviceType = map.get("deviceType");
			int dianLiang = Integer.parseInt(map.get("dianLiang"));
			if(dianLiang>80){
				dianliangImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.fullelectricity));
			}else if(dianLiang>40&&dianLiang<=80){
				dianliangImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.midelectricity));
			}else if(dianLiang>10&&dianLiang<=40){
				dianliangImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.lowelectricity));
			}else if(dianLiang<=10&&dianLiang>=0){
				dianliangImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.noelectricity));
			}else if(dianLiang<0){
				dianliangImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.wuelectricity));
			}
			final String deviceId = map.get("deviceId");
			if("1".equals(deviceType)){
				shebeitupian.setImageDrawable(getResources().getDrawable(R.drawable.shouzhang));
				binddevicename.setText("智能手杖");
				devicenumber_textview.setText(map.get("deviceCode"));
			}else if("2".equals(deviceType)){
				shebeitupian.setImageDrawable(getResources().getDrawable(R.drawable.belt));
				binddevicename.setText("智能腰带");
				devicenumber_textview.setText(map.get("deviceCode"));
			}
			delete_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if("1".equals(createSign)){
						AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareBindActivity.this);
			    		builder
			    			.setTitle("确认")
			    			.setMessage("确认解绑该设备?")
			    			.setPositiveButton("是", new DialogInterface.OnClickListener() {
			    				@Override
			    				public void onClick(DialogInterface d, int which) {
			    					// TODO Auto-generated method stub
			    					d.cancel();
			    					new UserDeleteDeviceAsyncTask().execute(new String[]{archivesId,deviceId});
			    				}
			    			})
			    			.setNegativeButton("否", new DialogInterface.OnClickListener(){
			    				@Override
			    				public void onClick(DialogInterface d, int which) {
			    					// TODO Auto-generated method stub
			    					d.cancel(); 
			    				}
			    			});
			    		builder.create().show();
					}else if("0".equals(createSign)){
						Toast.makeText(getApplicationContext(), "无权限删除设备", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}
	}
	 
		/**
		 * dis：AsyncTask参数类型：
		 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
		 * 第二个参数表示进度的刻度
		 * 第三个参数表示返回的结果类型
		 * */
		private class UserLoadDeviceByArchIdAsyncTask extends AsyncTask<String, String, String>{
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
					String jsonResult = HttpUtils.doPost("/anyCare/anyCaredDeviceListByArchivesId.action", userInforMap);
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
				dataList.clear();
				if(result!=null&&!"".equals(result)){
					try {
						JSONArray resultArray=new JSONArray(result);
						for(int i = 0;i<resultArray.length();i++){
							JSONObject resultObject = resultArray.getJSONObject(i);
							Map<String,String> map = new HashMap<String, String>();
							map.put("deviceCode", resultObject.getString("deviceCode"));
							map.put("deviceType", resultObject.getString("deviceType"));
							map.put("deviceId", resultObject.getString("deviceId"));
							map.put("dianLiang", TextUtils.isEmpty(resultObject.getString("dianLiang"))?"-1":resultObject.getString("dianLiang"));
							dataList.add(map);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dialog.dismiss();//dialog关闭，数据处理完毕
				}else if("".equals(result)){
					Toast.makeText(getApplicationContext(), "无绑定设备信息！", Toast.LENGTH_SHORT).show();
					dialog.dismiss();//dialog关闭，数据处理完毕
				}
				if(myAdapter==null){
					myAdapter=new MyAdapter();
					bind_listview.setAdapter(myAdapter);
				}else{
					myAdapter.notifyDataSetChanged();
				}
			}
		}
		/**
		 * dis：AsyncTask参数类型：
		 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
		 * 第二个参数表示进度的刻度
		 * 第三个参数表示返回的结果类型
		 * */
		private class UserDeleteDeviceAsyncTask extends AsyncTask<String, String, String>{
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
					userInforMap.put("deviceId", params[1]);
					String jsonResult = HttpUtils.doPost("/anyCare/archivesUnBindDevice.action", userInforMap);
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
				dataList.clear();
				if(result!=null&&!"".equals(result)&&"true".equals(result)){
					Toast.makeText(getApplicationContext(), "解绑设备成功！", Toast.LENGTH_SHORT).show();
					new UserLoadDeviceByArchIdAsyncTask().execute(new String[]{archivesId});
				}else if(result!=null&&!"".equals(result)&&"false".equals(result)){
					Toast.makeText(getApplicationContext(), "解绑设备失败,请重试！", Toast.LENGTH_SHORT).show();
				}else if("".equals(result)){
					Toast.makeText(getApplicationContext(), "操作失败，请重试！", Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();//dialog关闭，数据处理完毕
			}
		}
	 
}

