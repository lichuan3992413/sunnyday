package sunnyday.controller.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.CustInfo;

@Repository
public interface ICustInfoDAO {
	
	public int updateCustInfoByMobileAndStatus(String mobile, int status);
	
	public CustInfo queryPersonalCustInfoByMobileAccno(String mobile,String accno);
	
	public CustInfo queryCommonCustInfoByMobileAccno(String mobile,String accno);
	
	/**
	 * 是否存在指定手机号的对私客户
	 * 
	 * @param mobile
	 * @return
	 */
	public boolean existCustInfoByMobile(String mobile);
	
	/**
	 * 根据手机号，客户类型，状态查找客户签约信息
	 * 
	 * @param mobile
	 * @param custType
	 * @param status
	 * @return
	 */
	public List<CustInfo> queryAllPersonalCustInfo(String mobile, String custType, String status);
}
