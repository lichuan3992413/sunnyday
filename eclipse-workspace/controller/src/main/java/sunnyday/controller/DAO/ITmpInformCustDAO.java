package sunnyday.controller.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.TmpInformCust;

@Repository
public interface ITmpInformCustDAO {
	/**
	 * 获取所有待确认签约账户记录
	 * @return
	 */
	public List<TmpInformCust> getTmpInformCustByMobile(String mobile);
	
	public int updateTmpInformCustByMobile(String mobile);
}
