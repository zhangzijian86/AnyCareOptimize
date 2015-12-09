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
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = null;
	private String uuId;
	private ImageView return_textview;
	private Button mylocation_button;
	private LatLng llA;//定位坐标位置
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
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.layout_jpushmap);
        Bundle bundle = this.getIntent().getExtras();
        uuId = bundle.getString("uuId");
        deviceType = bundle.getString("deviceType");
		return_textview = (ImageView) findViewById(R.id.return_textview);
		return_textview.setOnClickListener(new MyOnClickListener());
		mylocation_button = (Button) findViewById(R.id.mylocation_button);
		mylocation_button.setOnClickListener(new MyOnClickListener());
		
		//获取地图控件引用 
        bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);	
        mMapView = (MapView) findViewById(R.id.bmapView);  
        mBaiduMap = mMapView.getMap();
        //设置缩放
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        //添加图标
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
					button.setText("最近一次的位置信息");
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
		//初始化dialog
  		dialog=new LoadingProgressDialog(this,"正在加载...");
  		//初始化dialog end
		new loadingLastLocationAsyncTask().execute(uuId,deviceType);
	}
	/**
	 * 设置是否显示交通图
	 */
	public void setTraffic(View view) {
		mBaiduMap.setTrafficEnabled(true);
	}
	
	@Override  
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
        
        super.onDestroy(); 
        bdA.recycle();
//        bd.recycle();
    }
	
    @Override  
    protected void onResume() {
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
    }
    
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
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
					// sourceLatLng待转换坐标  
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
		//若没有数据，返回默认值""
		String openParam=sp.getString("openParam", "");
		return openParam;
	}
    
    /**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class loadingLastLocationAsyncTask extends AsyncTask<String, String, String>{
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
		
		//数据处理完毕后更新UI操作
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
						// sourceLatLng待转换坐标  
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
				Toast.makeText(getApplicationContext(), "通讯异常", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class SeeCrutchAsyncTask extends AsyncTask<String, String, String>{
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
		
		//数据处理完毕后更新UI操作
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
