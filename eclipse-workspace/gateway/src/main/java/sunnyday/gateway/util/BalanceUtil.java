package sunnyday.gateway.util;

import org.springframework.stereotype.Service;

import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;

@Service
public class BalanceUtil {

	public static int updateUserBalance(UserBean user, double balance) {
		int result = 0;
		if(user.getUser_type()==0){
			UserBalanceInfo userBalanceInfo = DataCenter.getUserBalanceMap().get(user.getUser_id());
			if(null != userBalanceInfo){
				if(!userBalanceInfo.charge(balance)){
					result = 9;
				}
			}else{
				result = 9;
			}
		}
		return result;
	}
	
	

}
