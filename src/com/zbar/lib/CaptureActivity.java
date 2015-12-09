package com.zbar.lib;

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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fuer.anycare.bind.activity.AnyCareAddActivity;
import com.fuer.anycare.bind.activity.AnyCareBindActivity;
import com.fuer.anycare.common.ui.LoadingProgressDialog;
import com.fuer.anycare.common.utils.HttpUtils;
import com.fuer.main.anycare.R;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:25:31
 * 
 * 版本: V_1.0.0
 * 
 * 描述: 扫描界面
 */
public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	
	
	private ImageView bangdingshebeitu;
	private ImageView duigou;
	private String archivesId;
	private EditText shebeihaoET;
	private EditText fanweimaET;
	private LoadingProgressDialog dialog;
	
	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		
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

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
//		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//		 handler.sendEmptyMessage(R.id.restart_preview);
		String args[]= result.split("-");
		if(args.length==2){
			if (!TextUtils.isEmpty(args[0])&& !TextUtils.isEmpty(args[1])) {
				new ArchivesBindDevicesAsyncTask().execute(new String[] { archivesId, args[0],args[1] });
			} else {
				Toast.makeText(getApplicationContext(),"设备号、防伪码都不能为空！", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(),"无效数据！", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(R.id.restart_preview);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(false);
			

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	
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
				CaptureActivity.this.finish();
			} else if (result != null && !"".equals(result)
					&& "nodevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "无该设备！",
						Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(R.id.restart_preview);
			} else if (result != null && !"".equals(result)
					&& "bindDeviceLocked".equals(result)) {
				Toast.makeText(getApplicationContext(), "设备已被绑定，请先解绑！",
						Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(R.id.restart_preview);
			} else if (result != null && !"".equals(result)
					&& "bindSameDevice".equals(result)) {
				Toast.makeText(getApplicationContext(), "一个人员不可绑定另一个相同设备",
						Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(R.id.restart_preview);
			} else {
				Toast.makeText(getApplicationContext(), "绑定失败！",
						Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(R.id.restart_preview);
			}
			dialog.dismiss();// dialog关闭，数据处理完毕
		}
	}
}