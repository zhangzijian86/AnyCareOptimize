package com.fuer.anycare.database.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseManager {
	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DataBaseManager(Context context) {
		helper = new DatabaseHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	/**
	 * add persons
	 * 
	 * @param persons
	 */
	public void add(List<XingZouJingZhiEntity> xingzoujingzhis) {
		db.beginTransaction(); // 开始事务
		try {
			for (XingZouJingZhiEntity xingzoujingzhi : xingzoujingzhis) {
				db.execSQL("INSERT INTO xingzoujingzhi VALUES(?, ?, ?, ?, ?)",new Object[] { xingzoujingzhi.getDeviceNumber(), xingzoujingzhi.getJingZhi(), xingzoujingzhi.getBuShu(), xingzoujingzhi.getHuoDongLiang(),xingzoujingzhi.getTime() });
			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	
	/**
	 * add persons
	 * 
	 * @param persons
	 */
	public void update(XingZouJingZhiEntity xingZouJingZhiEntity) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("UPDATE xingzoujingzhi set jingzhi = ? ,bushu = ? , huodongliang = ? WHERE devicenumber = ? AND time = ?",new Object[] {xingZouJingZhiEntity.getJingZhi(),xingZouJingZhiEntity.getBuShu(), xingZouJingZhiEntity.getHuoDongLiang(),xingZouJingZhiEntity.getDeviceNumber(),xingZouJingZhiEntity.getTime() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	
	/**
	 * add persons
	 * 
	 * @param persons
	 */
	public void delete(XingZouJingZhiEntity xingZouJingZhiEntity) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("delete from  xingzoujingzhi WHERE devicenumber = ? AND time = ?",new Object[] {xingZouJingZhiEntity.getDeviceNumber(),xingZouJingZhiEntity.getTime() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	
	public void updateParam(String id,String content) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("UPDATE param set content = ?  WHERE id = ? ",new Object[] {content,id });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
	
	public String queryParam(String id) {
		String result = "";
		Cursor c = db.rawQuery("SELECT content FROM param WHERE id = ?  ", new String[]{id});
		if (c.moveToNext()) {
			result = c.getString(c.getColumnIndex("content"));
		}
		c.close();
		return result;  
	}
	
	public List<XingZouJingZhiEntity> query(String deviceNumber ,String startTime, String endTime) {  
		DecimalFormat fnum = new DecimalFormat("##0.00");
		ArrayList<XingZouJingZhiEntity> xingzoujingzhis = new ArrayList<XingZouJingZhiEntity>();  
		Cursor c = db.rawQuery("SELECT devicenumber, jingzhi, bushu , huodongliang,time FROM xingzoujingzhi WHERE devicenumber = ? AND time >= ? AND time <= ? order by time", new String[]{deviceNumber,startTime,endTime});
		while (c.moveToNext()) {
			XingZouJingZhiEntity xingZouJingZhiEntity = new XingZouJingZhiEntity();
			xingZouJingZhiEntity.setDeviceNumber(c.getString(c.getColumnIndex("devicenumber")));
			xingZouJingZhiEntity.setJingZhi(c.getString(c.getColumnIndex("jingzhi")));
			xingZouJingZhiEntity.setBuShu(c.getString(c.getColumnIndex("bushu")));
			float huodongliang=c.getFloat(c.getColumnIndex("huodongliang"));
			xingZouJingZhiEntity.setHuoDongLiang(fnum.format(huodongliang));
			xingZouJingZhiEntity.setTime(c.getString(c.getColumnIndex("time")));
			xingzoujingzhis.add(xingZouJingZhiEntity);
		}
		c.close();
		return xingzoujingzhis;  
	}
	
	public XingZouJingZhiEntity query(String deviceNumber ,String time) {
		DecimalFormat fnum = new DecimalFormat("##0.00");
		XingZouJingZhiEntity xingZouJingZhiEntity = null;  
		Cursor c = db.rawQuery("SELECT devicenumber,jingzhi,bushu,huodongliang,time FROM xingzoujingzhi WHERE devicenumber = ? AND time = ?  ", new String[]{deviceNumber,time});
		if (c.moveToNext()) {
			xingZouJingZhiEntity = new XingZouJingZhiEntity();
			xingZouJingZhiEntity.setDeviceNumber(c.getString(c.getColumnIndex("devicenumber")));
			xingZouJingZhiEntity.setJingZhi(c.getString(c.getColumnIndex("jingzhi")));
			xingZouJingZhiEntity.setBuShu(c.getString(c.getColumnIndex("bushu")));
			float huodongliang=c.getFloat(c.getColumnIndex("huodongliang"));
			xingZouJingZhiEntity.setHuoDongLiang(fnum.format(huodongliang));
			xingZouJingZhiEntity.setTime(c.getString(c.getColumnIndex("time")));
		}
		c.close();
		return xingZouJingZhiEntity;  
	}

}
