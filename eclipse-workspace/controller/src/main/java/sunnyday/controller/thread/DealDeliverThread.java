package sunnyday.controller.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import sunnyday.common.model.AutoSendMsgConfig;
import sunnyday.common.model.AutoSendMsgContent;
import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.DeliverCommandModel;
import sunnyday.common.model.DeliverMatchInfo;
import sunnyday.common.model.ResultContent;
import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserServiceForm;
import sunnyday.common.model.UserSignForm;
import sunnyday.controller.DAO.IDeliverDAO;
import sunnyday.controller.cache.AutoSendMsgConfigCache;
import sunnyday.controller.cache.GateConfigCache;
import sunnyday.controller.cache.MatchDeliverSpNumberCache;
import sunnyday.controller.cache.SmsTemplateCache;
import sunnyday.controller.cache.UserInfoCache;
import sunnyday.controller.util.BaseDeliverDeal;
import sunnyday.controller.util.DeliverUtil;
import sunnyday.controller.util.EncodeResponse;
import sunnyday.controller.util.HSToolCode;
import sunnyday.controller.util.UtilTool;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.ParamUtil;
import sunnyday.tools.util.Spring;
@Service
public class DealDeliverThread extends Thread {
	private static final Logger log = CommonLogFactory.getCommonLog("infoLog");
	private boolean isRunnable = false;
	
	@Resource(name="${db.type}_DeliverDAO")
	private IDeliverDAO deliverDAO;
	
	public DealDeliverThread(){
		this.isRunnable = true;
	}

	@Override
	public void run() {
		log.info("---------------> 上行匹配处理线程启动 <---------------");
		while(isRunnable){
			try{
				DeliverBean deliver = getReceiveDeliver();
				
				if(deliver != null){
					if(log.isInfoEnabled()){
						log.info("DeliverBean[sp_number=["+deliver.getSp_number()+"] mobile=["+deliver.getMobile()+"] msg_content=["+deliver.getMsg_content()+"]]");
					}
					String deliver_msg = deliver.getMsg_content();
					if(deliver_msg!=null){
						deliver_msg = deliver_msg.trim();
						//上行内容不为空，获取到客户上行的指令信息
						DeliverCommandModel model = getDeliverCommandModel(deliver_msg);
					    if(model!=null){
					    	if(log.isInfoEnabled()){
					    		log.info("sp_number=["+deliver.getSp_number()+"] mobile=["+deliver.getMobile()+"] msg_content=["+deliver.getMsg_content()+"] match deliver command is " + model.toString());
					    	}
					    
					      	model.setDeliver_mobile(deliver.getMobile());
					    	model.setCommand(deliver_msg);
					    	String msg_content = "";
					    	//任意匹配
					    	if(model.getType()==3){
					    		//获取任意匹配的自动触发的内容
					    		if(model.getSend_switch()==0){
					    			msg_content = getAnyWayMatchSendContent(model);
					    		}
					    		
					    	}else{
					    		String class_name = "deliverDeal";
					    	  	if(model.getKeyword_type()==1){
						    		//添加黑名单
						    		class_name = class_name+"0000";
						    	}else if(model.getKeyword_type()==2){
						    		//解除黑名单
						    		class_name = class_name+"DY";
						    	}else{
						    		//其他指令类型
						    		String tmp_commnd = deliver_msg.split("#")[0];
						    		tmp_commnd = specailConvert(tmp_commnd);
						    		class_name = class_name+tmp_commnd.toUpperCase();
						    	}
						    	try {
						    		BaseDeliverDeal  deal = (BaseDeliverDeal) Spring.getApx().getBean(class_name);
						    		msg_content = deal.dealDeliver(model);
								} catch (Exception e) {
									log.error("deal deliver msg error! class_name=["+class_name+"] deliver msg_content=["+deliver_msg + "]",e);
								}
					    	}
					    	if(model.getSend_switch()==0&&StringUtils.isNotBlank(msg_content)){
				    			//发送短信
				    			sendMessage(model,msg_content);
				    		}
					    	
					    	
					    	
					    }else {
							log.warn("deliver content don't match deliver command, deliver content="+deliver_msg);
						}
					}else {
						log.warn("deliver msg_content is null "+deliver);
					}
					
					UserSignForm sign = matchCustomerInfo(deliver);
					if(deliver.getUser_id() != null){
						//匹配上行成功
						//处理上行退订问题
						dealDeliverTD(deliver, sign);
						setDeliverSendType(deliver);
						if(deliver.getSend_type()== ParamUtil.NO_NEED){
							//客户不需要上行，直接入库
							DataCenter_old.addSentDeliverToDBQueue(deliver);
						}else {
							DataCenter_old.addDealedDeliver(deliver);
						}
						
					}else{
						DataCenter_old.addSentDeliverToDBQueue(deliver);
					}
					
					
				}else{
					sleep(1000);
				}
			}catch(Exception e){
				log.error("", e);
			}
		}
	}


/*	private void dealAutoSendMsg(DeliverBean deliver) {
		//根据service_code匹配auto_send_msg_config表中的配置项
		try{
			AutoSendMsgConfigForm autoSendMsgConfigForm = matchKeyWord(deliver);
			auto_send_log.info("receive user_id="+deliver.getUser_id()+", service_code="+deliver.getService_code()+", mobile="+deliver.getMobile()+", msg_content="+deliver.getMsg_content()
					+", matchAutoSendMsgCofigForm="+autoSendMsgConfigForm);
			if(autoSendMsgConfigForm!=null){
				String mobile = deliver.getMobile();
				sendMsg(mobile,autoSendMsgConfigForm);
			}
			
			
		
		}catch(Exception e){
			if (log.isErrorEnabled()) {
				log.error("dealAutoSendMsg-> DeliverBean: "+deliver +" is fail",e);
				}
		}
	}*/

