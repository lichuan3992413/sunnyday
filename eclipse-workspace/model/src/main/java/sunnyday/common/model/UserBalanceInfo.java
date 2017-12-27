package sunnyday.common.model;

import java.io.Serializable;

import sunnyday.adapter.redis.Chargable;
import sunnyday.tools.util.ArithUtil;

public class UserBalanceInfo implements Chargable,Serializable{
	 
	private static final long serialVersionUID = -4841380629352008812L;
	private String user_id;
	private double balance; //当前的余额(尚未扣除代扣费的值)
	private double debt_cache;//待扣除的金额
	private double charge_sum;//本次需要扣除金额
	
	 
	
	 

	@Override
	public String toString() {
		return "[user_id=" + user_id + ", balance=" + balance
				+ ", debt_cache=" + debt_cache + ", charge_sum=" + charge_sum
				+ "]";
	}

	//进行扣费
	public synchronized boolean charge(double charge_count) {
		boolean result = false;
		double tmp1 = ArithUtil.sub(balance, debt_cache);
		double tmp2 = ArithUtil.sub(tmp1, charge_count);
		if (tmp2 >= 0) {
			debt_cache = ArithUtil.add(debt_cache, charge_count);
			result = true;
		}
		return result;
	}
	 
	/**
	 * 获取待扣除的金额
	 */
	public synchronized double getCharge_sum() {
		return charge_sum;
	}
	
	public synchronized void setCharge_sum(double charge_sum){
		this.charge_sum = charge_sum;
	}
	
	public synchronized void SyncDebt(double old){
		debt_cache =  ArithUtil.sub(debt_cache, old);
	}

	public synchronized  void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public synchronized String getUser_id() {
		return user_id;
	}

	/**
	 * 获取客户当前的余额
	 * @return
	 */
	public synchronized double getBalance() {
		double result = 0;
		result =  ArithUtil.sub(balance, debt_cache);
		return result;
	}

	public synchronized void setBalance(double balance) {
		this.balance = balance;
	}

	public synchronized double getDebt_cache() {
		return debt_cache;
	}

	public synchronized  void setDebt_cache(double debt_cache) {
		this.debt_cache = debt_cache;
	}
}
