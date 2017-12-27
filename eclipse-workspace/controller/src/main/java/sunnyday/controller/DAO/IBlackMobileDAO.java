package sunnyday.controller.DAO;


import org.springframework.stereotype.Repository;

import sunnyday.common.model.BlackMobile;

@Repository
public interface IBlackMobileDAO {
	/**
	 * 手机号码加黑
	 * @return
	 */
	public boolean addBlack(BlackMobile blackMobile);
	
	/**
	 * 解除黑名单
	 * @return
	 */
	public int deleteBlack(BlackMobile blackMobile);
	
	/**
	 * 解除业务级别黑名单
	 * @param blackMobile
	 * @return
	 */
	public int deleteServiceBlack(BlackMobile blackMobile);
}