	/**
	 * 特殊处理<br>
	 * ? 问题查询
	 * 1 表示开通KT
	 * 
	 */
	private String specailConvert(String tmp_commnd) {
		
		if("?".equals(tmp_commnd.trim()) || "？".equals(tmp_commnd.trim())){
			tmp_commnd = "QUESTION";
		}
		
		if("1".equals(tmp_commnd.trim())){
			tmp_commnd = "KT";
		}
		
		return tmp_commnd;
	}

	private String getAnyWayMatchSendContent(DeliverCommandModel model) {
		String msg_content = "";
		Map<Integer,ResultContent> map = model.getResult_map();
		if(map!=null&&!map.isEmpty()){
			ResultContent rc = map.get(0);
			if(rc!=null){
				String tmp = rc.getContent();
				if(StringUtils.isNotBlank(tmp)){
					msg_content = tmp;
				}else{
					msg_content = "尊敬的客户，您上行的指令有误";
				}
			}
		}
		return msg_content;
		
	}
	
	/**
	 * 模拟上行信息
	 *
	 * @return
	 */
	private DeliverBean createDeliverBean() {
		DeliverBean tmp = new DeliverBean();
		tmp.setUser_id("NJYHUStest");
		tmp.setSp_number("106980592001");
		//tmp.setMobile("15722903258");
		tmp.setMobile("13774699223");
		tmp.setStatus(0);
		tmp.setTry_times(0);
		tmp.setSubmitTime(new Date().getTime());
		tmp.setSub_msg_id(110);
		tmp.setPk_total(0);
		tmp.setPk_number(0); 
		tmp.setMsg_format(8);
		tmp.setReveive_time(new Date().getTime());
		tmp.setSend_status(0);
		tmp.setSend_type(0);
		tmp.setExt_code("");
		tmp.setSrc_spNumner("");//收时的上行接入号
		tmp.setInsert_time("");//入库时间
		tmp.setUpdate_time("");//修改时间
		tmp.setService_code("");
        tmp.setMsg_content(" jy#7217 ");
		//tmp.setContent("MX#0051#20320301#20320321");
		//tmp.setContent("Y");
		tmp.setIs_encode(0);
		tmp.setProvince("");//修改时间
		tmp.setCity("");//修改时间
		return tmp;
	}

