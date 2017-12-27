package sunnyday.gateway.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.ReportBean;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;

/**
 * 百悟查询状态报告接口
 * @author 1307365
 * 获取状态报告信息，若获取的上行信息不存在则返回NULL
 */
@Service
public class ReportServiceImpl implements ISmsService{
	private int LIMIT = 100;
	private int report_frequency = 200 ;

	public HSResponse doSomething(CommonParameter param) {
		String corp_id = param.getCorp_id();
		String corp_pwd = param.getCorp_pwd();
		String requestIP = param.getRequest_ip();
		String TYPE = param.getType();
		 try {
			 LIMIT = Integer.parseInt(param.getOther().get("limit").toString());
			} catch (Exception e) {
		}
		
		//判断用户是否存在
        if(corp_id == null){
        	LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,ErrorCodeUtil.common_user_id_error, "参数:corp_id未填写",TYPE);
        	return new HSResponse(ErrorCodeUtil.common_user_id_error,"账号参数非法");
        }
        UserBean userInfo = DataCenter.getUserBean(corp_id.trim());
        if(userInfo==null||userInfo.getStatus() == 1){
        	LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,ErrorCodeUtil.common_user_id_error, "用户["+corp_id+"]不存在或者已经关闭",TYPE);
        	return new HSResponse(ErrorCodeUtil.common_user_id_error,"用户名不存在");
        }else {
        	try {
        		//重置客户访问频率限制
        		String tmp = (String) userInfo.getParamMap().get("report_frequency");
        		if(tmp!=null){
        			report_frequency = Integer.parseInt(tmp);
        		}
			} catch (Exception e) {
			}
			
		}
		 
        
        //校验用户访问ip
        if (requestIP!=null&&userInfo.getUser_ip() != null && userInfo.getUser_ip().trim().length() > 0) {
        	String[] ips=userInfo.getUser_ip().split(",");
        	boolean flag=false;
        	for (String ip_every : ips) {
				ip_every = ip_every.replace(".", "\\.").replace("*", "[0-9]{1,3}");
				if(requestIP.matches(ip_every)){
					flag =  true;
					break;
				}
        	}
        	if(!flag){
        		LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,ErrorCodeUtil.common_user_ip_error, "非法IP访问,所允许的IP为["+userInfo.getUser_ip()+"]",TYPE);
            	return new HSResponse(ErrorCodeUtil.common_user_ip_error,"非法IP访问");
        	}
        }
  
        
		//校验访问频率
		if(DataCenter.getUser_report_count().get(corp_id) == null){
			DataCenter.getUser_report_count().put(corp_id, System.currentTimeMillis());
		}else{
			long time = System.currentTimeMillis() - DataCenter.getUser_report_count().get(corp_id) ;
			if(time < report_frequency){
				LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,ErrorCodeUtil.common_fast_error, "访问"+TYPE+"超过限制频率（间隔应200ms以上，客户的间隔为["+time+"ms]）",TYPE);
	        	return new HSResponse(ErrorCodeUtil.common_fast_error,"访问超过限制频率");
			}else{
				DataCenter.getUser_report_count().put(corp_id, System.currentTimeMillis());
			}
		}
		
		
		//校验用户是否支持主动获取
		if(userInfo.getReport_type()!=ParamUtil.USER_GET){
			//LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,ErrorCodeUtil.common_get_report_no_root, "该客户["+corp_id+"]不支持主动获取状态报告",TYPE);
        	return new HSResponse(ErrorCodeUtil.common_get_report_no_root,"不支持主动获取状态报告");
		}
		
		List<ReportBean> list = DataCenter.getReportBean(userInfo.getUser_id(),LIMIT);
	 
		if (list!=null&&list.size()<=0) {
			//LogUtil.writeReceiverLog(corp_id, corp_pwd,requestIP,"0", "尚无状态报告产生",TYPE);
			return  new HSResponse(ErrorCodeUtil.NO_REPORTS,"尚无状态报告产生");
		} else {
			HSResponse  hp  = new HSResponse("1000","获取状态报告["+(list!=null?list.size():0)+"]条");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reports", list);
			hp.setOther(map);
			DataCenter.addReportRespMessage(list);
			return hp;
		}
	}
	 

	 

	
	
}
