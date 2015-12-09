package com.fuer.anycare.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;  
	private static final String ANYCARE="anycare";

	//三个不同参数的构造函数  
	//带全部参数的构造函数，此构造函数必不可少  
	public DatabaseHelper(Context context, String name, CursorFactory factory,int version) {  
		super(context, ANYCARE, factory, version);  
	}  
	//带两个参数的构造函数，调用的其实是带三个参数的构造函数  
	public DatabaseHelper(Context context,String name){  
		this(context,ANYCARE,VERSION);  
	}  
	//带三个参数的构造函数，调用的是带所有参数的构造函数  
	public DatabaseHelper(Context context,String name,int version){  
		this(context, ANYCARE,null,version);  
	}
	
	public DatabaseHelper(Context context) {
	//CursorFactory设置为null,使用默认值  
		super(context, ANYCARE, null, VERSION);
	}  


	//数据库第一次被创建时onCreate会被调用  
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(ANYCARE,"create a Database");
		//创建数据库sql语句
		db.execSQL("CREATE TABLE IF NOT EXISTS param (id VARCHAR PRIMARY KEY, name VARCHAR, content VARCHAR, createtime VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS xingzoujingzhi (devicenumber VARCHAR, jingzhi VARCHAR,bushu VARCHAR, huodongliang VARCHAR,time VARCHAR)");
		db.execSQL("INSERT INTO param VALUES(?, ?, ?, ?)",new Object[] {"1","最新同步日期","1900-01-01","1900-01-01"});
	}

	//如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//创建成功，日志输出提示  
		Log.i(ANYCARE,"update a Database");
	}
	
}

