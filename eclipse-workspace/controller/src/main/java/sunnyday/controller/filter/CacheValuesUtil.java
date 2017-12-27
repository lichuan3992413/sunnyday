package sunnyday.controller.filter;

public class CacheValuesUtil {
	public static final String placeHolder = "@";
	public static final String md5Holder = "&";
	public static final String splitChar = "#";
	public static final String valueSplit = "<%>";
	public static final String md5Split = "<$>";
	
	public static int getRepeatTime(String values) {
		int bi = values.indexOf(md5Split);
		int ei = values.indexOf(valueSplit) + valueSplit.length();
		String oneValue = values.substring(bi, ei);
		return CacheValueUtil.getCharCount(oneValue, splitChar);
	}
	
	public static int getSendTimes(String value, String md5) {
		if(value.contains(md5)){
			value = value.substring(value.indexOf(md5) + md5Split.length());
			value = value.substring(0, value.indexOf(valueSplit));
		}
		return getRepeatTime(value) - CacheValueUtil.getCharCount(value, placeHolder);
	}
	
	public static String getNewValue(int repeat_count) {
		StringBuilder sb = new StringBuilder();
		sb.append(md5Holder).append(md5Split);
		for(int i = 0; i < repeat_count; i++){
			sb.append(placeHolder).append(splitChar);
		}
		sb.append(valueSplit);
		return sb.toString();
	}

	public static String addValueElement(String ehcacheValue, long current_time, String md5) {
		
		if (!ehcacheValue.contains(md5) && !ehcacheValue.contains(md5Holder)){
			ehcacheValue = ehcacheValue + getNewValue(getRepeatTime(ehcacheValue));
		}
		ehcacheValue = ehcacheValue.replaceFirst(md5Holder, md5);

		int headOffset = ehcacheValue.indexOf(md5) + md5.length() + md5Split.length();
		String headStr = ehcacheValue.substring(0, headOffset);
		String tmpStr = ehcacheValue.substring(headOffset);
		
		int tailOffset = tmpStr.indexOf(valueSplit);
		String bodyStr = tmpStr.substring(0, tailOffset);
		bodyStr = CacheValueUtil.addValueElement(bodyStr, current_time);
		
		String tailStr = tmpStr.substring(tailOffset);
		return headStr + bodyStr + tailStr;
	}

	public static long getFirstElement(String value, String md5) {
		long result = 0;
		int offset = value.indexOf(md5);
		if(offset != -1){
			value = value.substring(offset + md5.length() + md5Split.length());
			value = value.substring(0, value.indexOf(valueSplit));
			result = CacheValueUtil.getFirstElement(value);
		}
		return result;
	}

	public static String removeHeadElement(String value, String md5) {
		String [] values = value.split(valueSplit);
		for(int i = 0; i < values.length; i++){
			if(values[i].contains(md5)){
				String tmpStr = values[i].substring(values[i].indexOf(md5Split) + md5Split.length());
				tmpStr = CacheValueUtil.removeHeadElement(tmpStr);
				values[i] = md5 + md5Split + tmpStr;
			}
		}
		
		String result = "";
		for(String v : values){
			result = result + v + valueSplit;
		}
		return result;
	}
	
	public static void main(String [] args){
		String md5_16 = "[1ioejsllkdjowije]";
		String newValue = getNewValue(1000);
		System.err.println(newValue);
		System.out.println(getRepeatTime(newValue));
		System.out.println(getSendTimes(newValue, md5_16));
		
		String firstAdd = addValueElement(newValue, System.currentTimeMillis(), md5_16);
		System.out.println("firstAdd = " + firstAdd);
		long first = getFirstElement(firstAdd, md5_16);
		System.out.println(first);
		md5_16 = "[2ioejslgw333owije]";
		String secondAdd = addValueElement(firstAdd, System.currentTimeMillis() + 1, md5_16);
		String thirdAdd = addValueElement(secondAdd, System.currentTimeMillis() + 2, md5_16);
		System.out.println(thirdAdd);
		String removeString = removeHeadElement(thirdAdd, md5_16);
		System.out.println(removeString);
		
	}


}
