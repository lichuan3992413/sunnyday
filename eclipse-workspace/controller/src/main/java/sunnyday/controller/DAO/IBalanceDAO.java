package sunnyday.controller.DAO;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.BalanceNoticeForm;
import sunnyday.common.model.UserBalanceForm;

@Repository
public interface IBalanceDAO {
	/**
	 * 获取所有的余额预警的配置
	 * @return
	 */
	public List<BalanceNoticeForm> getAllBalanceNotice();
	
	/**
	 * 获取所有客户的余额信息
	 * key:user_id
	 * @return
	 */
	public Map<String,UserBalanceForm> getAllUserBalanceForm();


	/**
	 * 更改预警配置中，通知状态的值
	 * @param sn
	 * @param status
	 * @return
	 */
	public boolean updateBalanceNoticeSarningStatus(long sn,int status) ;
 

}
