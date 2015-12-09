package com.fuer.anycare.map.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.login.activity.AnyCareLoginActivity;
import com.fuer.main.anycare.R;

public class AnyCareMapActivity extends Activity{
	
	private  MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private Marker mMarkerA;
	private InfoWindow mInfoWindow;
	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	BitmapDescriptor bdA = null;
	private String archivesId;
	private String crutchDeviceNumber;
	private String beltDeviceNumber;
	private String serviceTime;
	private String longitude;
	private String latitude;
	private String address;
	private Button locationBtn;
	private Button liShiBtn;
	private String liShiSign="OFF";
	private Button mylocation_button;
	private LatLng llA;//��λ����λ��
	private LoadingProgressDialog dialog;
	
	private String crutchId;
	private String beltId;
	
	private SeekBar seekBar;
	private Drawable myThumb ;
	
	private float yuanBanJing;
	private double yuanLat;
	private double yuanLon;
	
	private boolean choice[]= new boolean[]{false,false};
	
//	BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.layout_map);
        Bundle bundle = this.getIntent().getExtras();
        archivesId = bundle.getString("archivesId");
        crutchDeviceNumber = bundle.getString("crutchDeviceNumber");
        beltDeviceNumber = bundle.getString("beltDeviceNumber");
        longitude = bundle.getString("longitude");
        latitude = bundle.getString("latitude");
        address = bundle.getString("address");
        crutchId = bundle.getString("crutchId");
        beltId = bundle.getString("beltId");
        serviceTime = bundle.getString("serviceTime");
        yuanBanJing = TextUtils.isEmpty(bundle.getString("yuanBanJing"))? 0f:Float.parseFloat(bundle.getString("yuanBanJing"));
    	yuanLat = TextUtils.isEmpty(bundle.getString("yuanLat"))? 0d:Double.parseDouble(bundle.getString("yuanLat"));
    	yuanLon = TextUtils.isEmpty(bundle.getString("yuanLon"))? 0d:Double.parseDouble(bundle.getString("yuanLon"));
        liShiBtn = (Button) findViewById(R.id.guiji);
        locationBtn = (Button) findViewById(R.id.location);
		liShiBtn.setOnClickListener(new MyOnClickListener());
		locationBtn.setOnClickListener(new MyOnClickListener());
		mylocation_button = (Button) findViewById(R.id.mylocation_button);
		mylocation_button.setOnClickListener(new MyOnClickListener());
		seekBar = (SeekBar) findViewById(R.id.myline);
		myThumb = getResources().getDrawable(R.drawable.slide_circle0);
		myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
		seekBar.setThumb(myThumb);
		seekBar.setPadding(60, 5, 60, 5);
		seekBar.setMax(3);
		if(yuanBanJing==0f){
			myThumb = getResources().getDrawable(R.drawable.slide_circle0);
			myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
			seekBar.setThumb(myThumb);
			seekBar.setPadding(60, 5, 60, 5);
			seekBar.setProgress(0);
		}else if(yuanBanJing==1000f){
			myThumb = getResources().getDrawable(R.drawable.slide_circle1);
			myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
			seekBar.setThumb(myThumb);
			seekBar.setPadding(60, 5, 60, 5);
			seekBar.setProgress(1);
		}else if(yuanBanJing==2000f){
			myThumb = getResources().getDrawable(R.drawable.slide_circle2);
			myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
			seekBar.setThumb(myThumb);
			seekBar.setPadding(60, 5, 60, 5);
			seekBar.setProgress(2);
		}else if(yuanBanJing==5000f){
			myThumb = getResources().getDrawable(R.drawable.slide_circle5);
			myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
			seekBar.setThumb(myThumb);
			seekBar.setPadding(60, 5, 60, 5);
			seekBar.setProgress(3);
		}
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int iStrength = seekBar.getProgress();
				switch (iStrength) {
				case 0:
					new SetArchivesWeilanAsyncTask().execute(new String[]{archivesId,longitude,latitude,"0"});
					yuanBanJing = 0f;
					break;
				case 1:
					new SetArchivesWeilanAsyncTask().execute(new String[]{archivesId,longitude,latitude,"1000"});
					yuanBanJing = 1000f;
					break;
				case 2:
					new SetArchivesWeilanAsyncTask().execute(new String[]{archivesId,longitude,latitude,"2000"});
					yuanBanJing = 2000f;
					break;
				case 3:
					new SetArchivesWeilanAsyncTask().execute(new String[]{archivesId,longitude,latitude,"5000"});
					yuanBanJing = 5000f;
					break;
				}
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
//				description.setText("��ʼ�϶�");
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//				description.setText("��ǰ���ȣ�" + progress + "%");
				int iStrength = seekBar.getProgress();
				switch (iStrength) {
				case 0:
					myThumb = getResources().getDrawable(R.drawable.slide_circle0);
					break;
				case 1:
					myThumb = getResources().getDrawable(R.drawable.slide_circle1);
					break;
				case 2:
					myThumb = getResources().getDrawable(R.drawable.slide_circle2);
					break;
				case 3:
					myThumb = getResources().getDrawable(R.drawable.slide_circle5);
					break;
				}
				myThumb.setBounds(new Rect(0, 0, myThumb.getIntrinsicWidth(),myThumb.getIntrinsicHeight()));
				seekBar.setThumb(myThumb);
				seekBar.setPadding(60, 5, 60, 5);
				seekBar.setMax(3);
			}
		});
		//��ȡ��ͼ�ؼ����� 
        bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);	
        mMapView = (MapView) findViewById(R.id.bmapView);  
        mBaiduMap = mMapView.getMap();
        //��������
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        //���ͼ��
        llA = new LatLng(39.963175, 116.400244);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
		
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llA);
		mBaiduMap.setMapStatus(u);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				OnInfoWindowClickListener listener = null;
				if (marker == mMarkerA ) {
					button.setText("λ����Ϣ:"+address+"\n����ʱ��:"+serviceTime);
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							mBaiduMap.hideInfoWindow();
						}
					};
					LatLng ll = marker.getPosition();
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -90, listener);
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return true;
			}
		});
