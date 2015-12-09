package com.fuer.anycare.map.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.main.activity.AnyCareMainActivity;
import com.fuer.anycare.splash.activity.AnyCareSplashActivity;
import com.fuer.main.anycare.R;

public class AnyCareJpushMapActivity extends Activity {
	
	private  MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private Marker mMarkerA;
	private InfoWindow mInfoWindow;
	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	BitmapDescriptor bdA = null;
	private String uuId;
	private ImageView return_textview;
	private Button mylocation_button;
	private LatLng llA;//��λ����λ��
	private LoadingProgressDialog dialog;
	private String deviceNumber;
	private String longitude;
	private String latitude;
	private String address;
	private String deviceType;
//	BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.layout_jpushmap);
        Bundle bundle = this.getIntent().getExtras();
        uuId = bundle.getString("uuId");
        deviceType = bundle.getString("deviceType");
		return_textview = (ImageView) findViewById(R.id.return_textview);
		return_textview.setOnClickListener(new MyOnClickListener());
		mylocation_button = (Button) findViewById(R.id.mylocation_button);
		mylocation_button.setOnClickListener(new MyOnClickListener());
		
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
					button.setText("���һ�ε�λ����Ϣ");
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
		new loadingLastLocationAsyncTask().execute(uuId,deviceType);
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
				case R.id.return_textview:
					String openParam = readOpenParam();
					if(openParam != null && !"".equals(openParam)){
						startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
					}else{
						startActivity(new Intent(getApplication(), AnyCareSplashActivity.class));
					}
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
					AnyCareJpushMapActivity.this.finish();
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
    
    private String readOpenParam(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��û�����ݣ�����Ĭ��ֵ""
		String openParam=sp.getString("openParam", "");
		return openParam;
	}
    
    /**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class loadingLastLocationAsyncTask extends AsyncTask<String, String, String>{
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
				Map<String,String> userInforMap = new HashMap<String,String>();
				userInforMap.put("uuId", params[0]);
				userInforMap.put("deviceType", params[1]);
				result=HttpUtils.doPost("/anyCare/allTypeQueryByUUId.action", userInforMap);
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
				try{
					JSONObject resultObject=new JSONObject(result);
					int status = resultObject.getInt("status");
					if(status==200){
						JSONObject data=new JSONObject(resultObject.getString("data"));
						longitude = data.getString("longitude");
						latitude = data.getString("latitude");
						address = data.getString("address");
						String biaoZhi = data.getString("biaoZhi");
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
					}else{
						String message = resultObject.getString("message");
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}else {
				Toast.makeText(getApplicationContext(), "ͨѶ�쳣", Toast.LENGTH_SHORT).show();
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startActivity(new Intent(getApplication(), AnyCareSplashActivity.class));
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			AnyCareJpushMapActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
