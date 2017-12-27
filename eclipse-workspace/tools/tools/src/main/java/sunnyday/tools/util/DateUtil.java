package sunnyday.tools.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;

public class DateUtil {
	
	private static final Logger log = CommonLogFactory.getLog("infoLog");
	private static SimpleDateFormat fm= new SimpleDateFormat("yyyyMMddHHssmm");
	private static int seq = 0;
	public static String getCurrentDateString(String style){
		return new SimpleDateFormat(style).format(new Date());
	}
	  
	public static synchronized String getFileName(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMdd/HHmms");
		return sdf.format(System.currentTimeMillis()) + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + ".sms";
	}
	public static  String getMsgID(){
		String result = fm.format(new Date())+getRandomNum(5);
		return result;
	}
	/**
	 * yyyy-MM-dd
	 * @return
	 */
	public static synchronized String currentDate(){
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd");
		return sdf5.format(new Date(System.currentTimeMillis()));
	}
	
	public static synchronized String getFilePrefix(){
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf2.format(System.currentTimeMillis()) + (seq++)%10000 ;
	}
	public static synchronized long diffTime(String time1,String time2){
		long time = 0l;
		try {
			SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			time = (sdf4.parse(time2)).getTime()-(sdf4.parse(time1)).getTime();
		} catch (Exception e) {
			log.error("", e);
		}
		return time;
	}
	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static synchronized String currentTime() {
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf3.format(new Date(System.currentTimeMillis()));
	}

	public static synchronized String currentTimeToMs(){
		return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
	} 
	
	public static synchronized String msToDateFormat(long millis){
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf3.format(millis);
	}
	public static synchronized long getcurrentTime(){
		return System.currentTimeMillis();
	}	private static String getRandomNum(int pwd_len) {
		   // 35是因为数组是从0开始的，26个字母+10个数字
		   final int maxNum = 36;
		   int i; // 生成的随机数
		   int count = 0; // 生成的密码的长度
		   char[] str = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
		     '9' };

		   StringBuffer pwd = new StringBuffer("");
		   Random r = new Random();
		   while (count < pwd_len) {
		    // 生成随机数，取绝对值，防止生成负数，

		    i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
		   
		    if (i >= 0 && i < str.length) {
		     pwd.append(str[i]);
		     count++;
		    }
		   }

		   return pwd.toString();
		}
	/**
	 * 查询业务号(百悟)
	 * @param serviceMap
	 * @param corp_service
	 * @param userInfo
	 * @return
	 */
	public static String getCustomerYwForm(Map<String, List<UserServiceForm>> serviceMap, String corp_service,UserBean userInfo) {
        if(serviceMap==null || serviceMap.size()<=0){
            return null;
        }
        String pwd = CommonUntil.getPassword(userInfo);
        for(String key : serviceMap.keySet()){
        	for (UserServiceForm model : serviceMap.get(key)) {
                if (corp_service.equalsIgnoreCase(MD5.convert(pwd+model.getService_code()))) {
                    return model.getService_code();
                }
            }
        }
        return null;
    }
	
}
