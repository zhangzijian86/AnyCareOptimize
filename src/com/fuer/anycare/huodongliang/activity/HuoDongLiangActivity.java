package com.fuer.anycare.huodongliang.activity;

import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuer.anycare.chart.view.ChartView;
import com.fuer.anycare.common.tool.DateTool;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.database.util.DataBaseManager;
import com.fuer.anycare.database.util.XingZouJingZhiEntity;
import com.fuer.main.anycare.R;

public class HuoDongLiangActivity extends Activity{
	
	private LinearLayout layout;
	private LinearLayout xingzoulayout;
	private LinearLayout tixinglayout;
	private TextView xingzoutext;
	private TextView xingzouvalue;
	private TextView tixingtext;
	private TextView tixingvalue;
	
	private Button preButton;
	private Button nextButton;
	private Button nowButton;
	
	private int year;
	private int month;
	private String date;
	
	private Dialog mdialog;
	private LoadingProgressDialog dialog;
	private DataBaseManager dataBaseManager; 
	private String deviceNumber;
	private String sign = "H";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_huodongliang);
		layout = (LinearLayout) this.findViewById(R.id.rela); 
		xingzoulayout= (LinearLayout) this.findViewById(R.id.xingzoulayout); 
		tixinglayout= (LinearLayout) this.findViewById(R.id.tixinglayout); 
		xingzoutext = (TextView) this.findViewById(R.id.xingzoutext);  
		xingzouvalue = (TextView) this.findViewById(R.id.xingzouvalue);
		tixingtext = (TextView) this.findViewById(R.id.tixingtext);
		tixingvalue = (TextView) this.findViewById(R.id.tixingvalue);
		preButton = (Button)this.findViewById(R.id.pre_button);
		nextButton = (Button)this.findViewById(R.id.next_button);
		nowButton = (Button)this.findViewById(R.id.now_button);
        xingzoulayout.setOnClickListener(new MyOnClickListener());
        tixinglayout.setOnClickListener(new MyOnClickListener());
        preButton.setOnClickListener(new MyOnClickListener());
        nextButton.setOnClickListener(new MyOnClickListener());
        nowButton.setOnClickListener(new MyOnClickListener());
        //��ʼ������ start
  		year=DateTool.getDateYear();
  		month=DateTool.getDateMonth();
  		//��ʼ��end
  		nowButton.setText(year + "��" + month + "��");
  		date = DateTool.yearMonth(year, month);
  		//��ʼ��dialog
  		dialog=new LoadingProgressDialog(this,"���ڼ���...");
  		//��ʼ��dialog end
  		//���������·ݻ������
  		Bundle bundle = this.getIntent().getExtras();
  		deviceNumber = bundle.getString("beltDeviceNumber");
  		dataBaseManager = new DataBaseManager(this);
  		new loadHuoDongLiangAsyncTask().execute(new String[]{deviceNumber,date});
	}
	
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.xingzoulayout:
				xingzoulayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.orangecircle));
				tixinglayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.graycircle));
				xingzoutext.setTextColor(Color.rgb(253,123,93));
				xingzouvalue.setTextColor(Color.rgb(253,123,93));
				tixingtext.setTextColor(Color.rgb(162,162,162));
				tixingvalue.setTextColor(Color.rgb(162,162,162));
				sign = "H";
				new loadHuoDongLiangAsyncTask().execute(new String[]{deviceNumber,date});
				break;
			case R.id.tixinglayout:
				tixinglayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.orangecircle));
				xingzoulayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.graycircle));
				xingzoutext.setTextColor(Color.rgb(162,162,162));
				xingzouvalue.setTextColor(Color.rgb(162,162,162));
				tixingtext.setTextColor(Color.rgb(253,123,93));
				tixingvalue.setTextColor(Color.rgb(253,123,93));
				sign = "J";
				new loadJingZhiAsyncTask().execute(new String[]{deviceNumber,date});
				break;
			case R.id.next_button:
				date = DateTool.calYearOfMonth(date, 1);
				String []yearSplitMonth1 = date.split("-");
				year = Integer.parseInt(yearSplitMonth1[0]);
				month =Integer.parseInt(yearSplitMonth1[1]);
				nowButton.setText(year + "��" + month + "��");
				if("H".equals(sign)){
					new loadHuoDongLiangAsyncTask().execute(new String[]{deviceNumber,date});
				}else if("J".equals(sign)){
					new loadJingZhiAsyncTask().execute(new String[]{deviceNumber,date});
				}
				break;
			case R.id.pre_button:
				date = DateTool.calYearOfMonth(date, -1);
				String []yearSplitMonth2 = date.split("-");
				year = Integer.parseInt(yearSplitMonth2[0]);
				month =Integer.parseInt(yearSplitMonth2[1]);
				nowButton.setText(year + "��" + month + "��");
				if("H".equals(sign)){
					new loadHuoDongLiangAsyncTask().execute(new String[]{deviceNumber,date});
				}else if("J".equals(sign)){
					new loadJingZhiAsyncTask().execute(new String[]{deviceNumber,date});
				}
				break;
			case R.id.now_button:
				showDialog(R.id.now_button);
				int SDKVersion = HuoDongLiangActivity.this.getSDKVersionNumber();// ��ȡϵͳ�汾
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
			  if("H".equals(sign)){
					new loadHuoDongLiangAsyncTask().execute(new String[]{deviceNumber,date});
				}else if("J".equals(sign)){
					new loadJingZhiAsyncTask().execute(new String[]{deviceNumber,date});
				}
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
	private class loadHuoDongLiangAsyncTask extends AsyncTask<String, String, List<XingZouJingZhiEntity>>{
		
		//����ִ��֮ǰ�Ĳ���
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();//��ʾdialog���������ڴ���....
		}
		
		@Override
		protected List<XingZouJingZhiEntity> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<XingZouJingZhiEntity> list = null;
			try{
				String deviceNumber = params[0];
				String deviceNumberD = deviceNumber.toUpperCase();
				String deviceNumberX = deviceNumber.toLowerCase();
				String date = params[1];
				String startDate = date+"-01";
				String endDate = date+"-31";
				list = dataBaseManager.query(deviceNumberX, startDate, endDate);
				if(list.size()==0){
					list = dataBaseManager.query(deviceNumberD, startDate, endDate);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return list;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
		}

		@Override
		protected void onPostExecute(List<XingZouJingZhiEntity> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String[] YData;
			String[] XData;
			String[] BData;
			if(result!=null){
				int length = result.size();
				XData = new String[length];
				YData = new String[length];
				BData = new String[length];
				for(int i= 0;i<result.size();i++){
					XingZouJingZhiEntity xingZouJingZhiEntity = result.get(i);
					YData[i] =  xingZouJingZhiEntity.getHuoDongLiang();
					BData[i] =  xingZouJingZhiEntity.getBuShu();
					if(i%2==1){
						String date = xingZouJingZhiEntity.getTime().split("-")[2];
						XData[i] = date;
					}else{
						XData[i] = "";
					}
				}
			}else{
				XData = new String[0];
				YData = new String[0];
				BData = new String[0];
			}
			ChartView v1 = new ChartView(HuoDongLiangActivity.this);
			layout.removeAllViewsInLayout();
			v1.SetInfo(YData,
    			getWindowManager().getDefaultDisplay(),
    			XData,
              	"��",
              	"ǧ��",
              	"ǧ��/��",
              	BData,
              	sign,
              	xingzouvalue,
              	tixingvalue
    		);
			layout.addView(v1);
			dialog.dismiss();
		}
		
		
		
	}
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class loadJingZhiAsyncTask extends AsyncTask<String, String, List<XingZouJingZhiEntity>>{
		
		//����ִ��֮ǰ�Ĳ���
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();//��ʾdialog���������ڴ���....
		}
		
		@Override
		protected List<XingZouJingZhiEntity> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<XingZouJingZhiEntity> list = null;
			try{
				String deviceNumber = params[0];
				String deviceNumberD = deviceNumber.toUpperCase();
				String deviceNumberX = deviceNumber.toLowerCase();
				String date = params[1];
				String startDate = date+"-01";
				String endDate = date+"-31";
				list = dataBaseManager.query(deviceNumberX, startDate, endDate);
				if(list.size()==0){
					list = dataBaseManager.query(deviceNumberD, startDate, endDate);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return list;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
		}

		@Override
		protected void onPostExecute(List<XingZouJingZhiEntity> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String[] YData;
			String[] XData;
			String[] BData;
			if(result!=null){
				int length = result.size();
				XData = new String[length];
				YData = new String[length];
				BData = new String[length];
				for(int i= 0;i<result.size();i++){
					XingZouJingZhiEntity xingZouJingZhiEntity = result.get(i);
					YData[i] =  xingZouJingZhiEntity.getJingZhi();
					BData[i] =  xingZouJingZhiEntity.getBuShu();
					if(i%2==1){
						String date = xingZouJingZhiEntity.getTime().split("-")[2];
						XData[i] = date;
					}else{
						XData[i] = "";
					}
				}
			}else{
				XData = new String[0];
				YData = new String[0];
				BData = new String[0];
			}
			ChartView v1 = new ChartView(HuoDongLiangActivity.this);
			layout.removeAllViewsInLayout();
			v1.SetInfo(YData,
    			getWindowManager().getDefaultDisplay(),
    			XData,
              	"��",
              	"��",
              	"��/��",
              	BData,
              	sign,
              	xingzouvalue,
              	tixingvalue
    		);
			layout.addView(v1);
			dialog.dismiss();
		}
		
		
		
	}
}
