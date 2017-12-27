package sunnyday.controller.filter;

public interface IFilter {

	public void doStart(String filterName);
	
	public void doStop();
	
	public Object doFilter(String key, int idleSecond, int repeatTime);
	/**
	 * 短信重复下发二级过滤
	 * @param pk_toal
	 * @param key_content 
	 * @param key_word 
	 * @param idleSecond
	 * @param repeatTime
	 * @return
	 */
	public Object doFilter(int pk_toal,String key_content,String key_word, int idleSecond, int repeatTime);
	
}
