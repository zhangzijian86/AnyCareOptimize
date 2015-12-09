package com.fuer.anycare.login.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.fuer.anycare.common.application.MyApplication;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.anycare.common.utils.NetUtils;
import com.fuer.anycare.forget.activity.AnyCareForgetActivity;
import com.fuer.anycare.main.activity.AnyCareMainActivity;
import com.fuer.anycare.register.activity.AnyCareRegister1Activity;
import com.fuer.anycare.version.utils.DownLoadManager;
import com.fuer.anycare.version.utils.UpdataInfo;
import com.fuer.anycare.version.utils.UpdataInfoParser;
import com.fuer.main.anycare.R;

public class AnyCareLoginActivity extends Activity {
	
	private Button btdenglu;
	private Button btzhuce;
	private EditText passwdedit;
	private EditText namededit;
	private LoadingProgressDialog dialog;
	private TextView wangjiTV;
	
	/**
     * 检测服务器版本更新所用参数
     * */
    private final String TAG = this.getClass().getName();
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private TextView textView;
	private UpdataInfo info;
	private String localVersion;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_login);
        btdenglu = (Button)findViewById(R.id.denglu);
        btzhuce = (Button)findViewById(R.id.zhuce);
        passwdedit = (EditText)findViewById(R.id.passwdedittext);
        namededit = (EditText)findViewById(R.id.nameedittext);
        wangjiTV = (TextView)findViewById(R.id.wangji);
        btdenglu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				Intent intent = new Intent(AnyCareLoginActivity.this,AnyCareMainActivity.class); 
