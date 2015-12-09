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
						Toast.makeText(getApplicationContext(),"�豸�š���α�붼����Ϊ�գ�", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// ��ʼ��dialog
		dialog = new LoadingProgressDialog(this, "���ڼ���...");
		// ��ʼ��dialog end
		
		
		
	}

	/**
	 * dis��AsyncTask�������ͣ� ��һ���������鴫�뵽�첽�����в����в�����ͨ���������·�� �ڶ���������ʾ���ȵĿ̶�
	 * ������������ʾ���صĽ������
	 * */
	private class ArchivesBindDevicesAsyncTask extends
			AsyncTask<String, String, String> {
		// ����ִ��֮ǰ�Ĳ���
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();// ��ʾdialog���������ڴ���....
		}

		// ��ɺ�ʱ����
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

		// ���ݴ�����Ϻ����UI����
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && !"".equals(result) && "true".equals(result)) {
				Toast.makeText(getApplicationContext(), "�豸�󶨳ɹ���",
						Toast.LENGTH_SHORT).show();
				Bundle bundle = new Bundle();
				bundle.putString("archivesId", archivesId);
				startActivity(new Intent(getApplication(),AnyCareBindActivity.class).putExtras(bundle));
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				AnyCareAddActivity.this.finish();
			} else if (result != null && !"".equals(result)
					&& "nodevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "�޸��豸��",
						Toast.LENGTH_SHORT).show();
			} else if (result != null && !"".equals(result)
					&& "bindDeviceLocked".equals(result)) {
				Toast.makeText(getApplicationContext(), "�豸�ѱ��󶨣����Ƚ��",
						Toast.LENGTH_SHORT).show();
			} else if (result != null && !"".equals(result)
					&& "bindSameDevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "һ����Ա���ɰ���һ����ͬ�豸",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "��ʧ�ܣ�",
						Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();// dialog�رգ����ݴ������
		}
	}

}
