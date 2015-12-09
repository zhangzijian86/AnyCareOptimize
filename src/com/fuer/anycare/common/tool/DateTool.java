package com.fuer.anycare.common.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {

	public static String getDateType1(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"). format(new Date());
	
	}
	public static String getDateType2(){
		return new SimpleDateFormat("yyyy-MM-dd"). format(new Date());
	}
	
	public static String getDateType3(){
		return new SimpleDateFormat("HH:mm:ss"). format(new Date());
	}
	
	public static String getDateType4(){
		return new SimpleDateFormat("HH:mm"). format(new Date());
	}
	public static Integer getDateYear(){
		return Integer.parseInt(new SimpleDateFormat("yyyy"). format(new Date()));
	}
	public static Integer getDateMonth(){
		return Integer.parseInt(new SimpleDateFormat("MM"). format(new Date()));
	}
	public static Integer getDateDay(){
		return Integer.parseInt(new SimpleDateFormat("dd"). format(new Date()));
	}
	
	public static String getYearOfMonth(){
		return new SimpleDateFormat("yyyy-MM"). format(new Date());
	}
	
	/**
	 * 传入日期，month 为正，则为当前月份加上现在的月份
	 * 			month 为负，则为当前月份减掉现在的月份
	 * */
	public static String calYearOfMonth(String yearOfMonth,int month){
		String reStr = "";
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
			Date date = df.parse(yearOfMonth);
			Calendar rightNow = Calendar.getInstance();
		    rightNow.setTime(date);
		    rightNow.add(Calendar.MONTH,month);//日期加3个月
		    Date dt=rightNow.getTime();
		    reStr = df.format(dt);
		}catch(Exception e){
			e.printStackTrace();
		}
		return reStr;
	}
	
	public static String yearMonth(int year,int month){
		String reStr = "";
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
			Date date = df.parse(year+"-"+month);
			reStr = df.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
		return reStr;
	}
	
	public static void main(String []args){
		System.out.println(yearMonth(2015, 1));
	}
}

