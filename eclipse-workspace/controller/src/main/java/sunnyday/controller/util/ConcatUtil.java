package sunnyday.controller.util;

public class ConcatUtil {
	private static final String JOIN = " --> ";
	public  static String concat(String fail_desc, String suffix){
		if(fail_desc == null || fail_desc.trim().equals("")){
			return suffix;
		}
		return fail_desc + JOIN + suffix;
	}
}
