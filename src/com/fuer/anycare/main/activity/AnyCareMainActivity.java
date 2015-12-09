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
	 * ViewPager��������
	 */
	private PagerAdapter mAdapter;
	private List<View> mViews;
	private LayoutInflater mInflater;
	
	private int currentIndex;
	/**
	 * �ײ�����textview
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
	private List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();//������ݵ�List
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
	private String crutchDeviceId;//��Ӧ�����豸���е�id
	private String beltDeviceId;//��Ӧ�����豸���е�id
	
	private String crutchId;//��Ӧ�����ռ����������豸���ݵ�Id
	private String beltId;//��Ӧ�����ռ����������豸���ݵ�Id
	
	private String serviceTime;//��ʾ����������ʱ��
	private String biaoZhi;//1Ϊsos��2λ�����ѯ
	
	
	private Button showMapBtn;
	private Button qiujiuBtn;
	private Button huodongliangBtn;
	private LinearLayout pagerLayout;//������� add 2015-10-19
	private ViewPager adViewPager;//������� add 2015-10-19
	private List<View> pageViews;//������� add 2015-10-19
	private AdPageAdapter adPageAdapter;//������� add 2015-10-19
	private boolean isContinue = true;//������� add 2015-10-19
	private ImageView imageView;//������� add 2015-10-19
	private ImageView[] imageViews;//������� add 2015-10-19
	private AtomicInteger atomicInteger = new AtomicInteger(0);//������� add 2015-10-19
	
	private LoadingProgressDialog dialog;
	/**
     * ���������汾�������ò���
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
	private String createSign;//������Ϣ������־1��ʶ�Լ�������0��ʾ�����Լ��˺Ŵ���
	
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
		 * ��ʼ��View
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
					//��������Ļ������ʾλ��
					 popWindow.showAtLocation(mainlayout, Gravity.NO_GRAVITY, 0, 0);
				}
			}
		});
		
		//��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
//		new loadAllDataByUserIdAsyncTask().execute();
		//�Զ����汾��俪ʼ
		try{
			localVersion = getVersionName();
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		//�Զ����汾������
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

	private void initView(){
		mViews = new ArrayList<View>();
		View first = mInflater.inflate(R.layout.main_tab_01, null);
		View second = mInflater.inflate(R.layout.main_tab_02, null);
		//�����ѯ��ͼbtn
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
		//webview����
		WebView myWebView = (WebView)second.findViewById(R.id.my_webview);
		//webview��������֧��Javascript
		myWebView.getSettings().setJavaScriptEnabled(true);
		//requestFocus();
		//�����Ƿ�֧�����ţ�����Ϊfalse��Ĭ��Ϊtrue
		myWebView.getSettings().setSupportZoom(true);
		//�����Ƿ���ʾ���Ź��ߣ�Ĭ��Ϊfalse��
		myWebView.getSettings().setBuiltInZoomControls(false);
		//һ����ٻ��õ��������WebView�����ʾ��ͨ��ҳʱһ�����ֺ���������������ᵼ��ҳ��鿴�����ǳ������㡣
		//LayoutAlgorithm��һ��ö�٣���������html�Ĳ��֣��ܹ����������ͣ�
		//NORMAL��������ʾ��û����Ⱦ�仯��
		//SINGLE_COLUMN�����������ݷŵ�WebView����ȿ��һ���С�
		//NARROW_COLUMNS�����ܵĻ���ʹ�����еĿ�Ȳ�������Ļ��ȡ�
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
		// װ��R.layout.popup��Ӧ�Ľ��沼��
		popView = this.getLayoutInflater().inflate(R.layout.layout_pop, null);
		popView.setFocusable(true); // �������Ҫ
		popView.setFocusableInTouchMode(true);
		popsetTV = (TextView) popView.findViewById(R.id.zhuyeshezhi);
		popexitTV = (TextView) popView.findViewById(R.id.zhuyetuichu);
		addArchBtn  = (Button) popView.findViewById(R.id.jiahao);
		telephoneTV = (TextView) popView.findViewById(R.id.telephone);
		bind_listview =  (ListView) popView.findViewById(R.id.bind_listview);
		popsetTV.setOnClickListener(new MyOnClickListener());
		popexitTV.setOnClickListener(new MyOnClickListener());
		addArchBtn.setOnClickListener(new MyOnClickListener());
		
		//��ȡ��Ļdip����
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
		popWindow = new PopupWindow(popView, 0, 0, true);
		popWindow.setWidth(dm.widthPixels*4/5);
		popWindow.setHeight(LayoutParams.FILL_PARENT);
		popWindow.setAnimationStyle(R.style.animationInOut);
		popWindow.setTouchable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable()); 
		//��ӷ��ذ�ť�¼�
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
		//���ӻ����� add 2015-10-19 start
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
		//���ӻ����� add 2015-10-19 end
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
//							Toast.makeText(getApplicationContext(), "�豸δ�������ݣ�", Toast.LENGTH_SHORT).show();
//						}
					}else{
						Toast.makeText(getApplicationContext(), "����豸��", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.qiuzhu:
					if(crutchDeviceNumber!=null&&!"".equals(crutchDeviceNumber)||beltDeviceNumber!=null&&!"".equals(beltDeviceNumber)){
						if(crutchId!=null&&!"".equals(crutchId)||beltId!=null&&!"".equals(beltId)){
							if("1".equals(biaoZhi)){
								Toast.makeText(getApplicationContext(), "������λ��ѯ\n"+nameTV.getText()+"��"+serviceTime+"��������", Toast.LENGTH_SHORT).show();
							}else{
								Intent intent = new Intent(AnyCareMainActivity.this,WarningActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("archivesId",archivesId);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						}else{
							Toast.makeText(getApplicationContext(), "�豸δ�������ݣ�", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "����豸��", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getApplicationContext(), "��������豸��", Toast.LENGTH_SHORT).show();
					}
				break;
				case R.id.zuotouxiangtu:
					//����λ��
					//��������Ļ������ʾλ��
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
						Toast.makeText(getApplicationContext(), "���ȴ���������Ϣ��", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	}
	
	WebViewClient wvc = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			// ʹ���Լ���WebView�������ӦUrl�����¼���������ʹ��Ĭ�������������ҳ��
			view.loadUrl(url);
			// �ǵ����ĵ�����¼�������֪���������ٽ���һ�£�Android�з���True����˼���ǵ���Ϊֹ��,
			//�¼��ͻ᲻��ð�ݴ����ˣ����ǳ�֮Ϊ���ĵ� 
			return true;
		}
	};
	
	/**
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class loadAllDataByUserIdAsyncTask extends AsyncTask<String, String, String>{
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
		
		//���ݴ�����Ϻ����UI����
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
									//�չ�
									if("1".equals(deviceType)){
										map.put("crutchDeviceId", deviceId);
									//����
									}else if("2".equals(deviceType)){
										map.put("beltDeviceId", deviceId);
									}
								}
							}
							//�������߾�ֹ������ݵ����ݿ���
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
									//�жϿ�ʼ���ڸ�ͬ������һ��,����ͬ����������
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
											diquTV.setText("���ڳ���:"+city);
											if("1".equals(biaoZhi)){
												qiujiuBtn.setText("�����澯");
											}else{
												qiujiuBtn.setText("������ʷ");
											}
										}else{
											diquTV.setText("�Ѱ�������");
											qiujiuBtn.setText("������");
										}
									}else{
										diquTV.setText("�豸δ��");
										qiujiuBtn.setText("δ��");
									}
									wenduTV.setText(("".equals(temperature1)?"--":temperature1)+"��/"+("".equals(temperature2)?"--":temperature2)+"��");
									if ("��".equals(status)) {
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather00));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather01));
									}else if("��".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather02));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather03));
									}else if("������".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather04));
									}else if("��������б���".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather05));
									}else if("���ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather06));
									}else if("С��".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather07));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather08));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather09));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather10));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather11));
									}else if("�ش���".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather12));
									}else if("��ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather13));
									}else if("Сѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather14));
									}else if("��ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather15));
									}else if("��ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather16));
									}else if("��ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather17));
									}else if("��".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather18));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather19));
									}else if("ɳ����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather20));
									}else if("С������".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather21));
									}else if("�е�����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather22));
									}else if("�󵽱���".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather23));
									}else if("���굽����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather24));
									}else if("���굽�ش���".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather25));
									}else if("С����ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather26));
									}else if("�е���ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather27));
									}else if("�󵽱�ѩ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather28));
									}else if("����".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather29));
									}else if("��ɳ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather30));
									}else if("ǿɳ���� ".equals(status)){
										tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather31));
									}else if("�� ".equals(status)){
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
						nameTV.setText("��");
						idhaoTV.setText("ID:��");
						crutchDeviceNumber = "";
						longitude = "";
						latitude = "";
						address = "";
						serviceTime = "";
						biaoZhi = "";
						qiujiuBtn.setText("δ��");
						diquTV.setText("�豸δ��");
						wenduTV.setText("--��/--��");
					}
					if(myAdapter==null){
						myAdapter=new MyAdapter();
						bind_listview.setAdapter(myAdapter);
						bind_listview.setOnItemClickListener(new MyItemClickListener());
					}else{
						myAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();//dialog�رգ����ݴ������
				}else{
					dialog.dismiss();//dialog�رգ����ݴ������
				}
			}catch(Exception e){
				e.printStackTrace();
				
			}
		}
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
		    			.setTitle("ȷ��")
		    			.setMessage("ȷ��ɾ������Ա��Ϣ?")
		    			.setPositiveButton("��", new DialogInterface.OnClickListener() {
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					// TODO Auto-generated method stub
		    					dialog.cancel();
		    					new UserDeleteArchivesAsyncTask().execute(new String[]{readUserId(),map.get("archivesId")});
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
					diquTV.setText("���ڳ���:"+city);
					if("1".equals(biaoZhi)){
						qiujiuBtn.setText("�����澯");
					}else{
						qiujiuBtn.setText("������ʷ");
					}
				}else{
					diquTV.setText("�Ѱ�������");
					qiujiuBtn.setText("������");
				}
			}else{
				diquTV.setText("�豸δ��");
				qiujiuBtn.setText("δ��");
			}
			wenduTV.setText(("".equals(temperature1)?"--":temperature1)+"��/"+("".equals(temperature2)?"--":temperature2)+"��");
			if ("��".equals(status)) {
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather00));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather01));
			}else if("��".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather02));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather03));
			}else if("������".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather04));
			}else if("��������б���".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather05));
			}else if("���ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather06));
			}else if("С��".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather07));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather08));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather09));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather10));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather11));
			}else if("�ش���".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather12));
			}else if("��ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather13));
			}else if("Сѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather14));
			}else if("��ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather15));
			}else if("��ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather16));
			}else if("��ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather17));
			}else if("��".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather18));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather19));
			}else if("ɳ����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather20));
			}else if("С������".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather21));
			}else if("�е�����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather22));
			}else if("�󵽱���".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather23));
			}else if("���굽����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather24));
			}else if("���굽�ش���".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather25));
			}else if("С����ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather26));
			}else if("�е���ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather27));
			}else if("�󵽱�ѩ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather28));
			}else if("����".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather29));
			}else if("��ɳ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather30));
			}else if("ǿɳ���� ".equals(status)){
				tianqituIM.setBackgroundDrawable(getResources().getDrawable(R.drawable.weather31));
			}else if("�� ".equals(status)){
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
	 * dis:��ʾdialog 
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
	 * dis��AsyncTask�������ͣ�
	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
	 * �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class UserDeleteArchivesAsyncTask extends AsyncTask<String, String, String>{
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
		
		//���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dataList.clear();
			if(result!=null&&!"".equals(result)&&"true".equals(result)){
				Toast.makeText(getApplicationContext(), "��Ա��Ϣɾ���ɹ���", Toast.LENGTH_SHORT).show();
				new loadAllDataByUserIdAsyncTask().execute();
			}else if(result!=null&&!"".equals(result)&&"false".equals(result)){
				Toast.makeText(getApplicationContext(), "��Ա��Ϣɾ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}else if("".equals(result)){
				Toast.makeText(getApplicationContext(), "����ʧ�ܣ������ԣ�", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();//dialog�رգ����ݴ������
		}
	}
	
	
	/*
	 * ��ȡ��ǰ����İ汾��
	 */
	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		return packInfo.versionName;
	}
	
	/*
	 * �ӷ�������ȡxml���������бȶ԰汾��
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				// ����Դ�ļ���ȡ������ ��ַ
				String path = getResources().getString(R.string.url_server);
				// ��װ��url�Ķ���
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
				// ������
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
//				Toast.makeText(getApplicationContext(), "�汾����ͬ��������",Toast.LENGTH_SHORT).show();
				break;
			case UPDATA_CLIENT:
				// �Ի���֪ͨ�û���������
				// Toast.makeText(getApplicationContext(), "��������������~",1).show();
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				// ��������ʱ
				Toast.makeText(getApplicationContext(), "��ȡ������������Ϣʧ��", 1).show();
				// LoginMain();
				break;
			case SDCARD_NOMOUNTED:
				// sdcard������
				Toast.makeText(getApplicationContext(), "SD��������",1).show();
				break;
			case DOWN_ERROR:
				// ����apkʧ��
				Toast.makeText(getApplicationContext(), "�����°汾ʧ��", 1).show();
				// LoginMain();
				break;
			}
		}
	};
	
	
	/*
	 * 
	 * �����Ի���֪ͨ�û����³���
	 * 
	 * �����Ի���Ĳ��裺 1.����alertDialog��builder. 2.Ҫ��builder��������, �Ի��������,��ʽ,��ť
	 * 3.ͨ��builder ����һ���Ի��� 4.�Ի���show()����
	 */
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("�汾����");
		builer.setMessage(info.getDescription());
		// ����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ
		builer.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "����apk,����");
				downLoadApk();
			}
		});
		// ����ȡ����ťʱ���е�¼
		builer.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// LoginMain();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}
	
	/*
	 * �ӷ�����������APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // �������Ի���
		pd = new ProgressDialog(AnyCareMainActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("�������ظ���");
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
						pd.dismiss(); // �������������Ի���

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
	
	// ��װapk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// ִ�ж���
		intent.setAction(Intent.ACTION_VIEW);
		// ִ�е���������
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
	}
	
	
	private void saveUserPage(int page){
		//��ȡSharedPreferences����·����/data/data/cn.itcast.preferences/shared_pref/paramater.xml
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��ȡ�༭��
		Editor editor=sp.edit();
		//ͨ��editor��������
		editor.putInt("page", page);
		//�ύ�޸ģ�������д���ļ�
		editor.commit();
	}
	
	private int readUserPage(){
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//��û�����ݣ�����Ĭ��ֵ""
		int page=sp.getInt("page", 0);
		return page;
	}
}
