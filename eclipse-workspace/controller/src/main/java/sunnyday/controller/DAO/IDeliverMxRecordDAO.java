package sunnyday.controller.DAO;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.DeliverMxRecord;

@Repository
public interface IDeliverMxRecordDAO {

	public DeliverMxRecord queryDeliverMxRecordByMobile(String mobile);
	
	public int deleteDeliverMxRecord(String mobile);
	
	public boolean addDeliverMxRecord(DeliverMxRecord deliverMxRecord);
	
	public boolean updateDeliverMxRecord(DeliverMxRecord deliverMxRecord);
}
