package sunnyday.gateway.util;

public class ConcatUtil {
	private static final String JOIN = " --> ";
	public synchronized static String concat(String fail_desc, String suffix){
		if(fail_desc == null || fail_desc.trim().equals("")){
			return suffix;
		}
		return fail_desc + JOIN + suffix;
	}
}
