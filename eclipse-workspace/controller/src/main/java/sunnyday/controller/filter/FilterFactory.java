package sunnyday.controller.filter;

public class FilterFactory {
	public static final int REPEAT_FILTER = 1;
	
	public static final int REPEAT_FILTER_MEMCACHE = 2;
	
	public static final int REPEAT_FILTER_PRO = 3;
	public static final int REPEAT_FILTER_REDIS = 4;
	
	public static IFilter getMsgFilter(int filterType){
		switch(filterType){
		case REPEAT_FILTER: return new EhcacheRepeatMsgFilter3();
//		case REPEAT_FILTER_MEMCACHE: return new MemcacheRepeatMsgFilter();
		case REPEAT_FILTER_PRO: return new EhcacheRepeatMsgFilter2();
//		case REPEAT_FILTER_REDIS: return new RedisRepeatMsgFilter();
		default: return null;
		}
	}
	
	public static IFilter getMsgFilter(int filterType,String redisType){
		switch(filterType){
		case REPEAT_FILTER: return new EhcacheRepeatMsgFilter3();
//		case REPEAT_FILTER_MEMCACHE: return new MemcacheRepeatMsgFilter();
		case REPEAT_FILTER_PRO: return new EhcacheRepeatMsgFilter2();
//		case REPEAT_FILTER_REDIS: return new RedisRepeatMsgFilter();
		default: return null;
		}
	}
}
