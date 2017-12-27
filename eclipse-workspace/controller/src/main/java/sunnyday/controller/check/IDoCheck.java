package sunnyday.controller.check;

import sunnyday.common.model.SmsMessage;

public interface IDoCheck {

	/**
	 * 
	 * @param msg
	 * @return
	 * 		0：不通过
	 * 		1：通过
	 */
	int doCheck(SmsMessage msg);//基础审核接口，返回审核状态值
	
	

}