//      loveobject_button.setOnClickListener(new MyOnClickListener());
		//��ʼ��dialog
  		dialog=new LoadingProgressDialog(this,"���ڼ���...");
  		//��ʼ��dialog end
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);
		llA = new LatLng(lat,lon);
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.GPS);  
		// sourceLatLng��ת������  
		converter.coord(llA);  
		LatLng desLatLng = converter.convert();
		mMarkerA.setPosition(desLatLng);
		u = MapStatusUpdateFactory.newLatLng(desLatLng);
		mBaiduMap.setMapStatus(u);
		new SeeCrutchAsyncTask().execute(archivesId);
		//���Χ����Ϊ0������ʾΧ����Ϣ
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		if(yuanLat!=0&&yuanLon!=0){
			LatLng weilan = new LatLng(yuanLat,yuanLon);
			converter.coord(weilan);
			weilan = converter.convert();
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(yuanBanJing)
			// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
			.direction(100)
			.latitude(weilan.latitude)
			.longitude(weilan.longitude).build();
			mBaiduMap.setMyLocationData(locData);
		}
		
		if(!(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId))){
			AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareMapActivity.this);
    		builder
    			.setTitle("������")
    			.setMessage("�Ƿ�ִ�з����ѯ?")
    			.setPositiveButton("��", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,beltDeviceNumber});
    					dialog.cancel();
    				}
    			})
    			.setNegativeButton("��", new DialogInterface.OnClickListener(){
    	
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					dialog.cancel(); 
    				}
    			});
    		builder.create().show();
		}
	}
	/**
	 * �����Ƿ���ʾ��ͨͼ
	 */
	public void setTraffic(View view) {
		mBaiduMap.setTrafficEnabled(true);
	}
	
	@Override  
    protected void onDestroy() {
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
        super.onDestroy(); 
        bdA.recycle();
//        bd.recycle();
    }
	
    @Override  
    protected void onResume() {
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
    }
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
    } 
    
    private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				//չʾ���µ�5�ι켣��Ϣ
				case R.id.guiji:
					if("OFF".equals(liShiSign)){
						//�����µ���ʷ���ݹ켣��Ϣ��ʾ
						new ArchivesLiShiAsyncTask().execute(new String[]{archivesId});
					}else if("ON".equals(liShiSign)){
						//�ر����µ���ʷ���ݹ켣��Ϣ��ʾ
//						liShiBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.orangecircle));
					}
					break;
				//�����ѯ����
				case R.id.location:
					if(!"".equals(crutchDeviceNumber)&&!"".equals(beltDeviceNumber)){
						AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareMapActivity.this);
						builder.setTitle("ѡ�����ѯ���豸")
							.setMultiChoiceItems(new String[] { "����", "����" }, choice, new DialogInterface.OnMultiChoiceClickListener(){

								@Override
								public void onClick(DialogInterface dialog,int which, boolean choicesign) {
									// TODO Auto-generated method stub
									choice[which] = choicesign;
								}
							})
							.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									boolean choice0 = choice[0];
									boolean choice1 = choice[1];
									if(choice1 == false && choice0==false){
										Toast.makeText(getApplicationContext(), "��ѡ����豸", Toast.LENGTH_SHORT).show();
									}else if(choice1 == false && choice0==true){
										new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,""});
									}else if(choice1 == true && choice0==false){
										new FanXiangQueryAsyncTask().execute(new String[]{"",beltDeviceNumber});
									}else if(choice1 == true && choice0==true){
										new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,beltDeviceNumber});
									}
								}
							})
							.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	
								@Override
								public void onClick(DialogInterface dialog,int which) {
									// TODO Auto-generated method stub
									dialog.cancel(); 
								}
							})
							.show();
					//�й����豸���������豸
					}else if(!"".equals(crutchDeviceNumber)&&"".equals(beltDeviceNumber)){
						new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,""});
					//�޹����豸���������豸
					}else if("".equals(crutchDeviceNumber)&&!"".equals(beltDeviceNumber)){
						new FanXiangQueryAsyncTask().execute(new String[]{"",beltDeviceNumber});
					}else{
						Toast.makeText(getApplicationContext(), "���豸,������", Toast.LENGTH_SHORT).show();
					}