	public void sendMessage(DeliverCommandModel model, String msg_content) {
		UserBean userBean = UserInfoCache.getUser_info(model.getUser_id());
		if(userBean!=null){
			String pwd = UtilTool.getPassword(userBean);
			String URL = GateConfigCache.getValue(ParamUtil.TIME_SEND_URL_KEY);
			StringBuilder sb = new StringBuilder();
			sb.append(model.getDeliver_mobile()).append(ParamUtil.SPLIT).append("").append(ParamUtil.SPLIT).append("").append(ParamUtil.SPLIT)
					.append(msg_content).append(ParamUtil.GROUP);
			
			
			HttpUtil4.postGXHQFMsg(URL, model.getUser_id(), pwd, model.getSend_service_code(), 1, sb.delete(sb.length() - ParamUtil.GROUP.length(), sb.length()).toString());
		}
		
	}

	private DeliverCommandModel getDeliverCommandModel(String deliver_msg) {
		Map<Integer, List<AutoSendMsgConfig>> commandMap = AutoSendMsgConfigCache.getDeliverCommandInfo();
		boolean isMatch = false;
		DeliverCommandModel model = new DeliverCommandModel();
		if(commandMap!=null&&!commandMap.isEmpty()){
			isMatch = all_match(deliver_msg,commandMap,model);
			log.info("all_match result:[deliver_msg=" + deliver_msg + "], [isMatch=" + isMatch + "]");
			
			if(!isMatch){
				isMatch = template_match(deliver_msg,commandMap,model);
				log.info("template_match result:[deliver_msg=" + deliver_msg + "], [isMatch=" + isMatch + "]");
			}
			if(!isMatch){
				isMatch = contains_match(deliver_msg,commandMap,model);
				log.info("contains_match result:[deliver_msg=" + deliver_msg + "], [isMatch=" + isMatch + "]");
			}
			
			if(!isMatch){
				isMatch = anyway_match(deliver_msg,commandMap,model);
				log.info("anyway_match result:[deliver_msg=" + deliver_msg + "], [isMatch=" + isMatch + "]");
			}
		}
		
		if(isMatch){
			return model;
		}else{
			return null;
		}
	}

	private boolean anyway_match(String deliver_msg,
			Map<Integer, List<AutoSendMsgConfig>> commandMap,
			DeliverCommandModel model) {
		boolean isMatch = false;
		List<AutoSendMsgConfig> list = commandMap.get(ParamUtil.ANYWAY);
		if(list!=null&&!list.isEmpty()){
			AutoSendMsgConfig each = list.get(0);
			dealCommandModel(model, each);
			isMatch = true;
			
		}
	
	return isMatch;
	}

	private boolean contains_match(String deliver_msg,
			Map<Integer, List<AutoSendMsgConfig>> commandMap,
			DeliverCommandModel model) {
		boolean isMatch = false;
		List<AutoSendMsgConfig> list = commandMap.get(ParamUtil.CONTAINS);
		if(list!=null&&!list.isEmpty()){
			for(AutoSendMsgConfig each : list){
				try{
					if(deliver_msg.contains(each.getKeyword())){
						dealCommandModel(model, each);
						isMatch = true;
						break;
					}
				}catch(Exception e){
					log.error("match_mode=contains deliver msg matches error keyword="+each.getKeyword(),e);
				}
				
			}
		}
	
	return isMatch;
	}

	public void dealCommandModel(DeliverCommandModel model,
			AutoSendMsgConfig each) {
		model.setSn(each.getSn());
		model.setKeyword(each.getKeyword());
		model.setStatus(each.getStatus());
		model.setKeyword_type(each.getKeyword_type());
		model.setSend_switch(each.getSend_switch());
		model.setMatch_type(each.getMatch_type());
		model.setUser_id(each.getUser_id());
		model.setSend_service_code(each.getSend_service_code());
		model.setMatch_service_code(each.getMatch_service_code());
		model.setType(each.getType());
		List<AutoSendMsgContent> tmpList = AutoSendMsgConfigCache.getAutoSendMsgCotent(each.getKeyword());
		if(tmpList!=null&&tmpList.size()>0){
			Map<Integer,ResultContent> resultContentMap = new HashMap<Integer, ResultContent>();
			for(AutoSendMsgContent asmc : tmpList){
				ResultContent rc = new ResultContent();
				String msg_content = asmc.getMsg_content();
				//模板
				if(asmc.getMsg_content_type()==1){
					Map<String, SmsTemplateInfo> templateMap = SmsTemplateCache.getSms_common_template();
					if(templateMap!=null&&!templateMap.isEmpty()){
						rc.setSmsTemplateInfo(templateMap.get(msg_content));
					}
					
				}else{
					rc.setContent(msg_content);
				}
				rc.setType(asmc.getMsg_content_type());
				resultContentMap.put(asmc.getContent_id(), rc);
			}
			model.setResult_map(resultContentMap);
		}
	}

