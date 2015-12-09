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
     * ���������汾�������ò���
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
					//trueΪ��ס����
					String loginName = namededit.getText().toString().trim();
					String loginPass = passwdedit.getText().toString().trim();
					if(!TextUtils.isEmpty(loginName)&&!TextUtils.isEmpty(loginPass)){
						//ִ����֤���첽����
						new UserLoginAsyncTask().execute(new String[]{loginName,loginPass});
					}else{
						Toast.makeText(getApplicationContext(), "�û����������벻��Ϊ�գ�", Toast.LENGTH_SHORT).show();
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
        //��ʼ��dialog
		dialog=new LoadingProgressDialog(this,"���ڼ���...");
		//��ʼ��dialog end
		//��ס�û���������
		namededit.setText(readLoginName());
		passwdedit.setText(readLoginPass());
		//�Զ����汾��俪ʼ
		try{
			localVersion = getVersionName();
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		}catch(Exception e){
			e.printStackTrace();
		}
		//�Զ����汾������
		
    }  
    	/**
    	 * dis��AsyncTask�������ͣ�
    	 * ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·��
    	 * �ڶ���������ʾ���ȵĿ̶�
    	 * ������������ʾ���صĽ������
    	 * */
    	private class UserLoginAsyncTask extends AsyncTask<String, String, String>{
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
    				String phoneNumber = params[0];
    				userInforMap.put("phoneNumber", params[0]);
    				userInforMap.put("userPassword", params[1]);
    				String jsonResult = HttpUtils.doPost("/anyCare/userLogin.action", userInforMap);
    				if(jsonResult!=null&&!"".equals(jsonResult)){
    					if(!"errorinfor".equals(jsonResult)){
    						// ���� JPush �ӿ������ñ�����add by ma_yming
    						JPushInterface.setAliasAndTags(getApplicationContext(), jsonResult, null, mAliasCallback);//����userId��alias��������
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
    		
    		//���ݴ�����Ϻ����UI����
    		@Override
    		protected void onPostExecute(String result) {
    			// TODO Auto-generated method stub
    			super.onPostExecute(result);
    			if("success".equals(result)){
    				Toast.makeText(getApplicationContext(), "��¼�ɹ�", Toast.LENGTH_SHORT).show();
    				startActivity(new Intent(getApplication(), AnyCareMainActivity.class));
    				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    				AnyCareLoginActivity.this.finish();
    			}else if("false".equals(result)){
    				Toast.makeText(getApplicationContext(), "�û����������������µ�¼��", Toast.LENGTH_SHORT).show();
    			}else if("networkerror".equals(result)){
    				Toast.makeText(getApplicationContext(), "��������쳣", Toast.LENGTH_SHORT).show();
    			}
    			dialog.dismiss();//dialog�رգ����ݴ������
    		}
    	}
    	
    	private void saveOpenParam(String openParam){
    		//��ȡSharedPreferences����·����/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//��ȡ�༭��
    		Editor editor=sp.edit();
    		//ͨ��editor��������
    		editor.putString("openParam", openParam);
    		//�ύ�޸ģ�������д���ļ�
    		editor.commit();
    	}
    	
    	private void saveLoginInfor(String loginName,String loginPass){
    		//��ȡSharedPreferences����·����/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//��ȡ�༭��
    		Editor editor=sp.edit();
    		//ͨ��editor��������
    		editor.putString("loginName", loginName);
    		editor.putString("loginPass", loginPass);
    		//�ύ�޸ģ�������д���ļ�
    		editor.commit();
    	}
    	
    	private void saveUserInfor(String userId,String phoneNumber){
    		//��ȡSharedPreferences����·����/data/data/cn.itcast.preferences/shared_pref/paramater.xml
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//��ȡ�༭��
    		Editor editor=sp.edit();
    		//ͨ��editor��������
    		editor.putString("userId", userId);
    		editor.putString("phoneNumber", phoneNumber);
    		//�ύ�޸ģ�������д���ļ�
    		editor.commit();
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
    	
    	private String readLoginName(){
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//��û�����ݣ�����Ĭ��ֵ""
    		String loginName=sp.getString("loginName", "");
    		return loginName;
    	}
    	
    	private String readLoginPass(){
    		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
    		//��û�����ݣ�����Ĭ��ֵ""
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
    	 * dis:��ʾdialog 
    	 * */
    	private void dialog() {
    		AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareLoginActivity.this);
    		builder
    			.setTitle("ȷ��")
    			.setMessage("ȷ��Ҫ�˳���?")
    			.setPositiveButton("��", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					// TODO Auto-generated method stub
    					dialog.cancel();
    					saveUserInfor("", "");
    					saveOpenParam("");
    					AnyCareLoginActivity.this.finish();
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
    	
    	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
    	    @Override
    	    public void gotResult(int code, String alias, Set<String> tags) {
    	        String logs ;
    	        switch (code) {
    	        case 0:
    	            //logs = "�ɹ�����Alias��Tags";
    	            //Log.i("MYPUSH", logs);
    	            // ���������� SharePreference ��дһ���ɹ����õ�״̬���ɹ�����һ�κ��Ժ󲻱��ٴ������ˡ�
    	            break;
    	        case 6002:
    	            //logs = "����Alias��Tagsʧ�ܣ���60�������";
    	            //Log.i("MYPUSH", logs);
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(AnyCareLoginActivity.this);
    	    		builder
    	    			.setTitle("�������Ͳ���ʧ�ܣ�")
    	    			.setMessage("�Ƿ�����?")
    	    			.setPositiveButton("��", new DialogInterface.OnClickListener() {
    	    				@Override
    	    				public void onClick(DialogInterface dialog, int which) {
    	    					// TODO Auto-generated method stub
    	    					dialog.cancel();
    	    					// ���� JPush �ӿ������ñ�����add by ma_yming
    	    		            JPushInterface.setAliasAndTags(getApplicationContext(), readUserId(), null, mAliasCallback);//����userId��alias��������
    	    		            //add by ma_yming end
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
    	            break;
    	        default:
    	            logs = "Failed with errorCode = " + code;
    	            Log.e("MYPUSH", logs);
    	        }
    	    }
    	};
    	
    	
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
//    				Toast.makeText(getApplicationContext(), "�汾����ͬ��������",Toast.LENGTH_SHORT).show();
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
    		pd = new ProgressDialog(AnyCareLoginActivity.this);
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
}
