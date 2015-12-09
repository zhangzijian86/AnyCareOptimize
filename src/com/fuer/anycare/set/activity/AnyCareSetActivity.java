package com.fuer.anycare.set.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuer.anycare.version.utils.DownLoadManager;
import com.fuer.anycare.version.utils.UpdataInfo;
import com.fuer.anycare.version.utils.UpdataInfoParser;
import com.fuer.main.anycare.R;

public class AnyCareSetActivity extends Activity {
	
	private ImageView shezhitu;
	private Button xiugaimimaBtn;
	private Button jianchagengxinBtn;
	private Button yijianfankuiBtn;
	private Button guanyuwomenBtn;
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
	private Button getVersion;
	private UpdataInfo info;
	private String localVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_set);
		shezhitu = (ImageView) findViewById(R.id.shezhitu);
		shezhitu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		xiugaimimaBtn = (Button) findViewById(R.id.xiugaimima);
		jianchagengxinBtn = (Button) findViewById(R.id.jianchagengxin);
		yijianfankuiBtn = (Button) findViewById(R.id.yijianfankui);
		guanyuwomenBtn = (Button) findViewById(R.id.guanyuwomen);
		xiugaimimaBtn.setOnClickListener(new MyOnClickListener());
		jianchagengxinBtn.setOnClickListener(new MyOnClickListener());
		yijianfankuiBtn.setOnClickListener(new MyOnClickListener());
		guanyuwomenBtn.setOnClickListener(new MyOnClickListener());
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.xiugaimima:
					startActivity(new Intent(AnyCareSetActivity.this,AnyCareAlterPasswordActivity.class));
					break;
				case R.id.jianchagengxin:
					//�Զ����汾��俪ʼ
					try{
						localVersion = getVersionName();
						CheckVersionTask cv = new CheckVersionTask();
						new Thread(cv).start();
					}catch(Exception e){
						e.printStackTrace();
					}
					//�Զ����汾������
					break;
				case R.id.yijianfankui:
					startActivity(new Intent(AnyCareSetActivity.this,AnyCareAdviceActivity.class));
					break;
				case R.id.guanyuwomen:
					startActivity(new Intent(AnyCareSetActivity.this,AnyCareAboutUsActivity.class));
					break;
			}
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
				Toast.makeText(getApplicationContext(), "���°汾��������",Toast.LENGTH_SHORT).show();
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
		pd = new ProgressDialog(AnyCareSetActivity.this);
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
