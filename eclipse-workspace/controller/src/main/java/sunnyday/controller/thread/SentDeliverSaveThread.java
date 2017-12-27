package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.hskj.utils.EncodeResponse;
import com.hskj.utils.HSToolCode;
import com.hskj.utils.UtilTool;

import sunnyday.common.model.DeliverBean;
import sunnyday.controller.DAO.IDeliverDAO;
import sunnyday.tools.util.CommonLogFactory;
@Service
public class SentDeliverSaveThread extends ObjectWriteThread{
	private Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	
	
	@Resource(name="${db.type}_DeliverDAO")
	private IDeliverDAO deliverDAO;
	@Override
	protected Object getOneMessage() {
		return DataCenter_old.getSentDeliverToDB();
	}

	@Override
	protected String getGroupKey(Object smsObj) {
		return "sendDeliverMessage";
	}

	@Override
	protected void writeList(List<Object> list) {
		List<Object> insert_list = new ArrayList<Object>();
		if(list!=null){
			for(Object obj:list){
				DeliverBean each = (DeliverBean)obj;
				if (UtilTool.isEncode()) {
					if (each.getIs_encode() == 0) {
						EncodeResponse rep = HSToolCode.encoded(each.getMsg_content());
						if(rep.isSuccess()){
							each.setIs_encode(1);
							each.setMsg_content(rep.getContent());
						}
						
					}
				}

				info_log.info("to db  DeliverBean->: "+each);
				insert_list.add(obj);
			}
		}
		deliverDAO.insertIntoDeliverMessage(insert_list);
	}

}
