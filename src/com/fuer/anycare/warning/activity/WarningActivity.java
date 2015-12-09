package com.fuer.anycare.warning.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuer.anycare.common.tool.DateTool;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;

public class WarningActivity extends Activity {
	
	private List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();//������ݵ�List
	private MyAdapter myAdapter;
	private ListView bind_listview;
	
	private Button preButton;
	private Button nextButton;
	private Button nowButton;
	
	private int year;
	private int month;
	private String date;
	
	private LoadingProgressDialog dialog;
	private Dialog mdialog;
	
	private String archivesId;
	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_warning);
		bind_listview =  (ListView) findViewById(R.id.warn_listview); 
		preButton = (Button)this.findViewById(R.id.pre_button);
		nextButton = (Button)this.findViewById(R.id.next_button);
		nowButton = (Button)this.findViewById(R.id.now_button);
		preButton.setOnClickListener(new MyOnClickListener());
        nextButton.setOnClickListener(new MyOnClickListener());
        nowButton.setOnClickListener(new MyOnClickListener());
        //��ʼ������ start
  		year=DateTool.getDateYear();
  		month=DateTool.getDateMonth();
  		//��ʼ��end
  		nowButton.setText(year + "��" + month + "��");
  		//��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
		Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		date = DateTool.yearMonth(year, month);
		new UserLoadGaoJingHistoryAsync().execute(new String[]{archivesId,date}); 
	}
	
	/**
	 * dis:�Զ���adapter��ʵ��listView ��ť�¼�
	 * */
	private class MyAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// �����Ҿͷ���10�ˣ�Ҳ����һ����10��������
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
			convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_warning_list_style,null);
			TextView day= (TextView)convertView.findViewById(R.id.day_textview);
			LinearLayout addviewLayout = (LinearLayout)convertView.findViewById(R.id.addview_layout);//��̬�������
			final Map<String,Object> map=(Map<String,Object>)dataList.get(position);
			String dayString = map.get("servicetime").toString().substring(8);
			day.setText(dayString);
			List<WarnCollectEntity> warnList = (List<WarnCollectEntity>) map.get("warnList");
			for(int i=0;i<warnList.size();i++){
				View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_warning_child_style,null);
				ImageView status = (ImageView)view.findViewById(R.id.status_imageview);
				TextView time= (TextView)view.findViewById(R.id.time_textview);
				TextView address= (TextView)view.findViewById(R.id.address_textview);
				WarnCollectEntity warnCollectEntity = warnList.get(i);
				String biaoZhi = warnCollectEntity.getBiaozhi();
				if("1".equals(biaoZhi)||"3".equals(biaoZhi)){
					status.setBackgroundDrawable(getResources().getDrawable(R.drawable.sos));
				}else if("2".equals(biaoZhi)){
					status.setBackgroundDrawable(getResources().getDrawable(R.drawable.fall));
				}
				String timeString = warnCollectEntity.getServicetime().substring(10);
				time.setText(timeString);
				address.setText(warnCollectEntity.getAddress());
				addviewLayout.addView(view);
			}
			return convertView;
		}
	}
	
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.next_button:
					date = DateTool.calYearOfMonth(date, 1);
					String []yearSplitMonth1 = date.split("-");
					year = Integer.parseInt(yearSplitMonth1[0]);
					month =Integer.parseInt(yearSplitMonth1[1]);
					nowButton.setText(year + "��" + month + "��");
					new UserLoadGaoJingHistoryAsync().execute(new String[]{archivesId,date});
					break;
				case R.id.pre_button:
					date = DateTool.calYearOfMonth(date, -1);
					String []yearSplitMonth2 = date.split("-");
					year = Integer.parseInt(yearSplitMonth2[0]);
					month =Integer.parseInt(yearSplitMonth2[1]);
					nowButton.setText(year + "��" + month + "��");
					new UserLoadGaoJingHistoryAsync().execute(new String[]{archivesId,date});
					break;
				case R.id.now_button:
					showDialog(R.id.now_button);
					int SDKVersion = WarningActivity.this.getSDKVersionNumber();// ��ȡϵͳ�汾
					System.out.println("SDKVersion = " + SDKVersion);
					DatePicker dp = findDatePicker((ViewGroup) mdialog.getWindow().getDecorView());// ���õ���������
					if (dp != null) {
						if (SDKVersion < 11) {
							((ViewGroup) dp.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
						} else if (SDKVersion > 14) {
							//ֻ��ʾ����
							//((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);//.getChildAt(0)
							//ֻ��ʾ����
							((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
							//ֻ��ʾ����
							//((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
							//��ʾ����
							//((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
						}
					 }
					break;
				default:
					break;
			}
		}
	}
	@Override
	 protected Dialog onCreateDialog(int id) {
		switch (id) {
	  		case R.id.now_button:
	  			mdialog = new DatePickerDialog(this, onDateSetListener, year, month-1, 1);
	  			return mdialog;
		}
		return null;
	 }
	
	/**
	 * author:ma_yming
	 * dis:��ʾ���ڶԻ���
	 * */
	private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		  @Override
		  public void onDateSet(DatePicker view, int yearOfYear, int monthOfYear,int dayOfMonth) {
			  month = monthOfYear+1;
			  year = yearOfYear;
			  nowButton.setText(year + "��" + month + "��");
			  date = DateTool.yearMonth(year, month);
			  new UserLoadGaoJingHistoryAsync().execute(new String[]{archivesId,date});
		  }
	 };
	 
	 
	 /**
	  * �ӵ�ǰDialog�в���DatePicker�ӿؼ�
	  * 
	  * @param group
	  * @return
	  */
	 private DatePicker findDatePicker(ViewGroup group) {
		 if (group != null) {
			 for (int i = 0, j = group.getChildCount(); i < j; i++) {
				 View child = group.getChildAt(i);
				 if (child instanceof DatePicker) {
					 return (DatePicker) child;
				 } else if (child instanceof ViewGroup) {
					 DatePicker result = findDatePicker((ViewGroup) child);
				 if (result != null){
					 return result;
				 	}
				 }
			 }
		 }
		 return null;
	 }
	 
	 /**
	 * ��ȡϵͳSDK�汾
	 * 
	 * @return
	 */
	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class UserLoadGaoJingHistoryAsync extends AsyncTask<String, String, String>{
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
			String result = "";
			try{
				String userId = readUserId();
				String archId = params[0];
				String mouth = params[1];
				if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(archId)&&!TextUtils.isEmpty(mouth)){
					Map<String,String> userInforMap = new HashMap<String,String>();
					userInforMap.put("userId", readUserId());
					userInforMap.put("archivesId", archId);
					userInforMap.put("time", mouth);
					result=HttpUtils.doPost("/anyCare/allTypeQueryByArchivesIdGaoJingHistory.action", userInforMap);
				}else{
					result = "paramempty";
				}
				return result;
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
			dataList.clear();
			if(!TextUtils.isEmpty(result)){
				if("paramempty".equals(result)){
					Toast.makeText(getApplicationContext(), "�������������������������", Toast.LENGTH_SHORT).show();
				}else{
					try {
						JSONObject jsonObject = new JSONObject(result);
						int resultStatus = jsonObject.getInt("status");
						if(resultStatus==200){
							JSONArray resultArray = new JSONArray(jsonObject.getString("data"));
							for(int i = 0 ;i<resultArray.length();i++){
								JSONObject dayObject = resultArray.getJSONObject(i);
								String servicetime = dayObject.getString("servicetime");
								JSONArray dayArray = new JSONArray(dayObject.getString("detail"));
								List<WarnCollectEntity> warnList = new ArrayList<WarnCollectEntity>();
								for(int j = 0;j<dayArray.length();j++){
									WarnCollectEntity warnCollectEntity = new WarnCollectEntity();
									JSONObject dayDateObject = dayArray.getJSONObject(j);
									warnCollectEntity.setBiaozhi(dayDateObject.getString("biaozhi"));
									warnCollectEntity.setAddress(dayDateObject.getString("address"));
									warnCollectEntity.setServicetime(dayDateObject.getString("servicetime"));
									warnList.add(warnCollectEntity);
								}
								Map<String,Object> map = new HashMap<String, Object>();
								map.put("servicetime", servicetime);
								map.put("warnList", warnList);
								dataList.add(map);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else {
				Toast.makeText(getApplicationContext(), "��������ʧ�ܣ�����������", Toast.LENGTH_SHORT).show();
			}
			if(myAdapter==null){
				myAdapter=new MyAdapter();
				bind_listview.setAdapter(myAdapter);
			}else{
				myAdapter.notifyDataSetChanged();
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