	private boolean template_match(String deliver_msg,
			Map<Integer, List<AutoSendMsgConfig>> commandMap,
			DeliverCommandModel model) {
		boolean isMatch = false;
		List<AutoSendMsgConfig> list = commandMap.get(ParamUtil.TEMPLATE);
		if(list!=null&&!list.isEmpty()){
			for(AutoSendMsgConfig each : list){
				try{
					if(deliver_msg.matches(each.getKeyword())){
						dealCommandModel(model, each);
						isMatch = true;
						break;
					}
				}catch(Exception e){
					log.error("match_mode=template deliver msg matches error keyword="+each.getKeyword(),e);
				}
			
			}
		}
	
	return isMatch;
		
	}

	private boolean all_match(String deliver_msg,
			Map<Integer, List<AutoSendMsgConfig>> commandMap,
			DeliverCommandModel model) {
			boolean isMatch = false;
			List<AutoSendMsgConfig> list = commandMap.get(ParamUtil.ALL_MATCH);
			if(list!=null&&!list.isEmpty()){
				for(AutoSendMsgConfig each : list){
					try{
						if(deliver_msg.matches(each.getKeyword())){
							dealCommandModel(model, each);
							isMatch = true;
							break;
						}
					}catch(Exception e){
						log.error("match_mode=all_match deliver msg matches error keyword="+each.getKeyword(),e);
					}
					
				}
			}
		
		return isMatch;
	}

	/**
	 * 
	 * @param mobile
	 * @param autoSendMsgConfigForm
	 * 	String corp_id = request.getParameter("corp_id");
		String corp_pwd = request.getParameter("corp_pwd");
		String corp_service = request.getParameter("corp_service");
		String mobiles = request.getParameter("mobile");
		String msg_content = request.getParameter("msg_content");
		String msg_id = request.getParameter("corp_msg_id");
		String ext = request.getParameter("ext");//用户提交时的扩展
	 */

//	private void sendMsg(String mobile,AutoSendMsgConfigForm autoSendMsgConfigForm) {
//		if(autoSendMsgConfigForm!=null){
//			String user_id = autoSendMsgConfigForm.getUser_id();
//			String service_code = autoSendMsgConfigForm.getSend_service_code();
//			UserBean userBean = UserInfoCache.getUser_info(user_id);
//			String msg_content = autoSendMsgConfigForm.getMsg_content();
//			String ip = GateConfigCache.getValue("plate_sms_send_ip");
//			String port = GateConfigCache.getValue("plate_sms_send_port");
//			if (ip == null) {
//				ip = LOCAL_IP;
//			}
//			
//			if(port == null){
//				port = "8080";
//			}
//			StringBuilder sb = new StringBuilder();
//			sb.append("http://").append(ip).append(":").append(port).append("/sms_send2.do");
//			String url = sb.toString();
//			if(userBean!=null){
//				httpSendMsg(user_id, getPassword(userBean), service_code, mobile, msg_content, url);
//			}
//		}
//		
//
//		
//	}

//	private void httpSendMsg(String user_id, String user_pwd,
//			String service_code, String mobile, String msg_content, String url) {
//		HttpPost httppost = null;
//		String responseContent = "";
//		
//		try{
//			httppost = new HttpPost(url);
////			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("corp_id", user_id));
//			params.add(new BasicNameValuePair("corp_pwd", user_pwd));
//			params.add(new BasicNameValuePair("corp_service", service_code));
//			params.add(new BasicNameValuePair("mobile", mobile));
//			params.add(new BasicNameValuePair("msg_content", msg_content));
//			httppost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
//			HttpResponse response = httpclient.execute(httppost);
//			HttpEntity responseEntity = response.getEntity();
//			responseContent = EntityUtils.toString(responseEntity);
//			auto_send_log.info("auto send msg corp_id="+user_id+", corp_pwd="+user_pwd+", corp_service="+service_code+", mobile="+mobile+", msg_content="+msg_content);
//			auto_send_log.info("receive sendResp corp_id="+user_id+", mobile="+mobile+", result="+responseContent);
//		} catch (Exception e) {
//			if(auto_send_log.isErrorEnabled()){
//				auto_send_log.error("corp_id="+user_id+", corp_pwd="+user_pwd+", corp_service="+service_code+", mobile="+mobile+", msg_content="+msg_content,e);
//			}
//		} finally {
//			try{
//				if (httppost != null){
//					httppost.abort();
//				}
//			}catch(Exception e ){
//				auto_send_log.error("httppost.abort() exception",e);
//			}
//		}
//	
//		
//	}