//					new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,beltDeviceNumber});
					break;
				case R.id.mylocation_button:
					double lat = Double.parseDouble(latitude);
					double lon = Double.parseDouble(longitude);
					llA = new LatLng(lat,lon);
					CoordinateConverter converter  = new CoordinateConverter();  
					converter.from(CoordType.GPS);  
					// sourceLatLng��ת������  
					converter.coord(llA);  
					LatLng desLatLng = converter.convert();
					mMarkerA.setPosition(desLatLng);
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(desLatLng);
					mBaiduMap.setMapStatus(u);
					break;
			}
		}
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
	private class FanXiangQueryAsyncTask extends AsyncTask<String, String, String>{
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
				String crutchDN = params[0];
				String beltDN = params[1];
				if(!TextUtils.isEmpty(userId)&&(!TextUtils.isEmpty(crutchDN)||!TextUtils.isEmpty(beltDN))){
					Map<String,String> userInforMap = new HashMap<String,String>();
					userInforMap.put("userId", userId);
					userInforMap.put("crutchDeviceNumber", crutchDN);
					userInforMap.put("beltDeviceNumber", beltDN);
					result=HttpUtils.doPost("/anyCare/fanXiangQueryRequest.action", userInforMap);
				}else{
					result = "paramempty";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
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
				if("paramempty".equals(result)){
					Toast.makeText(getApplicationContext(), "�������������������������", Toast.LENGTH_SHORT).show();
				}else{
					if("repeatcommit".equals(result)){
						Toast.makeText(getApplicationContext(), "5����֮�ڲ����ظ��ύ��", Toast.LENGTH_SHORT).show();
					}else{
						locationBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.locating));
						Toast.makeText(getApplicationContext(), "�����ύ�ɹ������Ե�...", Toast.LENGTH_SHORT).show();
					}
				}
			}else {
				AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareMapActivity.this);
				builder.setTitle("����!")
					.setMessage("��������ʧ�ܣ��Ƿ���������")
					.setPositiveButton("��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new FanXiangQueryAsyncTask().execute(new String[]{crutchDeviceNumber,beltDeviceNumber});
						}
					})
					.setNegativeButton("��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel(); 
						}
					});
				builder.create().show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	
	
	
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class SeeCrutchAsyncTask extends AsyncTask<String, String, String>{
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
			String result = "";
			try{
				Map<String,String> userInforMap = new HashMap<String,String>();
				userInforMap.put("archivesId", params[0]);
				result=HttpUtils.doPost("/anyCare/seeCrutchData.action", userInforMap);
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
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
				
			}else {
				
			}
		}
	}
    
	/**
	 * dis��AsyncTask�������ͣ����õ���Χ������
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class SetArchivesWeilanAsyncTask extends AsyncTask<String, String, String>{
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
			String result = "";
			try{
				String userId = readUserId();
				String archId = params[0];
				String yuanLon =  params[1];
				String yuanLat =  params[2];
				String banJing = params[3];
				if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(archId)&&!TextUtils.isEmpty(yuanLon)&&!TextUtils.isEmpty(yuanLat)&&!TextUtils.isEmpty(banJing)){
					Map<String,String> userInforMap = new HashMap<String,String>();
					userInforMap.put("userId", readUserId());
					userInforMap.put("archivesId", archId);
					userInforMap.put("yuanLon", yuanLon);
					userInforMap.put("yuanLat", yuanLat);
					userInforMap.put("banJing", banJing);
					result=HttpUtils.doPost("/anyCare/setArchivesWeilan.action", userInforMap);
				}else{
					result = "paramempty";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
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
			if(!TextUtils.isEmpty(result)){
				if("paramempty".equals(result)){
					Toast.makeText(getApplicationContext(), "�������������������������", Toast.LENGTH_SHORT).show();
				}else{
					yuanLat = Double.parseDouble(latitude);
					yuanLon = Double.parseDouble(longitude);
					LatLng weilan = new LatLng(yuanLat,yuanLon);
					CoordinateConverter converter  = new CoordinateConverter();  
					converter.from(CoordType.GPS);  
					converter.coord(weilan);
					weilan = converter.convert();
					MyLocationData locData = new MyLocationData.Builder()
					.accuracy(yuanBanJing)
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100)
					.latitude(weilan.latitude)
					.longitude(weilan.longitude).build();
					mBaiduMap.setMyLocationData(locData);
					Toast.makeText(getApplicationContext(), "Χ�����óɹ�", Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(getApplicationContext(), "��������ʧ�ܣ�����������", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * dis��AsyncTask�������ͣ��鿴5����ʷ��Ϣ
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class ArchivesLiShiAsyncTask extends AsyncTask<String, String, String>{
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
			String result = "";
			try{
				String userId = readUserId();
				String archId = params[0];
				if(!TextUtils.isEmpty(userId)&&!TextUtils.isEmpty(archId)){
					Map<String,String> userInforMap = new HashMap<String,String>();
					userInforMap.put("userId", userId);
					userInforMap.put("archivesId", archId);
					result=HttpUtils.doPost("/anyCare/allTypeQueryByArchivesIdHistory.action", userInforMap);
				}else{
					result = "paramempty";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
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
			try{
				if(!TextUtils.isEmpty(result)){
					if("paramempty".equals(result)){
						Toast.makeText(getApplicationContext(), "�������������������������", Toast.LENGTH_SHORT).show();
					}else{
						//����JSONOBJECT��������
						JSONObject jsonObject = new JSONObject(result);
						int resultStatus = jsonObject.getInt("status");
						if(resultStatus==200){
							JSONArray resultArray = new JSONArray(jsonObject.getString("data"));
							BitmapDescriptor hisA = BitmapDescriptorFactory.fromResource(R.drawable.icon_hiscoding);	
							CoordinateConverter converter  = new CoordinateConverter();  
							converter.from(CoordType.GPS);  
							// sourceLatLng��ת������  
							if(resultArray.length()>2){
								for(int i = 1 ;i<resultArray.length();i++){
									JSONObject resultObject = resultArray.getJSONObject(i);
									String liShiLatitude = resultObject.getString("latitude");
									String liShiLongitude = resultObject.getString("longitude");
									LatLng pp = new LatLng(Double.parseDouble(liShiLatitude), Double.parseDouble(liShiLongitude));
									converter.coord(pp); 
									LatLng desLatLng = converter.convert();
									OverlayOptions ooP = new MarkerOptions().position(desLatLng).icon(hisA).zIndex(9).draggable(true);
									Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooP));
								}
								liShiSign="ON";
								liShiBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.display));
							}else{
								Toast.makeText(getApplicationContext(), "������ʷ����", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "����ʧ�ܣ�����������", Toast.LENGTH_SHORT).show();
						}
					}
				}else {
					Toast.makeText(getApplicationContext(), "��������ʧ�ܣ�����������", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
}
