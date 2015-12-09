package com.fuer.anycare.main.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fuer.anycare.archives.activity.AddArchByIdActivity;
import com.fuer.anycare.archives.activity.AddArchivesFirstActivity;
import com.fuer.anycare.bind.activity.AnyCareBindActivity;
import com.fuer.anycare.common.tool.DateTool;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.database.util.DataBaseManager;
import com.fuer.anycare.database.util.XingZouJingZhiEntity;
import com.fuer.anycare.huodongliang.activity.HuoDongLiangActivity;
import com.fuer.anycare.login.activity.AnyCareLoginActivity;
import com.fuer.anycare.map.activity.AnyCareMapActivity;
import com.fuer.anycare.set.activity.AnyCareSetActivity;
import com.fuer.anycare.version.utils.DownLoadManager;
import com.fuer.anycare.version.utils.UpdataInfo;
import com.fuer.anycare.version.utils.UpdataInfoParser;
import com.fuer.anycare.warning.activity.WarningActivity;
import com.fuer.main.anycare.R;

public class AnyCareMainActivity extends Activity {
	/**
	 * ViewPager
	 */
	private ViewPager mViewPager;
	/**
	 * ViewPager的适配器
	 */
	private PagerAdapter mAdapter;
	private List<View> mViews;
	private LayoutInflater mInflater;
	
	private int currentIndex;
	/**
	 * 底部两个textview
	 */
	private TextView anycareTV;
	private TextView webmobileTV;
	
	private LinearLayout mainlayout;
	private View popView;
	private PopupWindow popWindow;
	
	private TextView telephoneTV;
	private Button addArchBtn;
	private TextView popsetTV;
	private TextView popexitTV;
	private MyAdapter myAdapter;
	private List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();//存放数据的List
	private ListView bind_listview;
	
	private TextView nameTV;
	private TextView idhaoTV;
	private TextView diquTV;
	private ImageView tianqituIM;
	private TextView wenduTV;
	
	private String archivesId;
	
	private String yuanBanJing;
	private String yuanLat;
	private String yuanLon;
	
	private String crutchDeviceNumber;
	private String beltDeviceNumber;
	private String longitude;
	private String latitude;
	private String address;
	private String crutchDeviceId;//对应拐杖设备表中的id
	private String beltDeviceId;//对应腰带设备表中的id
	
	private String crutchId;//对应拐杖收集到的最新设备数据的Id
	private String beltId;//对应腰带收集到的最新设备数据的Id
	
	private String serviceTime;//显示服务器最新时间
	private String biaoZhi;//1为sos，2位反向查询
	
	
	private Button showMapBtn;
	private Button qiujiuBtn;
	private Button huodongliangBtn;
	private LinearLayout pagerLayout;//广告栏用 add 2015-10-19
	private ViewPager adViewPager;//广告栏用 add 2015-10-19
	private List<View> pageViews;//广告栏用 add 2015-10-19
	private AdPageAdapter adPageAdapter;//广告栏用 add 2015-10-19
	private boolean isContinue = true;//广告栏用 add 2015-10-19
	private ImageView imageView;//广告栏用 add 2015-10-19
	private ImageView[] imageViews;//广告栏用 add 2015-10-19
	private AtomicInteger atomicInteger = new AtomicInteger(0);//广告栏用 add 2015-10-19
	
	private LoadingProgressDialog dialog;
	/**
     * 检测服务器版本更新所用参数
     * */
    private final String TAG = this.getClass().getName();
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private UpdataInfo info;
	private String localVersion;
	
	private DataBaseManager dataBaseManager; 
	private String stime;
	
	private int page = 0;
	private String createSign;//档案信息创建标志1标识自己创建，0表示不是自己账号创建
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		mInflater = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
		anycareTV = (TextView) findViewById(R.id.anycareTV);
		webmobileTV = (TextView) findViewById(R.id.webmobileTV);
		anycareTV.setOnClickListener(new MyOnClickListener());
		webmobileTV.setOnClickListener(new MyOnClickListener());
		/**
		 * 初始化View
		 */
		initView();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener(){
			float maxArg = 0f;
			@Override
			public void onPageSelected(int position){
				resetTabBtn();
				switch (position){
					case 0:
						anycareTV.setTextColor(getResources().getColor(R.color.txt_orange));
						break;
					case 1:
						webmobileTV.setTextColor(getResources().getColor(R.color.txt_orange));
						break;
				}
				currentIndex = position;
			}
			@Override
			public void onPageScrolled(int position, float arg1, int arg2){
				if(maxArg < arg1){
					maxArg = arg1;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0){
				if(arg0 == 1){
					maxArg = 0f;
				}
				if(arg0 == 0 && currentIndex == 0 && maxArg == 0 && popWindow.isShowing()==false){
					//设置在屏幕左侧的显示位置
					 popWindow.showAtLocation(mainlayout, Gravity.NO_GRAVITY, 0, 0);
				}
			}
		});
		