	/*private void setDeliverSendType(DeliverBean deliver) {
		int type = DeliverUtil.getUserDeliverType(deliver.getUser_id());
		deliver.setSend_type(type);
		if(type== ParamUtil.NO_NEED){
			//即客户不需要接收上行信息
			deliver.setSend_status(-1);
			deliver.setStatus(-1);
		}
	}*/

	private DeliverBean getReceiveDeliver() {
		return DataCenter_old.getReceiveDeliver();
	}

	 
	 
	public static void main(String[] args) {
		String[] array = "TCBFG".split("#");
		System.out.println(array[0]);
	}
	 
	
	 
	
	/**
	 * 处理退订
	 * @param deliver
	 * @param signInfo
	 */
	private void dealDeliverTD(DeliverBean deliver, UserSignForm signInfo) {
			if(deliver != null && deliver.getMsg_content() != null && deliver.getMsg_content().length() > 0 && signInfo != null && signInfo.getIs_add_msg_use() == 0){
				List<String> list=dealUserSign(signInfo.getUnsubscribe_msg());
				if(list!=null&&list.size()>0){
					for(String str:list){
						if(deliver.getMsg_content().equals(str)){
							deliverDAO.saveBlackMobile(deliver.getMobile(), signInfo.getUser_id(),1);
							break;
						}
					}
				}
			}
	}
	
	private void dealDeliverTD(DeliverBean deliver, DeliverMatchInfo match) {
		if (deliver != null && deliver.getMsg_content() != null && deliver.getMsg_content().length() > 0) {
			if (match.isIs_td()) {
				if(match.getTemplate_id()!=null){
					deliverDAO.saveBlackMobile(deliver.getMobile(), match.getTemplate_id(),5);
				}else{
					deliverDAO.saveBlackMobile(deliver.getMobile(),deliver.getUser_id(),1);
				}
			} else {
				String unsubscribeMsg = UtilTool.getUnsubscribeMsg();
				Set<String> set = dealSign(unsubscribeMsg);
				log.info(set+"|"+match+"|"+deliver);
				if (set != null && !set.isEmpty()&&set.contains(deliver.getMsg_content())) {
					if(match.getTemplate_id()!=null){
						deliverDAO.saveBlackMobile(deliver.getMobile(), match.getTemplate_id(),5);
					}else{
						deliverDAO.saveBlackMobile(deliver.getMobile(),deliver.getUser_id(),1);
					}
				}
			}
		}
	}
	
	@Override
	public boolean doStop() {
		this.isRunnable = false;
		log.info("--------< 上行匹配线程关闭   >--------");
		interrupt();
		return false;
	}
	
	private List<String> dealUserSign(String sings){
		List<String> result = new ArrayList<String>();
		if(sings!=null&&sings.length()>0){
			String[] array = sings.split(",");
			for(String str:array){
				if(!"".equals(str)){
					result.add(str.trim());
				}
			}
		}
		return result;
	}
	
	private Set<String> dealSign(String sings){
		Set<String> result = new HashSet<String>();
		if(sings!=null&&sings.length()>0){
			String[] array = sings.split(",");
			for(String str:array){
				if(!"".equals(str)){
					result.add(str.trim());
				}
			}
		}
		return result;
	}
	
