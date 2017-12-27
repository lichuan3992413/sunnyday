package sunnyday.controller.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.DeliverBean;

@Repository
public interface IDeliverDAO {

	public void insertIntoDeliverMessage(List<Object> list) ;
	
	public void insertIntoDeliverMessage(DeliverBean each) ;
	
	
	public void updateDeliverMessage(DeliverBean each);
	
	public void updateDeliverMessage(List<Object> list);
	
	public void saveBlackMobile(String mobile, String user_id,int type) ;

}
