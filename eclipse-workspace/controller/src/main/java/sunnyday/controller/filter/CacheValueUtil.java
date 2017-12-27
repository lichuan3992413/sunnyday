package sunnyday.controller.filter;

public class CacheValueUtil {
	public static final String splitChar = "#";
	public static final String placeHolder = "@";

	public static int getCharCount(String srcStr, String _char){
		int index = -1;
		int result = -1;
		do{
			index++;
			result++;
			index = srcStr.indexOf(_char, index);
		}while(index != -1);
		
		return result;
	}
	
	public static int getRepeatTime(String value) {
		return getCharCount(value, splitChar);
	}
	
	public static int getSendTimes(String value) {
		return getRepeatTime(value) - getCharCount(value, placeHolder);
	}
	
	public static String getNewValue(int repeat_count) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < repeat_count; i++){
			sb.append(placeHolder).append(splitChar);
		}
		return sb.toString();
	}

	public static String addValueElement(String ehcacheValue, long current_time) {
		return ehcacheValue.replaceFirst(placeHolder, String.valueOf(current_time));
	}

	public static long getFirstElement(String value) {
		return Long.valueOf(value.substring(0, value.indexOf(splitChar)));
	}

	public static String removeHeadElement(String value) {
		return value.substring(value.indexOf(splitChar) + splitChar.length()) + placeHolder + splitChar;
	}
	
//	public static void main(String [] args){
//		String newValue = getNewValue(1);
//		System.out.println(getRepeatTime(newValue));
//		System.out.println(getSendTimes(newValue));
//		
//		String firstAdd = addValueElement(newValue, System.currentTimeMillis());
//		System.out.println(firstAdd);
//		long first = getFirstElement(firstAdd);
//		System.out.println(first);
//		String secondAdd = addValueElement(firstAdd, System.currentTimeMillis());
//		String thirdAdd = addValueElement(secondAdd, System.currentTimeMillis());
//		System.out.println(thirdAdd);
//		String removeString = removeHeadElement(thirdAdd);
//		System.out.println(removeString);
//		
//	}
}
