package com.hskj.deal.deliverDeal;


import org.slf4j.Logger;

import sunnyday.common.model.DeliverCommandModel;
import sunnyday.tools.util.CommonLogFactory;

public abstract class BaseDeliverDeal {
	protected Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	
	public abstract String dealDeliver(DeliverCommandModel cmommand);
}
