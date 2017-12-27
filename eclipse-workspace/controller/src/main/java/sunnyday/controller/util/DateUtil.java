package com.hskj.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;

import com.hskj.log.CommonLogFactory;

public class DateUtil {
	
	private static final Logger log = CommonLogFactory.getCommonLog("infoLog");
	
	private static int seq = 0;
	
	/**
	 * 将yyyyMMddHHmmss格式转换成yyyy-MM-dd HH:mm:ss
	 *
	 * @param date
	 * @return
	 */
	public static String convertDateFormat(String date) {
		/*
		 * 20170612145201
		 * 0123-45-67 89:01:23
		 */
		char[] dateChar = date.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < dateChar.length; i ++){
			if(i == 4 || i == 6){
				buffer.append("-");
			}
			
			if(i == 8){
				buffer.append(" ");
			}
			
			if(i == 10 || i == 12){
				buffer.append(":");
			}
			
			buffer.append(dateChar[i]);
		}
		
		return buffer.toString();
	}
	
	
	public static String currentTimeToMs(String style){
		return new SimpleDateFormat(style).format(new Date());
	}
	  
	public static synchronized String getFileName(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMdd/HHmms");
		return sdf.format(System.currentTimeMillis()) + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + ".sms";
	}
	/**
	 * yyyy-MM-dd
	 * @return
	 */
	public static synchronized String currentDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		return sdf.format(date);
	}
	
	public static synchronized String currentDate1() {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date(System.currentTimeMillis());
		return sdf.format(date);
	}
	
	public static synchronized String getFilePrefix(){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(System.currentTimeMillis()) + (seq++)%10000 ;
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static synchronized String currentTime() {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		return sdf.format(date);
	}
	
	public static synchronized String currentTime1() {
	    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		Date date = new Date(System.currentTimeMillis());
		return sdf.format(date);
	}
	
	public static  String currentTimeToMs(){
		return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
	} 
	
	public static synchronized String msToDateFormat(long millis){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(millis);
	}
	
	public static synchronized long dateFormatToMs(String date) throws Exception{
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(date).getTime();
	}
	
	
	public static synchronized int subTime(String old_time, String new_time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		if (old_time == null || "".equals(old_time)) {
			old_time = sdf.format(new Date() );
		}
		if(old_time.length()==19){
			old_time = old_time+",000";
		}
		if (new_time == null || "".equals(new_time)) {
			new_time = sdf.format(new Date() );
		}
		if(new_time.length()==19){
			new_time = new_time+",000";
		}
		try {
			long time1 = sdf.parse(old_time).getTime();
			long time2 = sdf.parse(new_time).getTime();
			return (int)(time2-time1)/1000;
		} catch (ParseException e) {
			log.error("", e);
		}
		return 0;
	}
	
	/**
	 * 返回当前时间和分钟，格式：HH:mm字符串 
	 * @return
	 */
	public static String currentHourMin(){
		return DateFormatUtils.format(new Date(), "HH:mm");
	}
	
	/**
	 * 增加小时数量
	 * 
	 * @param hm
	 * @param h
	 * @return
	 */
	public static String addHour(String hm, int h) {
		String[] hms = hm.split(":");
		int tmpH = Integer.valueOf(hms[0]) + h;
		String hStr = "" + tmpH;
		if (tmpH < 10) {
			hStr = "0" + hStr;
		}
		return hStr + ":" + hms[1];
	}

	/**
	 * 校验输入字符串是否满足格式
	 * @param str 需要匹配的字符串
	 * @param pattern 时间格式
	 * @return true满足，false不满足
	 */
	public static boolean timeMatch(String str,String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		try {
			Date parse = format.parse(str);
			return format.format(parse).equals(str);
		} catch (ParseException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		boolean yyyyMMdd = timeMatch("20170930", "yyyyMMdd");
		System.out.println(yyyyMMdd);
	}
}