		//初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
//		new loadAllDataByUserIdAsyncTask().execute();
		//自动检测版本语句开始
		try{
			localVersion = getVersionName();
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		//自动检测版本语句结束
		dataBaseManager = new DataBaseManager(this);  
		stime = dataBaseManager.queryParam("1");
		page = readUserPage();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String userId =readUserId();
		if(userId!=null&&!"".equals(userId)){
			new loadAllDataByUserIdAsyncTask().execute();
		}else{
			finish();
		}
	}

	private String readUserId(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String userId=sp.getString("userId", "");
		return userId;
	}
	
	private String readUserPhone(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		String phoneNumber=sp.getString("phoneNumber", "");
		return phoneNumber;
	}

	private void initView(){
		mViews = new ArrayList<View>();
		View first = mInflater.inflate(R.layout.main_tab_01, null);
		View second = mInflater.inflate(R.layout.main_tab_02, null);
		//点击查询地图btn
		showMapBtn = (Button) first.findViewById(R.id.dianjichaxun);
		showMapBtn.setOnClickListener(new MyOnClickListener());
		qiujiuBtn = (Button) first.findViewById(R.id.qiuzhu);
		qiujiuBtn.setOnClickListener(new MyOnClickListener());
		huodongliangBtn  = (Button) first.findViewById(R.id.huodongliang);
		huodongliangBtn.setOnClickListener(new MyOnClickListener());
		
		ImageView popleft = (ImageView) first.findViewById(R.id.zuotouxiangtu);
		ImageView deviceright = (ImageView) first.findViewById(R.id.youbiaogetu);
		popleft.setOnClickListener(new MyOnClickListener());
		deviceright.setOnClickListener(new MyOnClickListener());
		nameTV = (TextView) first.findViewById(R.id.mingzi);
		idhaoTV = (TextView) first.findViewById(R.id.idhao);
		diquTV = (TextView) first.findViewById(R.id.diqu);
		tianqituIM =  (ImageView) first.findViewById(R.id.tianqitu);
		wenduTV = (TextView) first.findViewById(R.id.wendu);
		//webview监听
		WebView myWebView = (WebView)second.findViewById(R.id.my_webview);
		//webview必须设置支持Javascript
		myWebView.getSettings().setJavaScriptEnabled(true);
		//requestFocus();
		//设置是否支持缩放，这里为false，默认为true
		myWebView.getSettings().setSupportZoom(true);
		//设置是否显示缩放工具，默认为false。
		myWebView.getSettings().setBuiltInZoomControls(false);
		//一般很少会用到这个，用WebView组件显示普通网页时一般会出现横向滚动条，这样会导致页面查看起来非常不方便。
		//LayoutAlgorithm是一个枚举，用来控制html的布局，总共有三种类型：
		//NORMAL：正常显示，没有渲染变化。
		//SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
		//NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
		myWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.loadUrl("http://www.any-care.cn/mobile/index.htmlx");
		myWebView.setWebViewClient(wvc);
		mViews.add(first);
		mViews.add(second);
		mAdapter = new PagerAdapter(){
			@Override
			public void destroyItem(ViewGroup container, int position, Object object){
				container.removeView(mViews.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position){
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1){
				return arg0 == arg1;
			}
			@Override
			public int getCount(){
				return mViews.size();
			}
		};
		// 装载R.layout.popup对应的界面布局
		popView = this.getLayoutInflater().inflate(R.layout.layout_pop, null);
		popView.setFocusable(true); // 这个很重要
		popView.setFocusableInTouchMode(true);
		popsetTV = (TextView) popView.findViewById(R.id.zhuyeshezhi);
		popexitTV = (TextView) popView.findViewById(R.id.zhuyetuichu);
		addArchBtn  = (Button) popView.findViewById(R.id.jiahao);
		telephoneTV = (TextView) popView.findViewById(R.id.telephone);
		bind_listview =  (ListView) popView.findViewById(R.id.bind_listview);
		popsetTV.setOnClickListener(new MyOnClickListener());
		popexitTV.setOnClickListener(new MyOnClickListener());
		addArchBtn.setOnClickListener(new MyOnClickListener());
		
		//获取屏幕dip数据
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
		popWindow = new PopupWindow(popView, 0, 0, true);
		popWindow.setWidth(dm.widthPixels*4/5);
		popWindow.setHeight(LayoutParams.FILL_PARENT);
		popWindow.setAnimationStyle(R.style.animationInOut);
		popWindow.setTouchable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable()); 
		//添加返回按钮事件
		popView.setOnKeyListener(new OnKeyListener() {
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK) {
		        	popWindow.dismiss();
		            return true;
		        }
		        return false;
		    }
		});
		//增加滑动栏 add 2015-10-19 start
		pagerLayout = (LinearLayout) first.findViewById(R.id.view_pager_content);
		adViewPager = new ViewPager(this);
        int densityDpi = dm.densityDpi;
        switch (densityDpi) {
			//ldpi(120)
	        case DisplayMetrics.DENSITY_LOW:
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
			//mdpi(160)
			case DisplayMetrics.DENSITY_MEDIUM:
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
			//hdpi(240)
			case DisplayMetrics.DENSITY_HIGH:
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
			case DisplayMetrics.DENSITY_XHIGH:
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
			case 480 :
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
			default:
		        adViewPager.setLayoutParams(new LayoutParams(dm.widthPixels, dm.heightPixels / 4));
				break;
		}
    	pagerLayout.addView(adViewPager);
    	initPageAdapter();
    	initCirclePoint();
    	adViewPager.setAdapter(adPageAdapter);
    	adViewPager.setOnPageChangeListener(new AdPageChangeListener());
    	new Thread(new Runnable() {
            @Override  
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(atomicInteger.get());  
                        atomicOption();  
                    }  
                }  
            }  
        }).start(); 
		//增加滑动栏 add 2015-10-19 end
	}
	
	private void initPageAdapter() {
		pageViews = new ArrayList<View>();
		ImageView img1 = new ImageView(this);
        img1.setBackgroundResource(R.drawable.ad1);  
        pageViews.add(img1);
        ImageView img2 = new ImageView(this);
        img2.setBackgroundResource(R.drawable.ad2);  
        pageViews.add(img2);
        ImageView img3 = new ImageView(this);
        img3.setBackgroundResource(R.drawable.ad3);  
        pageViews.add(img3); 
        adPageAdapter = new AdPageAdapter(pageViews);
	}
	
	private final class AdPageAdapter extends PagerAdapter {
        private List<View> views = null;  
        public AdPageAdapter(List<View> views) {  
            this.views = views;  
        }  
        @Override  
        public void destroyItem(View container, int position, Object object) {  
            ((ViewPager) container).removeView(views.get(position));  
        }  
        @Override  
        public int getCount() {  
            return views.size();  
        }  
        @Override  
        public Object instantiateItem(View container, int position) {  
            ((ViewPager) container).addView(views.get(position), 0);  
            return views.get(position);  
        }
        @Override  
        public boolean isViewFromObject(View view, Object object) {  
            return view == object;  
        }  
    }
	
	private void initCirclePoint(){
		View first = mInflater.inflate(R.layout.main_tab_01, null);
		ViewGroup group = (ViewGroup) first.findViewById(R.id.viewGroup); 
        imageViews = new ImageView[pageViews.size()];  
        for (int i = 0; i < pageViews.size(); i++) {
        	imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(10,10));  
            imageViews[i] = imageView;  
            
            if (i == 0) {
                imageViews[i].setBackgroundResource(R.drawable.point_focused);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.point_unfocused);  
            }
            group.addView(imageViews[i]);  
        }
	}
	
	private final class AdPageChangeListener implements OnPageChangeListener {
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
        }  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {}  
        @Override  
        public void onPageSelected(int arg0) {
            atomicInteger.getAndSet(arg0);  
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.point_focused);  
                if (arg0 != i) {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.point_unfocused);  
                }  
            }  
        }  
    }
	
	private final Handler viewHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
        	adViewPager.setCurrentItem(msg.what);  
            super.handleMessage(msg);  
        }  
    }; 
    private void atomicOption() {  
        atomicInteger.incrementAndGet();  
        if (atomicInteger.get() > imageViews.length - 1) {  
        	atomicInteger.getAndAdd(-imageViews.length);  
        }  
        try {  
            Thread.sleep(3000);
        } catch (InterruptedException e) {  
              
        }  
    }
	protected void resetTabBtn(){
		anycareTV.setTextColor(getResources().getColor(R.color.txt_grey));
		webmobileTV.setTextColor(getResources().getColor(R.color.txt_grey));
	}
	
	private class MyOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.anycareTV:
					mViewPager.setCurrentItem(0);
					break;
				case R.id.webmobileTV:
					mViewPager.setCurrentItem(1);
					break;
				case R.id.dianjichaxun:
					if(crutchDeviceNumber!=null&&!"".equals(crutchDeviceNumber)||beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
//						if(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId)){
							Bundle bundle = new Bundle();
							bundle.putString("archivesId",archivesId);
							bundle.putString("crutchDeviceNumber", crutchDeviceNumber);
							bundle.putString("beltDeviceNumber", beltDeviceNumber);
							bundle.putString("longitude",longitude);
							bundle.putString("latitude",latitude);
							bundle.putString("address",address);
							bundle.putString("yuanBanJing",yuanBanJing);
							bundle.putString("yuanLat",yuanLat);
							bundle.putString("yuanLon",yuanLon);
							bundle.putString("crutchId",crutchId);
							bundle.putString("beltId",beltId);
							bundle.putString("serviceTime",serviceTime);
							Intent intent = new Intent(AnyCareMainActivity.this,AnyCareMapActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
//						}else{
//							Toast.makeText(getApplicationContext(), "设备未发送数据！", Toast.LENGTH_SHORT).show();
//						}
					}else{
						Toast.makeText(getApplicationContext(), "请绑定设备！", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.qiuzhu:
					if(crutchDeviceNumber!=null&&!"".equals(crutchDeviceNumber)||beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
						if(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId)){
							if("1".equals(biaoZhi)){
								Toast.makeText(getApplicationContext(), "请点击定位查询\n"+nameTV.getText()+"在"+serviceTime+"发生求助", Toast.LENGTH_SHORT).show();
							}else{
								Intent intent = new Intent(AnyCareMainActivity.this,WarningActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("archivesId",archivesId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}else{
							Toast.makeText(getApplicationContext(), "设备未发送数据！", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请绑定设备！", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.huodongliang:
					if(beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
						Bundle bundle = new Bundle();
						bundle.putString("beltDeviceNumber", beltDeviceNumber);
						Intent intent = new Intent(AnyCareMainActivity.this,HuoDongLiangActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}else{
						Toast.makeText(getApplicationContext(), "请绑定腰带设备！", Toast.LENGTH_SHORT).show();
					}
				break;
				case R.id.zuotouxiangtu:
					//设置位置
					//设置在屏幕左侧的显示位置
					popWindow.showAtLocation(mainlayout, Gravity.NO_GRAVITY, 0, 0);
					break;
				case R.id.zhuyeshezhi:
					startActivity(new Intent(AnyCareMainActivity.this,AnyCareSetActivity.class));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
					break;
				case R.id.zhuyetuichu:
					if(popWindow.isShowing()){
						popWindow.dismiss();
					}
					startActivity(new Intent(AnyCareMainActivity.this,AnyCareLoginActivity.class));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
					break;
				case R.id.jiahao:
					startActivity(new Intent(getApplication(), AddArchByIdActivity.class));
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
					break;
				case R.id.youbiaogetu:
					if(archivesId!=null&&!"".equals(archivesId)){
						Bundle bundle = new Bundle();
						bundle.putString("archivesId",archivesId);
						bundle.putString("createSign",createSign);
						Intent intent = new Intent(AnyCareMainActivity.this,AnyCareBindActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}else{
						Toast.makeText(getApplicationContext(), "请先创建个人信息！", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	}
	
	WebViewClient wvc = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			// 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
			view.loadUrl(url);
			// 记得消耗掉这个事件。给不知道的朋友再解释一下，Android中返回True的意思就是到此为止吧,
			//事件就会不会冒泡传递了，我们称之为消耗掉 
			return true;
		}
	};
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class loadAllDataByUserIdAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("userId", readUserId());
				userInforMap.put("stime", stime);
				String jsonResult = HttpUtils.doPost("/anyCare/allTypeQueryByUserId.action", userInforMap);
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
			try{
				dataList.clear();
				telephoneTV.setText(readUserPhone());
				if(result!=null&&!"".equals(result)){
					JSONObject jsonObject = new JSONObject(result);
					int resultStatus = jsonObject.getInt("status");
					if(resultStatus==200){
						JSONArray resultArray = new JSONArray(jsonObject.getString("data"));
						int resultArrayLen = resultArray.length();
						if(page>=resultArrayLen){
							page = resultArrayLen-1;
						}
						for(int i = 0 ;i<resultArrayLen;i++){
							JSONObject resultObject = resultArray.getJSONObject(i);
							JSONObject anyCareArchivesEntity = resultObject.getJSONObject("anyCareArchivesEntity");
							JSONObject allDeviceCollectEntity = resultObject.getJSONObject("allDeviceCollectEntity");
							JSONArray anyCareDeviceEntityList = resultObject.getJSONArray("anyCareDeviceEntityList");
							JSONArray xingZouJingZhiList = resultObject.getJSONArray("xingZouJingZhiList");
							Map<String,String> map = new HashMap<String, String>();
							map.put("fullName", anyCareArchivesEntity.getString("fullName"));
							map.put("nickName", anyCareArchivesEntity.getString("nickName"));
							map.put("archivesCode", anyCareArchivesEntity.getString("archivesCode"));
							map.put("archivesId", anyCareArchivesEntity.getString("archivesId"));
							map.put("yuanBanJing", anyCareArchivesEntity.getString("yuanBanJing"));
							map.put("yuanLat", anyCareArchivesEntity.getString("yuanLat"));
							map.put("yuanLon", anyCareArchivesEntity.getString("yuanLon"));
							map.put("createSign", anyCareArchivesEntity.getString("createSign"));
							map.put("weatherStatus", allDeviceCollectEntity.getString("weatherStatus1"));
							map.put("temperature1", allDeviceCollectEntity.getString("weatherTemperature1"));
							map.put("temperature2", allDeviceCollectEntity.getString("weatherTemperature2"));
							map.put("city", allDeviceCollectEntity.getString("city"));
							map.put("crutchDeviceNumber", allDeviceCollectEntity.getString("crutchDeviceNumber"));
							map.put("beltDeviceNumber", allDeviceCollectEntity.getString("beltDeviceNumber"));
							map.put("longitude", allDeviceCollectEntity.getString("longitude"));
							map.put("latitude", allDeviceCollectEntity.getString("latitude"));
							map.put("address", allDeviceCollectEntity.getString("address"));
							map.put("serviceTime", allDeviceCollectEntity.getString("serviceTime"));
							map.put("biaoZhi", allDeviceCollectEntity.getString("biaoZhi"));
							map.put("crutchDeviceId", "");
							map.put("beltDeviceId", "");
							map.put("crutchId",allDeviceCollectEntity.getString("crutchCollectId"));
							map.put("beltId",allDeviceCollectEntity.getString("beltId"));
							if(anyCareDeviceEntityList != null){
								for(int a = 0 ;a<anyCareDeviceEntityList.length();a++){
									JSONObject anyCareDeviceEntity = anyCareDeviceEntityList.getJSONObject(a);
									String deviceType = anyCareDeviceEntity.getString("deviceType");
									String deviceId = anyCareDeviceEntity.getString("deviceId");
									//拐棍
									if("1".equals(deviceType)){
										map.put("crutchDeviceId", deviceId);
									//腰带
									}else if("2".equals(deviceType)){
										map.put("beltDeviceId", deviceId);
									}
								}
							}
							//处理行走静止活动量数据到数据库中
							if(xingZouJingZhiList != null){
								List<XingZouJingZhiEntity> xzjzList = new ArrayList<XingZouJingZhiEntity>();
								for(int a = 0 ;a<xingZouJingZhiList.length();a++){
									JSONObject xingZouJingZhiEntity = xingZouJingZhiList.getJSONObject(a);
									XingZouJingZhiEntity xzjz = new XingZouJingZhiEntity();
									String dn = xingZouJingZhiEntity.getString("deviceNumber");
									xzjz.setDeviceNumber(dn);
									xzjz.setHuoDongLiang(xingZouJingZhiEntity.getString("huoDongLiang"));
									xzjz.setJingZhi(xingZouJingZhiEntity.getString("jingZhi"));
									xzjz.setBuShu(xingZouJingZhiEntity.getString("buShu"));
									String time = xingZouJingZhiEntity.getString("time");
									xzjz.setTime(time);
									//判断开始日期跟同步日期一致,查找同步日期数据
									XingZouJingZhiEntity xzjzEntity = dataBaseManager.query(dn, time);
									if(xzjzEntity !=null){
										dataBaseManager.delete(xzjz);
									}
									xzjzList.add(xzjz);
								}
								dataBaseManager.add(xzjzList);
								dataBaseManager.updateParam("1", DateTool.getDateType2());
							}
							dataList.add(map);
							if(i == page){
								archivesId = anyCareArchivesEntity.getString("archivesId");
								nameTV.setText(anyCareArchivesEntity.getString("fullName"));
								idhaoTV.setText("ID:"+anyCareArchivesEntity.getString("archivesCode"));
								yuanBanJing = anyCareArchivesEntity.getString("yuanBanJing");
								yuanLat = anyCareArchivesEntity.getString("yuanLat");
								yuanLon = anyCareArchivesEntity.getString("yuanLon");
								createSign = anyCareArchivesEntity.getString("createSign");
								if(allDeviceCollectEntity != null){
									String status = allDeviceCollectEntity.getString("weatherStatus1");
									String temperature1 = allDeviceCollectEntity.getString("weatherTemperature1");
									String temperature2 = allDeviceCollectEntity.getString("weatherTemperature2");
									String city = allDeviceCollectEntity.getString("city");
									crutchDeviceNumber = allDeviceCollectEntity.getString("crutchDeviceNumber");
									beltDeviceNumber = allDeviceCollectEntity.getString("beltDeviceNumber");
									longitude = allDeviceCollectEntity.getString("longitude");
									latitude = allDeviceCollectEntity.getString("latitude");
									address = allDeviceCollectEntity.getString("address");
									serviceTime = allDeviceCollectEntity.getString("serviceTime");
									biaoZhi = allDeviceCollectEntity.getString("biaoZhi");
									crutchDeviceId = map.get("crutchDeviceId");
									beltDeviceId = map.get("beltDeviceId");
									crutchId= map.get("crutchId");
									beltId= map.get("beltId");
									if(crutchDeviceNumber!=null&&!"".equals(crutchDeviceNumber)||beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
										if(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId)){
											diquTV.setText("所在城市:"+city);
											if("1".equals(biaoZhi)){
												qiujiuBtn.setText("求助告警");
											}else{
												qiujiuBtn.setText("求助历史");
											}
										}else{
											diquTV.setText("已绑定无数据");
											qiujiuBtn.setText("无数据");
										}
									}else{
										diquTV.setText("设备未绑定");
										qiujiuBtn.setText("未绑定");
									}
									wenduTV.setText(("".equals(temperature1)?"--":temperature1)+"℃/"+("".equals(temperature2)?"--":temperature2)+"℃");
									if ("晴".equals(status)) {
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather00));
									}else if("多云".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather01));
									}else if("阴".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather02));
									}else if("阵雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather03));
									}else if("雷阵雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather04));
									}else if("雷阵雨伴有冰雹".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather05));
									}else if("雨夹雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather06));
									}else if("小雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather07));
									}else if("中雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather08));
									}else if("大雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather09));
									}else if("暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather10));
									}else if("大暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather11));
									}else if("特大暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather12));
									}else if("阵雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather13));
									}else if("小雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather14));
									}else if("中雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather15));
									}else if("大雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather16));
									}else if("暴雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather17));
									}else if("雾".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather18));
									}else if("冻雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather19));
									}else if("沙尘暴".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather20));
									}else if("小到中雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather21));
									}else if("中到大雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather22));
									}else if("大到暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather23));
									}else if("暴雨到大暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather24));
									}else if("大暴雨到特大暴雨".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather25));
									}else if("小到中雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather26));
									}else if("中到大雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather27));
									}else if("大到暴雪".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather28));
									}else if("浮尘".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather29));
									}else if("扬沙".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather30));
									}else if("强沙尘暴 ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather31));
									}else if("霾 ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather53));
									}
								}
							}
						}
					}else{
						archivesId = "";
						yuanBanJing = "";
						yuanLat = "";
						yuanLon = "";
						nameTV.setText("无");
						idhaoTV.setText("ID:无");
						crutchDeviceNumber = "";
						longitude = "";
						latitude = "";
						address = "";
						serviceTime = "";
						biaoZhi = "";
						qiujiuBtn.setText("未绑定");
						diquTV.setText("设备未绑定");
						wenduTV.setText("--℃/--℃");
					}
					if(myAdapter==null){
						myAdapter=new MyAdapter();
						bind_listview.setAdapter(myAdapter);
						bind_listview.setOnItemClickListener(new MyItemClickListener());
					}else{
						myAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();//dialog关闭，数据处理完毕
				}else{
					dialog.dismiss();//dialog关闭，数据处理完毕
				}
			}catch(Exception e){
				e.printStackTrace();
				
			}
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
		public Object getItem(int arg0) {
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
			convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bindarchives_list_style,null);
			TextView bindnickname= (TextView)convertView.findViewById(R.id.bindnickname_textview);
			TextView devicenumber_textview= (TextView)convertView.findViewById(R.id.devicenumber_textview);
			Button maintain_button=(Button)convertView.findViewById(R.id.maintain_button);
			Button delete_button=(Button)convertView.findViewById(R.id.delete_button);
			final Map<String,String> map=(Map<String,String>)dataList.get(position);
			bindnickname.setText(map.get("fullName"));
			devicenumber_textview.setText(map.get("archivesCode"));
			maintain_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putString("archivesId",map.get("archivesId"));
					Intent intent = new Intent(AnyCareMainActivity.this,AddArchivesFirstActivity.class);;
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			});
			delete_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareMainActivity.this);
		    		builder
		    			.setTitle("确认")
		    			.setMessage("确认删除该人员信息?")
		    			.setPositiveButton("是", new DialogInterface.OnClickListener() {
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					// TODO Auto-generated method stub
		    					dialog.cancel();
		    					new UserDeleteArchivesAsyncTask().execute(new String[]{readUserId(),map.get("archivesId")});
		    				}
		    			})
		    			.setNegativeButton("否", new DialogInterface.OnClickListener(){
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					// TODO Auto-generated method stub
		    					dialog.cancel(); 
		    				}
		    			});
		    		builder.create().show();
				}
			});
			return convertView;
		}
	}
	
	private class MyItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			// TODO Auto-generated method stub
			Map<String,String> bindDeviceInforMap=(Map<String,String>)dataList.get(position);
			page = position;
			String archivesCode= bindDeviceInforMap.get("archivesCode");
			archivesId = bindDeviceInforMap.get("archivesId");
			String fullName = bindDeviceInforMap.get("fullName");
			yuanBanJing = bindDeviceInforMap.get("yuanBanJing");
			yuanLat = bindDeviceInforMap.get("yuanLat");
			yuanLon = bindDeviceInforMap.get("yuanLon");
			crutchDeviceNumber = bindDeviceInforMap.get("crutchDeviceNumber");
			beltDeviceNumber = bindDeviceInforMap.get("beltDeviceNumber");
			longitude = bindDeviceInforMap.get("longitude");
			latitude = bindDeviceInforMap.get("latitude");
			address = bindDeviceInforMap.get("address");
			serviceTime = bindDeviceInforMap.get("serviceTime");
			biaoZhi = bindDeviceInforMap.get("biaoZhi");
			createSign = bindDeviceInforMap.get("createSign");
			
			String status = bindDeviceInforMap.get("weatherStatus");
			String temperature1 = bindDeviceInforMap.get("temperature1");
			String temperature2 = bindDeviceInforMap.get("temperature2");
			String city = bindDeviceInforMap.get("city");
			nameTV.setText(fullName);
			idhaoTV.setText("ID:"+archivesCode);
			crutchDeviceId = bindDeviceInforMap.get("crutchDeviceId");
			beltDeviceId = bindDeviceInforMap.get("beltDeviceId");
			crutchId= bindDeviceInforMap.get("crutchId");
			beltId= bindDeviceInforMap.get("beltId");
			if(crutchDeviceNumber!=null&&!"".equals(crutchDeviceNumber)||beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
				if(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId)){
					diquTV.setText("所在城市:"+city);
					if("1".equals(biaoZhi)){
						qiujiuBtn.setText("求助告警");
					}else{
						qiujiuBtn.setText("求助历史");
					}
				}else{
					diquTV.setText("已绑定无数据");
					qiujiuBtn.setText("无数据");
				}
			}else{
				diquTV.setText("设备未绑定");
				qiujiuBtn.setText("未绑定");
			}
			wenduTV.setText(("".equals(temperature1)?"--":temperature1)+"℃/"+("".equals(temperature2)?"--":temperature2)+"℃");
			if ("晴".equals(status)) {
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather00));
			}else if("多云".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather01));
			}else if("阴".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather02));
			}else if("阵雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather03));
			}else if("雷阵雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather04));
			}else if("雷阵雨伴有冰雹".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather05));
			}else if("雨夹雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather06));
			}else if("小雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather07));
			}else if("中雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather08));
			}else if("大雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather09));
			}else if("暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather10));
			}else if("大暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather11));
			}else if("特大暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather12));
			}else if("阵雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather13));
			}else if("小雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather14));
			}else if("中雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather15));
			}else if("大雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather16));
			}else if("暴雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather17));
			}else if("雾".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather18));
			}else if("冻雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather19));
			}else if("沙尘暴".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather20));
			}else if("小到中雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather21));
			}else if("中到大雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather22));
			}else if("大到暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather23));
			}else if("暴雨到大暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather24));
			}else if("大暴雨到特大暴雨".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather25));
			}else if("小到中雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather26));
			}else if("中到大雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather27));
			}else if("大到暴雪".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather28));
			}else if("浮尘".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather29));
			}else if("扬沙".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather30));
			}else if("强沙尘暴 ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather31));
			}else if("霾 ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather53));
			}
			popWindow.dismiss();
		}
	}

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
		}
		return false;
	}
	
	/** 
	 * author:ma_yming
	 * dis:显示dialog 
	 * */
	private void dialog() {
		if(mViewPager.getCurrentItem()==1){
			mViewPager.setCurrentItem(0);
			return;
		}else{
			saveUserPage(page);
			finish();
		}
	}
	
	
	/**
	 * dis：AsyncTask参数类型：
	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
	 * 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class UserDeleteArchivesAsyncTask extends AsyncTask<String, String, String>{
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
				userInforMap.put("userId", params[0]);
				userInforMap.put("archivesId", params[1]);
				String jsonResult = HttpUtils.doPost("/anyCare/abandonArchives.action", userInforMap);
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
				Toast.makeText(getApplicationContext(), "人员信息删除成功！", Toast.LENGTH_SHORT).show();
				new loadAllDataByUserIdAsyncTask().execute();
			}else if(result!=null&&!"".equals(result)&&"false".equals(result)){
				Toast.makeText(getApplicationContext(), "人员信息删除失败！", Toast.LENGTH_SHORT).show();
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "操作失败，请重试！", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog关闭，数据处理完毕
		}
	}
	
	
	/*
	 * 获取当前程序的版本号
	 */
	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		return packInfo.versionName;
	}
	
	/*
	 * 从服务器获取xml解析并进行比对版本号
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				// 从资源文件获取服务器 地址
				String path = getResources().getString(R.string.url_server);
				// 包装成url的对象
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				InputStream is = conn.getInputStream();
				info = UpdataInfoParser.getUpdataInfo(is);
				System.out.println("VersionActivity            ----------->          info = "+ info);
				if (info.getVersion().equals(localVersion)) {
					Message msg = new Message();
					msg.what = UPDATA_NONEED;
					handler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
//				Toast.makeText(getApplicationContext(), "版本号相同无需升级",Toast.LENGTH_SHORT).show();
				break;
			case UPDATA_CLIENT:
				// 对话框通知用户升级程序
				// Toast.makeText(getApplicationContext(), "可以升级程序啦~",1).show();
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1).show();
				// LoginMain();
				break;
			case SDCARD_NOMOUNTED:
				// sdcard不可用
				Toast.makeText(getApplicationContext(), "SD卡不可用",1).show();
				break;
			case DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(getApplicationContext(), "下载新版本失败", 1).show();
				// LoginMain();
				break;
			}
		}
	};
	
	
	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 * 
	 * 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性, 对话框的内容,样式,按钮
	 * 3.通过builder 创建一个对话框 4.对话框show()出来
	 */
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage(info.getDescription());
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "下载apk,更新");
				downLoadApk();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// LoginMain();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}
	
	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(AnyCareMainActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Message msg = new Message();
			msg.what = SDCARD_NOMOUNTED;
			handler.sendMessage(msg);
		} else {
			pd.show();
			new Thread() {
				@Override
				public void run() {
					try {
						File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
						sleep(1000);
						installApk(file);
						pd.dismiss(); // 结束掉进度条对话框

					} catch (Exception e) {
						Message msg = new Message();
						msg.what = DOWN_ERROR;
						handler.sendMessage(msg);
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
	}
	
	
	private void saveUserPage(int page){
		//获取SharedPreferences对象，路径在/data/data/cn.itcast.preferences/shared_pref/paramater.xml
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//获取编辑器
		Editor editor=sp.edit();
		//通过editor进行设置
		editor.putInt("page", page);
		//提交修改，将数据写到文件
		editor.commit();
	}
	
	private int readUserPage(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//若没有数据，返回默认值""
		int page=sp.getInt("page", 0);
		return page;
	}
}