	public static String getPassword(UserBean userBean){
		
		int is_encrypt = 1;
		String is_encrypt_tmp = (String) userBean.getParamMap().get("is_encrypt");
		if(is_encrypt_tmp!=null){
			try{
			is_encrypt = Integer.parseInt(is_encrypt_tmp);
			}catch(Exception e){
				log.error("", e);
			}
		}
		EncodeResponse result = null;
		String pwd= userBean.getUser_pwd();
		if(is_encrypt==0){
			result = HSToolCode.decoded(pwd);
			if(result.isSuccess()){
				pwd = result.getContent();
			}
		}
	return pwd;
}
	
	private UserSignForm matchCustomerInfo(DeliverBean each) {
		String user_id = null;
		String fullSpNum = each.getSp_number();
		String fullSubmitCode = "";
		UserSignForm usf = null;
		each.setSrc_spNumner(each.getSp_number());
		
		UserServiceForm matchedService =matchSpNumber(fullSpNum);
		if (matchedService != null) {
			user_id = matchedService.getUser_id();
			String all_extCode = fullSpNum.substring(matchedService.getTd_sp_number().length());
			String service_ext =  matchedService.getExt_code();
			String ext = "";
			 if(all_extCode.startsWith(service_ext)){
				 ext  = all_extCode.substring(service_ext.length(),all_extCode.length());
			 } 
			each.setExt_code(ext);
			fullSubmitCode = matchedService.getService_code() + ext;
			each.setSp_number(matchedService.getService_code()+ext);
			each.setService_code(matchedService.getService_code());
			each.setUser_id(user_id);
			
		}
		if(user_id!=null){
			usf = matchChildCode(user_id,fullSubmitCode);
		}
		return usf;
	}
	/**
	 * 匹配成功：返回客户的 user_id
	 * 匹配失败：返回 null
	 * @param sp_number
	 * @return
	 */
	public  UserServiceForm  matchSpNumber(String sp_number){
		
		Map<String,UserServiceForm>  deliverMatchSpNumberCache = MatchDeliverSpNumberCache.getDeliverMatchSpNumberCache();
		UserServiceForm result = null;
		
		try {
			if(sp_number!=null&&deliverMatchSpNumberCache!=null){
				String tmp_key = null;
				for(String key:deliverMatchSpNumberCache.keySet()){
					if(sp_number.startsWith(key)){
						if (log.isDebugEnabled()) {
						log.debug("matchSpNumber-> sp_number: "+sp_number+" --> "+key);
						}
						
						if(sp_number.equals(key)){
							tmp_key = key;
							break ;
						}
						if(tmp_key!=null){
							if(key.length()>=tmp_key.length()){
								tmp_key = key;
							}
						}else {
							tmp_key = key;
						} 
					}
				}
				if(tmp_key!=null){
					result = deliverMatchSpNumberCache.get(tmp_key);
				}
			}
			 
		} catch (Exception e) {
			log.error("",e);
		}
		return result ;
	}
	
	private UserSignForm matchChildCode(String user_id,String fullSubmitCode) {
		UserSignForm result = null;
		Map<String, List<UserSignForm>> tmpMap = UserInfoCache.getUser_sign_info();
		
		if(fullSubmitCode != null && tmpMap != null){
			List<UserSignForm> tmpList = tmpMap.get(user_id);
			for (UserSignForm each : tmpList) {
				if (fullSubmitCode.startsWith(each.getGate_sp_number())) {
					if (log.isDebugEnabled()) {
						log.debug("matchChildCode-> fullSubmitCode: "
								+ fullSubmitCode + "; gate_sp_number: "
								+ each.getGate_sp_number());
					}
					if (fullSubmitCode.equals(each.getGate_sp_number())) {
						result = each;
						break;
					}
					if (result == null) {
						result = each;
					} else if (result.getGate_sp_number().length() < each
							.getGate_sp_number().length()) {
						result = each;
					}
				}
			}
			
		}
		if (log.isDebugEnabled()) {
		log.debug("matchChildCode-> fullSubmitCode: "+fullSubmitCode+" , result: "+result);
		}
		return result;
	}
	
	private void setDeliverSendType(DeliverBean deliver) {
		int type = DeliverUtil.getUserDeliverType(deliver.getUser_id());
		deliver.setSend_type(type);
		if(type== ParamUtil.NO_NEED){
			//即客户不需要接收上行信息
			deliver.setSend_status(-1);
			deliver.setStatus(-1);
		}
	}
	
}
