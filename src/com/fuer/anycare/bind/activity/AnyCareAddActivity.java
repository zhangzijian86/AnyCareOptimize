package com.fuer.anycare.bind.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

public class AnyCareAddActivity extends Activity {
	private ImageView bangdingshebeitu;
	private ImageView duigou;
	private String archivesId;
	private EditText shebeihaoET;
	private EditText fanweimaET;
	private LoadingProgressDialog dialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_add);
		bangdingshebeitu = (ImageView) findViewById(R.id.bangdingshebeitu);
		duigou = (ImageView) findViewById(R.id.duigou);
		fanweimaET = (EditText) findViewById(R.id.fanweima);
		shebeihaoET = (EditText) findViewById(R.id.shebeihao);
		Bundle bundle = this.getIntent().getExtras();
		archivesId = bundle.getString("archivesId");
		bangdingshebeitu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		duigou.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (archivesId != null & !"".equals(archivesId)) {
					String shebeihao = shebeihaoET.getText().toString().trim();
					String fanweima = fanweimaET.getText().toString().trim();
					if (!TextUtils.isEmpty(shebeihao)&& !TextUtils.isEmpty(fanweima)) {
						new ArchivesBindDevicesAsyncTask().execute(new String[] { archivesId, shebeihao,fanweima });
					} else {
						Toast.makeText(getApplicationContext(),"设备号、防伪码都不能为空！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// 初始化dialog
		dialog = new LoadingProgressDialog(this, "正在加载...");
		// 初始化dialog end
		
		
		
	}

	/**
	 * dis：AsyncTask参数类型： 第一个参数标书传入到异步任务中并进行操作，通常是网络的路径 第二个参数表示进度的刻度
	 * 第三个参数表示返回的结果类型
	 * */
	private class ArchivesBindDevicesAsyncTask extends
			AsyncTask<String, String, String> {
		// 任务执行之前的操作
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();// 显示dialog，数据正在处理....
		}

		// 完成耗时操作
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Map<String, String> userInforMap = new HashMap<String, String>();
				userInforMap.put("archivesId", params[0]);
				userInforMap.put("deviceCode", params[1]);
				userInforMap.put("securityCode", params[2]);
				String jsonResult = "";
				jsonResult = HttpUtils.doPost("/anyCare/archivesBindDevice.action", userInforMap);
				return jsonResult;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);

		}

		// 数据处理完毕后更新UI操作
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && !"".equals(result) && "true".equals(result)) {
				Toast.makeText(getApplicationContext(), "设备绑定成功！",
						Toast.LENGTH_SHORT).show();
				Bundle bundle = new Bundle();
				bundle.putString("archivesId", archivesId);
				startActivity(new Intent(getApplication(),AnyCareBindActivity.class).putExtras(bundle));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				AnyCareAddActivity.this.finish();
			} else if (result != null && !"".equals(result)
					&& "nodevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "无该设备！",
						Toast.LENGTH_SHORT).show();
			} else if (result != null && !"".equals(result)
					&& "bindDeviceLocked".equals(result)) {
				Toast.makeText(getApplicationContext(), "设备已被绑定，请先解绑！",
						Toast.LENGTH_SHORT).show();
			} else if (result != null && !"".equals(result)
					&& "bindSameDevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "一个人员不可绑定另一个相同设备",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "绑定失败！",
						Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();// dialog关闭，数据处理完毕
		}
	}

}