//				startActivity(intent);
				if(NetUtils.isConnected(getApplicationContext())){
					//true为记住密码
					String loginName = namededit.getText().toString().trim();
					String loginPass = passwdedit.getText().toString().trim();
					if(!TextUtils.isEmpty(loginName)&&!TextUtils.isEmpty(loginPass)){
						//执行验证的异步操作
						new UserLoginAsyncTask().execute(new String[]{loginName,loginPass});
					}else{
						Toast.makeText(getApplicationContext(), "用户名或者密码不能为空！", Toast.LENGTH_SHORT).show();
					}
				}else{
					NetUtils.openSetting(AnyCareLoginActivity.this);
				}
			}
		 });
        btzhuce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {				
				Intent intent = new Intent(AnyCareLoginActivity.this,AnyCareRegister1Activity.class);
				startActivity(intent); 
			}
		 });
        wangjiTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AnyCareLoginActivity.this,AnyCareForgetActivity.class);
				startActivity(intent); 
			}
		 });
        //初始化dialog
		dialog=new LoadingProgressDialog(this,"正在加载...");
		//初始化dialog end
		//记住用户名和密码
		namededit.setText(readLoginName());
		passwdedit.setText(readLoginPass());
		//自动检测版本语句开始
		try{
			localVersion = getVersionName();
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		//自动检测版本语句结束
		
    }  
    	/**
    	 * dis：AsyncTask参数类型：
    	 * 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径
    	 * 第二个参数表示进度的刻度
    	 * 第三个参数表示返回的结果类型
    	 * */
    	private class UserLoginAsyncTask extends AsyncTask<String, String, String>{
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
    				String phoneNumber = params[0];
    				userInforMap.put("phoneNumber", params[0]);
    				userInforMap.put("userPassword", params[1]);
    				String jsonResult = HttpUtils.doPost("/anyCare/userLogin.action", userInforMap);
    				if(jsonResult!=null&&!"".equals(jsonResult)){
    					if(!"errorinfor".equals(jsonResult)){
    						// 调用 JPush 接口来设置别名。add by ma_yming
    						JPushInterface.setAliasAndTags(getApplicationContext(), jsonResult, null, mAliasCallback);//设置userId的alias（别名）
    						//add by ma_yming end
    						saveLoginInfor(params[0], params[1]);
    						saveUserInfor(jsonResult, phoneNumber);
    						saveOpenParam("YES");
    						return "success";
    					}else{
    						return "false";
    					}
    				}else{
    					return "networkerror";
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				return "false";
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
    			if("success".equals(result)){
    				Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
    				startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
    				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    				AnyCareLoginActivity.this.finish();
    			}else if("false".equals(result)){
    				Toast.makeText(getApplicationContext(), "用户名密码有误，请重新登录！", Toast.LENGTH_SHORT).show();
    			}else if("networkerror".equals(result)){
    				Toast.makeText(getApplicationContext(), "网络加载异常", Toast.LENGTH_SHORT).show();
    			}
    			dialog.dismiss();//dialog关闭，数据处理完毕
    		}
    	}
    	
    	private void saveOpenParam(String openParam){
    		//获取SharedPreferences对象，路径在/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//获取编辑器
    		Editor editor=sp.edit();
    		//通过editor进行设置
    		editor.putString("openParam", openParam);
    		//提交修改，将数据写到文件
    		editor.commit();
    	}
    	
    	private void saveLoginInfor(String loginName,String loginPass){
    		//获取SharedPreferences对象，路径在/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//获取编辑器
    		Editor editor=sp.edit();
    		//通过editor进行设置
    		editor.putString("loginName", loginName);
    		editor.putString("loginPass", loginPass);
    		//提交修改，将数据写到文件
    		editor.commit();
    	}
    	
    	private void saveUserInfor(String userId,String phoneNumber){
    		//获取SharedPreferences对象，路径在/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//获取编辑器
    		Editor editor=sp.edit();
    		//通过editor进行设置
    		editor.putString("userId", userId);
    		editor.putString("phoneNumber", phoneNumber);
    		//提交修改，将数据写到文件
    		editor.commit();
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
    	
    	private String readLoginName(){
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//若没有数据，返回默认值""
    		String loginName=sp.getString("loginName", "");
    		return loginName;
    	}
    	
    	private String readLoginPass(){
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//若没有数据，返回默认值""
    		String loginPass=sp.getString("loginPass", "");
    		return loginPass;
    	}
    	@Override
    	public boolean onKeyDown(int keyCode, KeyEvent event) {
    		// TODO Auto-generated method stub
    		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
    			dialog();
    		}
    		return super.onKeyDown(keyCode, event);
    	}
    	/** 
    	 * author:ma_yming
    	 * dis:显示dialog 
    	 * */
    	private void dialog() {
    		AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareLoginActivity.this);
    		builder
    			.setTitle("确认")
    			.setMessage("确定要退出吗?")
    			.setPositiveButton("是", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					dialog.cancel();
    					saveUserInfor("", "");
    					saveOpenParam("");
    					AnyCareLoginActivity.this.finish();
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
    	
    	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
    	    @Override
    	    public void gotResult(int code, String alias, Set<String> tags) {
    	        String logs ;
    	        switch (code) {
    	        case 0:
    	            //logs = "成功设置Alias和Tags";
    	            //Log.i("MYPUSH", logs);
    	            // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
    	            break;
    	        case 6002:
    	            //logs = "设置Alias和Tags失败，请60秒后再试";
    	            //Log.i("MYPUSH", logs);
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareLoginActivity.this);
    	    		builder
    	    			.setTitle("设置推送参数失败！")
    	    			.setMessage("是否重试?")
    	    			.setPositiveButton("是", new DialogInterface.OnClickListener() {
    	    				@Override
    	    				public void onClick(DialogInterface dialog, int which) {
    	    					// TODO Auto-generated method stub
    	    					dialog.cancel();
    	    					// 调用 JPush 接口来设置别名。add by ma_yming
    	    		            JPushInterface.setAliasAndTags(getApplicationContext(), readUserId(), null, mAliasCallback);//设置userId的alias（别名）
    	    		            //add by ma_yming end
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
    	            break;
    	        default:
    	            logs = "Failed with errorCode = " + code;
    	            Log.e("MYPUSH", logs);
    	        }
    	    }
    	};
    	
    	
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
//    				Toast.makeText(getApplicationContext(), "版本号相同无需升级",Toast.LENGTH_SHORT).show();
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
    		pd = new ProgressDialog(AnyCareLoginActivity.this);
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
}
