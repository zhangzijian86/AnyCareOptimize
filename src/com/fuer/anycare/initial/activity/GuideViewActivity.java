package com.fuer.anycare.initial.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.fuer.anycare.login.activity.AnyCareLoginActivity;
import com.fuer.main.anycare.R;

public class GuideViewActivity extends Activity {
	
    private ViewPager viewPager;  
    private ArrayList<View> pageViews;  
    private ViewGroup main, group;  
    private ImageView imageView;  
    private ImageView[] imageViews; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();  
        pageViews = new ArrayList<View>();  
        pageViews.add(inflater.inflate(R.layout.item01, null));
        pageViews.add(inflater.inflate(R.layout.item02, null));
        pageViews.add(inflater.inflate(R.layout.item03, null)); 
        View viewItem04 = inflater.inflate(R.layout.item04, null); 
        Button start_button = (Button) viewItem04.findViewById(R.id.start_button);
        start_button.setOnClickListener(new OnClickListener(){ 
        	public void onClick(View v){
        		saveGuideView();
        	}
        }); 
        pageViews.add(viewItem04);   
        imageViews = new ImageView[pageViews.size()];  
        main = (ViewGroup)inflater.inflate(R.layout.layout_guideview, null);  
        // group是R.layou.main中的负责包裹小圆点的LinearLayout.  
        group = (ViewGroup)main.findViewById(R.id.viewGroup);
        viewPager = (ViewPager)main.findViewById(R.id.guidePages);  
        for (int i = 0; i < pageViews.size(); i++) {  
            imageView = new ImageView(GuideViewActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10,10));  
            imageView.setPadding(0, 0, 0, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.point_focused);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.point_unfocused);  
            }  
            group.addView(imageViews[i]);  
        }
        setContentView(main);  
        viewPager.setAdapter(new GuidePageAdapter());  
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());  
    }
    
    /** 指引页面Adapter */
    class GuidePageAdapter extends PagerAdapter {
        @Override  
        public int getCount() {  
            return pageViews.size();  
        }  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(pageViews.get(arg1));  
        }  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(pageViews.get(arg1));  
            return pageViews.get(arg1);  
        }
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }
    } 
    
    /** 指引页面改监听器 */
    class GuidePageChangeListener implements OnPageChangeListener {  
  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0].setBackgroundResource(R.drawable.point_focused);  
                if (arg0 != i) {
                    imageViews[i].setBackgroundResource(R.drawable.point_unfocused);  
                }  
            }
        }  
    }
    
    private void saveGuideView(){
		//获取SharedPreferences对象，路径在/data/data/cn.itcast.preferences/shared_pref/paramater.xml
		SharedPreferences sp=getSharedPreferences("paramater", Context.MODE_PRIVATE);
		//获取编辑器
		Editor editor=sp.edit();
		//通过editor进行设置
		editor.putString("firstOpen", "1");
		//提交修改，将数据写到文件
		editor.commit();
		startActivity(new Intent(GuideViewActivity.this, AnyCareLoginActivity.class));
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		GuideViewActivity.this.finish();
	}
    
    private class LoginAsyncTask extends AsyncTask<String, String, String>{
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
			return "success";
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
			startActivity(new Intent(getApplication(), AnyCareLoginActivity.class));
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			GuideViewActivity.this.finish();
		}
	}
    
}