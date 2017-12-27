package sunnyday.adapter.redis;

public interface Chargable {
	/**
	 * 返回计费客户的user_id
	 * @return string
	 */
	public String getUser_id();
	
	/**
	 * 获取计费金额
	 * @return double
	 */
	public double getCharge_sum();
}
