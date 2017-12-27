package sunnyday.gateway.service;

import sunnyday.common.model.SubmitBean;

/*
 * 余额校验
 */
public interface BalanceVerifiable{
	
	int valid(SubmitBean submitBean);
	int valid_multi(SubmitBean submitBean);
	int do_valid(SubmitBean submitBean);
}
