package sunnyday.common.model;

import sunnyday.adapter.redis.Chargable;

public class UserBalanceForm implements Chargable{
	/**
	 * 用户id
	 */
	private String user_id;
	/**
	 * 上次同步时候的余额
	 */
	private double last_balance;
	/**
	 * 库里最新的余额
	 */
	private double cur_balance;
	/**
	 * 两次同步之间余额的增量
	 */
	private double add_balance;
	
	/**
	 * 持久化数据时，实际变化的量
	 */
	private double changeBalance ;
	
	/**
	 * 内存中扣除的金额
	 */
	private double diffBalance ;
	
	
	
	
	
 
	 
	
	@Override
	public String toString() {
		return "UserBalanceForm [user_id=" + user_id + ", last_balance="
				+ last_balance + ", cur_balance=" + cur_balance
				+ ", add_balance=" + add_balance + ", changeBalance="
				+ changeBalance + ", diffBalance=" + diffBalance + "]";
	}


	/**
	 * 内存中扣除的金额
	 */
	public double getDiffBalance() {
		return diffBalance;
	}


	public void setDiffBalance(double diffBalance) {
		this.diffBalance = diffBalance;
	}
	
	public void setDiffBalance(String diffBalance) {
		this.diffBalance = Double.parseDouble(diffBalance);
	}


	public double getChangeBalance() {
		
		return  changeBalance;
	}
	
    public String getChangeBalance_string() {

    //	return ArithUtil.df.format(changeBalance);
    	return null;
	}


	public void setChangeBalance(double changeBalance) {
		this.changeBalance = changeBalance;
	}
	
	public void setChangeBalance(String changeBalance) {
		this.changeBalance = Double.parseDouble(changeBalance);
	}

	public void setLast_balance(double last_balance) {
		this.last_balance = last_balance;
	}
	public void setLast_balance(String last_balance) {
		this.last_balance = Double.parseDouble(last_balance);
	}
	public double getLast_balance() {
		return last_balance;
	}
	public void setCur_balance(double cur_balance) {
		this.cur_balance = cur_balance;
	}
	public void setCur_balance(String cur_balance) {
		this.cur_balance = Double.parseDouble(cur_balance);
	}
	public double getCur_balance() {
		return cur_balance;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setAdd_balance(double add_balance) {
		this.add_balance = add_balance;
	}
	public void setAdd_balance(String add_balance) {
		this.add_balance = Double.parseDouble(add_balance);
	}
	public double getAdd_balance() {
		return add_balance;
	}
	 
	public double getCharge_sum() {
		return add_balance;
	}

 
	
}
