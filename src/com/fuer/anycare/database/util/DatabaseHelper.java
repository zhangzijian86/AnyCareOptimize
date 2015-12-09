package com.fuer.anycare.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;  
	private static final String ANYCARE="anycare";

	//������ͬ�����Ĺ��캯��  
	//��ȫ�������Ĺ��캯�����˹��캯���ز�����  
	public DatabaseHelper(Context context, String name, CursorFactory factory,int version) {  
		super(context, ANYCARE, factory, version);  
	}  
	//�����������Ĺ��캯�������õ���ʵ�Ǵ����������Ĺ��캯��  
	public DatabaseHelper(Context context,String name){  
		this(context,ANYCARE,VERSION);  
	}  
	//�����������Ĺ��캯�������õ��Ǵ����в����Ĺ��캯��  
	public DatabaseHelper(Context context,String name,int version){  
		this(context, ANYCARE,null,version);  
	}
	
	public DatabaseHelper(Context context) {
	//CursorFactory����Ϊnull,ʹ��Ĭ��ֵ  
		super(context, ANYCARE, null, VERSION);
	}  


	//���ݿ��һ�α�����ʱonCreate�ᱻ����  
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(ANYCARE,"create a Database");
		//�������ݿ�sql���
		db.execSQL("CREATE TABLE IF NOT EXISTS param (id VARCHAR PRIMARY KEY, name VARCHAR, content VARCHAR, createtime VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS xingzoujingzhi (devicenumber VARCHAR, jingzhi VARCHAR,bushu VARCHAR, huodongliang VARCHAR,time VARCHAR)");
		db.execSQL("INSERT INTO param VALUES(?, ?, ?, ?)",new Object[] {"1","����ͬ������","1900-01-01","1900-01-01"});
	}

	//���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//�����ɹ�����־�����ʾ  
		Log.i(ANYCARE,"update a Database");
	}
	
}
