package sunnyday.controller.DAO;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.ThreadControllerForm;
import sunnyday.common.model.UserBalanceForm;

@Repository
public interface IControlDAO  {

	public Map<String, ThreadControllerForm> getNewThreadControllerInfo(String app_name);

	public void updateThreadControllerStatus(List<ThreadControllerForm> updateList);

 

	public Map<String, Map<String, Object>> getUserParam() ;

	
 

	public Map<String, UserBalanceForm> getDbBalanceInfo() ;

	public void updateUserBalanceInDb(List<UserBalanceForm> dbUpdateList);
}
