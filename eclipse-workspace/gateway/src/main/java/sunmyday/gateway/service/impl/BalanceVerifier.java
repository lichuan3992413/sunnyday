package sunmyday.gateway.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.CountryPhoneCodeInfo;
import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.common.model.SignInfoForm;
import sunnyday.common.model.SubmitBean;
import sunnyday.common.model.TdInfo;
import sunnyday.common.model.TdSignInfo;
import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserServiceForm;
import sunnyday.gateway.cache.LocationInfoCache;
import sunnyday.gateway.cache.NetSwitchedMobileCache;
import sunnyday.gateway.cache.ServiceInfoCache;
import sunnyday.gateway.cache.TdInfoCache;
import sunnyday.gateway.cache.UserBeanCache;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.gateway.service.BalanceVerifiable;
import sunnyday.gateway.util.BalanceUtil;
import sunnyday.tools.util.CommonLogFactory;

public class BalanceVerifier implements BalanceVerifiable,Serializable {

	private static final long serialVersionUID = 1L;
	private transient Logger log = CommonLogFactory.getLog("gateway");
	private static BalanceVerifier  balanceValidImpl =null;

	public  static  BalanceVerifier Instance(){
		if(balanceValidImpl==null){
			balanceValidImpl = new BalanceVerifier();
		}
		return balanceValidImpl;
	}

	private int changeFormat(SubmitBean submitBean){
		int result =submitBean.getMsg_format();
		if(submitBean.getMsg_format()!=0){
			String content =submitBean.getContent();
			int leng_str = content.length();
			int leng_bye = 0;
			byte[] array = null;
			try {
				array = content.getBytes("utf-8");
				leng_bye = array.length;
			} catch (Exception e) {
			}
			if(leng_str==leng_bye){
				result = 0;
			}
		}
		return result ;
	}
	public int valid(SubmitBean submitBean) {
		StringBuffer sb = new StringBuffer();
		long time0 = System.currentTimeMillis();
		long time1 = System.nanoTime();
		String user_id = submitBean.getUser_id();
		try {
			if(DataCenter.getUse_code_0().contains(user_id)){
				submitBean.setMsg_format(changeFormat(submitBean));
			}
		} catch (Exception e) {
		}
		
		int result = 0;
		UserBean user=null;
		String submitSpNum = "";
		List<UserServiceForm> userServiceList =null;
		String mobile_type ="";
		try {
			user = UserBeanCache.getUserBean(submitBean.getUser_id());
			if(user==null){
				user = UserBeanCache.getUserBeanMap().get(submitBean.getUser_id().trim());
				if(user==null){
					log.warn(submitBean.getUser_id()+" is null ."+UserBeanCache.getUserBeanMap());
					submitBean.setResp(47);
					return 47;	
				}

			}
			submitSpNum = submitBean.getSp_number();
			// 从缓存中取得该账户该类型的所有业务
			if(null != user.getUser_sp_number() && !user.getUser_sp_number().equals("")){
				submitSpNum = "";
			}
			long time2 = System.nanoTime();
			sb.append("\n初始数据: ").append(time2-time1).append(" ns").append("\n");
			// 校验短信类型，设置国际短信单价 处理国家区位号 设置原手机号 和下发手机号
			mobile_type = checkMobileTypeAndSetCountryInfo(submitBean, user, submitSpNum) + "";
			
			if("0".equals(mobile_type.trim())||"47".equals(mobile_type.trim())){
				result = 30004 ;
				submitBean.setResp(result);
				return result;// 无效手机号码
			}
			long time3 = System.nanoTime();
			sb.append("号码校验: ").append(time3-time2).append(" ns").append("\n");
			
			userServiceList = checkServiceCode(submitSpNum, user.getServiceMap(), mobile_type);
			long time4 = System.nanoTime();
			sb.append("校验业务: ").append(time4-time3).append(" ns").append("\n");
			
			if (null != userServiceList && userServiceList.size() > 0) {
				// 获取实际发送业务（优先级为：号段业务|分流业务|账号备用|通道备用|主用）
				UserServiceForm useableService = getRealTd(user, submitBean, userServiceList);
				long time5 = System.nanoTime();
				sb.append("赋值业务: ").append(time5-time4).append(" ns").append("\n");
				// 存在可用业务则设置yw_code、sp_number、add_msg 不是国际通道的设置price
				SignInfoForm userSign = addMsg(user, useableService, submitBean);
				long time6 = System.nanoTime();
				sb.append("追加内容: ").append(time6-time5).append(" ns").append("\n");
				// 字数校验
				result = checkWords(userSign, submitBean);
				long time7 = System.nanoTime();
				sb.append("字数校验: ").append(time7-time6).append(" ns").append("\n");
				if (result == 0) {
					// 扣除费用
					result = BalanceUtil.updateUserBalance(user, submitBean.getCharge_count() * submitBean.getPrice());
					long time8 = System.nanoTime();
					sb.append("扣费处理: ").append(time8-time7).append(" ns").append("\n");
				} 

			} else {
				// 手机号码和业务不匹配
				result = 30005;
			}
		} catch (Exception e) {
			// 号码错误
			result = 47;
			log.error("["+submitBean.getUser_id()+"]balanceValidException", e);
		}
		submitBean.setResp(result);
		if(30004==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; mobile_type: "+mobile_type+";Resp_code: 30004; 无效手机号码!");
		}else if(30005==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; ServiceMap: "+(user!=null?user.getServiceMap():null)+"; "+submitBean+"; mobile_type: "+mobile_type+";Resp_code: 30005; 手机号码与业务不匹配!");
		} else if(result!=0){
			log.info("Resp_code: "+result+";  "+submitBean);
		}
		long time = System.currentTimeMillis()-time0;
		sb.append("扣费处理: ").append(time).append(" ms").append("\n");
		sb.append("-------------------------------------\n");
		if(time>50){
			log.info(sb.toString());
		}
		return result;
	}

	/*
	 * 
	 * 多签名校验
	 */
	
	public int valid_multi(SubmitBean submitBean) {
		String user_id = submitBean.getUser_id();
		try {
			if(DataCenter.getUse_code_0().contains(user_id)){
				submitBean.setMsg_format(changeFormat(submitBean));
			}
		} catch (Exception e) {
		}
		
		int result = 0;
		String sign = "";
		UserBean user=null;
		String submitSpNum = "";
		List<UserServiceForm> userServiceList =null;
		String mobile_type ="";
		try {
			user = UserBeanCache.getUserBean(submitBean.getUser_id());
			if(user==null){
				user = UserBeanCache.getUserBeanMap().get(submitBean.getUser_id().trim());
				if(user==null){
					log.warn(submitBean.getUser_id()+" is null ."+UserBeanCache.getUserBeanMap());
					submitBean.setResp(47);
					return 47;	
				}
				
			}
			String sign_type = "1";
			if(user.getParamMap()!=null){
				sign_type = (String) user.getParamMap().get("sign_type");
				
				if(sign_type==null||sign_type.trim().equals("")){
					sign_type = "1";
				}
			}
			
			
			sign = dealSignType(sign_type,submitBean);
			
			if(submitBean.getResp()!=0){
				return submitBean.getResp();
			}
			
			List<SignInfoForm> signInfoList = user.getUserSignInfo();
			SignInfoForm signInfo = validSign(sign,signInfoList);
			if(signInfo==null){
				result = 63;
				submitBean.setResp(result);
				return result;
			}
	
			submitSpNum = submitBean.getSp_number();
			// 从缓存中取得该账户该类型的所有业务
			if(null != user.getUser_sp_number() && !user.getUser_sp_number().equals("")){
				submitSpNum = "";
			}
			// 校验短信类型，设置国际短信单价 处理国家区位号 设置原手机号 和下发手机号
			mobile_type = checkMobileTypeAndSetCountryInfo(submitBean, user, submitSpNum) + "";
			
			if("0".equals(mobile_type.trim())||"47".equals(mobile_type.trim())){
				result = 30004 ;
				submitBean.setResp(result);
				return result;// 无效手机号码
			}
		 
			userServiceList = checkMultiServiceCode(submitSpNum, user.getUserMultiServiceMap(), mobile_type,sign);
			
			if (null != userServiceList && userServiceList.size() > 0) {
				// 获取实际发送业务（优先级为：号段业务|分流业务|账号备用|通道备用|主用）
				UserServiceForm useableService = getRealTd(user, submitBean, userServiceList);
				// 存在可用业务则设置yw_code、sp_number、add_msg 不是国际通道的设置price
				SignInfoForm userSign = addMultiMsg(user, useableService, submitBean,signInfo);
				// 字数校验
				result = checkWords(userSign, submitBean);
				
				if (result == 0) {
					// 扣除费用
					result = BalanceUtil.updateUserBalance(user, submitBean.getCharge_count() * submitBean.getPrice());
				} else {
					String signature = submitBean.getSignature();
					if (submitBean.getWith_gate_sign() == 1) {
						signature = submitBean.getOperator_signature();
					}
					if (null == signature) {
						signature = "";
					}
					submitBean.setSignature(signature);
				}

			} else {
				// 手机号码和业务不匹配
				result = 30005;
			}
		} catch (Exception e) {
			// 号码错误
			result = 47;
			log.error("["+submitBean.getUser_id()+"]balanceValidException", e);
		}
		submitBean.setResp(result);
		if(30004==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; mobile_type: "+mobile_type+";Resp_code: 30004; 无效手机号码!");
		}else if(30005==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; ServiceMap: "+(user!=null?user.getServiceMap():null)+"; "+submitBean+"; mobile_type: "+mobile_type+";Resp_code: 30005; 手机号码与业务不匹配!");
		} else if(result!=0){
			log.info("Resp_code: "+result+";  "+submitBean);
		}
		return result;
	}
	private static List<UserServiceForm> checkServiceCode(String checkServiceSub, Map<String, List<UserServiceForm>> map, String mobile_type) {
		String serviceKey = checkServiceSub + mobile_type;
		// 从缓存中取得该账户该类型的所有业务
		if(map.containsKey(serviceKey)){
			return map.get(serviceKey);
		}else if (!map.containsKey(serviceKey) && checkServiceSub.length() > 1){
			return checkServiceCode(checkServiceSub.substring(0, checkServiceSub.length() - 1), map, mobile_type);
		}else{
			return null;
		}
	}
	
	//多签名模式
	private static List<UserServiceForm> checkMultiServiceCode(String checkServiceSub,Map<String,HashSet<UserServiceForm>> map, String mobile_type,String sign) {
		
		String serviceKey = checkServiceSub + mobile_type+sign;
		// 从缓存中取得该账户该类型的所有业务
		if(map.containsKey(serviceKey)){
			HashSet<UserServiceForm> userServiceSet = map.get(serviceKey);
			List<UserServiceForm> userServiceList = null;
			if(userServiceSet!=null&&userServiceSet.size()>0){
				userServiceList = new ArrayList<UserServiceForm>(userServiceSet);
			}
			return userServiceList;
		}else if (!map.containsKey(serviceKey) && checkServiceSub.length() > 1){
			return checkMultiServiceCode(checkServiceSub.substring(0, checkServiceSub.length() - 1), map, mobile_type,sign);
		}else{
			return null;
		}
	}
	
	/**
	 * mobile前缀处理，只保留实际手机号码
	 *
	 * @param submitBean
	 * @param country_code
	 */
	private boolean dealMobileWithCountryCode(SubmitBean submitBean,String country_code) {
		boolean result = false;
		String[] mobiles = submitBean.getMobiles();
		for (int i = 0; i < mobiles.length; i++) {
			if (mobiles[i].startsWith(country_code)) {
				mobiles[i] = mobiles[i].substring(country_code.length());
				result = true;
			}
		}
		return result;
	}

	public void userServiceMapPutValue(int key, UserServiceForm userService,Map<Integer, List<UserServiceForm>> userServiceMap) {
		if (!userServiceMap.containsKey(key)) {
			userServiceMap.put(key, new ArrayList<UserServiceForm>());
		}
		userServiceMap.get(key).add(userService);
	}

	/**
	 * 获取可用的账户级备用业务 （Priority 取最高）
	 * 
	 * @param userServiceMap
	 * @param service
	 */
	private void setUserStandby( Map<Integer, List<UserServiceForm>> userServiceMap,UserServiceForm service) {
		if (userServiceMap.containsKey(1)) {
			int minPriority = userServiceMap.get(1).get(0).getPriority();
			if (service.getPriority() < minPriority) {
				userServiceMap.get(1).add(0, service);
			}
		} else {
			userServiceMapPutValue(1, service, userServiceMap);
		}
	}

	/**
	 * 校验下发号码类型 如果是下发国际设置相应信息,并一定赋值country_cn，除非找不到对应国家。
	 * 
	 * @param submitBean
	 * @param user
	 * @param submitSpNum 
	 * @return 号码类型, 0=国内未知,1=移动,2=联通,3=电信,4=国际,47=国际未知
	 */
	private int checkMobileTypeAndSetCountryInfo(SubmitBean submitBean, UserBean user, String submitSpNum) {
		
		int result = 0;
		String ori_mobile = submitBean.getMobiles()[0];
		dealMobileRemove0(submitBean);
		
		/**
		 * 去掉号码中的+和0
		 */
		String is_remove_00 = "1";
		if(user!=null&&user.getParamMap()!=null){
			 is_remove_00 =  (String) user.getParamMap().get("is_remove_00");
			if(is_remove_00==null||is_remove_00.trim().equals("")){
				is_remove_00 = "1";
			}
		}
		
		if("0".equalsIgnoreCase(is_remove_00.trim())){
			submitBean.setOri_mobile(submitBean.getMobiles()[0]);
			
		}else{
			submitBean.setOri_mobile(ori_mobile);
		}
		String tmp_mobile =  submitBean.getMobiles()[0];
	    //String mobile =submitBean.getMobiles()[0];
		boolean isHasInternation = false ;
		//客户包含了国际业务
//		if(isHasInternationServices(user.getServiceMap())){
//			isHasInternation = true ;
//			//处理国际业务匹配
//			if (!tmp_mobile.startsWith("86")){
//				// 校验国际短信，并设置单价
//				Map<String, CountryPhoneCodeInfo> countryPhoneCodeMap = user.getCountryPhoneCodeMap();
//				CountryPhoneCodeInfo countryPhoneCode = getCountryCode(submitBean,countryPhoneCodeMap);
//				if (countryPhoneCode != null) {
//					submitBean.setPrice(countryPhoneCode.getPrice());
//					submitBean.setCountry_cn(countryPhoneCode.getCountry_cn());
//					//result = 4;
//				    return 4 ;
//				} else {
//					//result = 47;
//					return 47 ;
//				}
//			}else {
//				submitBean.setCountry_cn(LOCATION_COUNTRY);
//			}
//		}else{
//		}
		
		dealMobileWithCountryCode(submitBean, "86");
		if(log.isDebugEnabled()){
			log.debug("1.result: "+result+" ; "+submitBean);
		}
		/**
		 * 非国际号码，再判读是否走携号转网
		 */
		if(result != 4&&result != 47){
			if(user.getIs_net_switch()==0){
				result = checkNetSwitchedMobile(submitBean);
			}
			
		}
		
		/**
		 *若客户没有匹配到国际业务，同时客户没有进行携号转网，则根据正则来匹配手机号码类型
		 */
		if (result == 0) {
		  // 校验号码号段正则
		  result = checkMethod(submitBean);
		}
		if (isHasInternation) {
			// 客户有国际业务时，提交国内号码再把86追加回去
			String[] mobiles = submitBean.getMobiles();
			for (int i = 0; i < mobiles.length; i++) {
				if(tmp_mobile.startsWith("86")){
				  mobiles[i] = tmp_mobile;
				}
			}
		} 
		if(log.isDebugEnabled()){
			log.debug("2.result: "+result+" ; "+submitBean);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private boolean isChineseNumber(String mobile){
		boolean result = false;
		if(mobile==null||"".equals(mobile)){
			return false ;
		}
		if (mobile.length() == 13 && mobile.startsWith("86")) {
			result = true;
		} else {
			List<CheckMethod> checkMethodList = LocationInfoCache.getCheckMethodList();
			if (null != checkMethodList && checkMethodList.size() > 0) {
				for (CheckMethod checkMethod : checkMethodList) {
					if (checkMethod.getCheck_code() != 4) {
						Matcher mat = (checkMethod.getPattern()).matcher(mobile);
						if (mat.find()) {
							return true;
						}
					}
				}
			}

		}
		return result;
	}
	/**
	 * 校验携号转网
	 * 
	 * @param submitBean
	 * @return
	 */
	private int checkNetSwitchedMobile(SubmitBean submitBean) {
		int result = 0;
		String mobile = submitBean.getMobiles()[0];
		Map<String, NetSwitchedMobileInfo> netSwitchedMobileMap = NetSwitchedMobileCache.getNumberPortabilityMap();
		if (null != netSwitchedMobileMap && netSwitchedMobileMap.containsKey(mobile)) {
			result = netSwitchedMobileMap.get(mobile).getDest_td_type();
		}
		return result;
	}

	/**
	 * 匹配通道号段正则
	 *
	 * @param submitBean
	 * @return
	 */
	private int checkMethod(SubmitBean submitBean) {
		int check_code = 0;
		List<CheckMethod> checkMethodList = LocationInfoCache.getCheckMethodList();
		if (null != checkMethodList && checkMethodList.size() > 0) {
			OK: for (CheckMethod checkMethod : checkMethodList) {
				// 获取短信手机号码
				String[] mobiles = submitBean.getMobiles();
				// 正则匹配
				for (String mobile : mobiles) {
					Matcher mat = (checkMethod.getPattern()).matcher(mobile);
					if (mat.find()) {
						check_code = checkMethod.getCheck_code();
						break OK;
					}
				}
			}
		}
		return check_code;
	}

	/**
	 * 获取最长匹配国家/省级区位号
	 * 
	 * @param submitBean
	 * @param countryPhoneCodeMap
	 */
	private CountryPhoneCodeInfo getCountryCode(SubmitBean submitBean, Map<String, CountryPhoneCodeInfo> countryPhoneCodeMap) {
		CountryPhoneCodeInfo result = null;
		String mobile = submitBean.getMobiles()[0];
		for (int i = mobile.length(); i > 0; i--) {
			//System.out.println("mobile: "+mobile.substring(0, i));
			result = countryPhoneCodeMap.get(mobile.substring(0, i));
			if(log.isDebugEnabled()){
				log.debug((i+1)+". "+mobile.substring(0, i));
			}
			if (result != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * 设置实际发送通道(赋值td_code、sp_number) userServiceRatioMap 存放分流业务的map key = ratio
	 * 
	 * @param submitBean
	 * @return
	 */
	private UserServiceForm getRealTd(UserBean user, SubmitBean submitBean, List<UserServiceForm> userServiceList) {

		UserServiceForm realService = null;

		// 将符合的业务放入map中,号段业务:key=-1,主用业务:key=0,分流业务:key=-2,优先级最高的账户级备用业务:key=1,通道级备用业务:key=2,
		Map<Integer, List<UserServiceForm>> userServiceMap = dealUserServiceMap(userServiceList, submitBean);

		// 获取可用的号段业务
		if (userServiceMap.containsKey(-1)) {
			realService = userServiceMap.get(-1).get(0);
		} else {
			List<UserServiceForm> services = userServiceMap.get(0);
			UserServiceForm mainService = null;
			if(services!=null&&services.size()>0){
				mainService = services.get(0);
			}else {
				mainService = new UserServiceForm();
				mainService.setStatus(1);
			}
			
			//UserServiceForm mainService = userServiceMap.get(0).get(0);
			// 分流业务
			List<UserServiceForm> branchService = userServiceMap.get(-2);
			UserServiceForm service = null;
			// 分流业务为空，且主用业务不可用，查找备用业务
			if (branchService == null) {
				branchService = new ArrayList<UserServiceForm>();
				// 主用不可用查找备用
				if (mainService.getStatus() == 1) {
					// 账户级备用
					if (null == service && userServiceMap.containsKey(1)) {
						service = userServiceMap.get(1).get(0);
					}
					// 如果允许切换到通道备用则查找通道级备用
					if (null == service && isSwitchTd(user, userServiceMap)) {
						service = userServiceMap.get(2).get(0);
					}
				}
				// 无可用业务用主用
				if (null == service) {
					service = mainService;
				}
			} else if (branchService != null && branchService.size() > 0 && mainService.getStatus() == 0) {
				service = mainService;
			}

			if (service != null) {
				if (service.getRatio() <= 0) {
					service.setRatio(1);
				}
				for (int i = 0; i < service.getRatio(); i++) {
					branchService.add(service);
				}
			}

			// 计算该号码的下发业务下标
			int groupKey = getRatioGroupKey(submitBean.getMobiles()[0], branchService.size());

			realService = branchService.get(groupKey);
		}

		return realService;
	}

	/**
	 * 计算号码的分流业务下标
	 * 
	 * @param mobile
	 * @param totalRatio
	 * @return
	 */
	private int getRatioGroupKey(String mobile, int totalRatio) {
		String subMobile = mobile.substring(mobile.length() - 5);
		return totalRatio == 0 ? 0 : Integer.parseInt(subMobile) % totalRatio;
	}

	/**
	 * 将符合的业务放入map中
	 *
	 * @param submitBean
	 * @param userServiceList
	 * @return
	 */
	private Map<Integer, List<UserServiceForm>> dealUserServiceMap( List<UserServiceForm> userServiceList, SubmitBean submitBean) {
		Map<Integer, List<UserServiceForm>> userServiceMap = new HashMap<Integer, List<UserServiceForm>>();
		for (UserServiceForm each : userServiceList) {
			int priorty = each.getPriority();
			if (priorty == 0) {
				// 主用业务，设置key=0
				userServiceMapPutValue(priorty, each, userServiceMap);
			} else if (priorty > 0 && each.getStatus() == 0) {
				if (each.getLevel() == 1) {
					// 可用的账户级备用业务，设置key=1，挑选一个优先级最高的使用
					setUserStandby(userServiceMap, each);
				} else if (each.getLevel() == 2) {
					// 可用的通道级备用业务，设置key=2
					userServiceMapPutValue(2, each, userServiceMap);
				}
			} else if (priorty == -2 && each.getStatus() == 0) {
				// 主用分流业务，设置key=-2
				for (int i = 0; i < each.getRatio(); i++) {
					userServiceMapPutValue(-2, each, userServiceMap);
				}
			}
			
		}
		return userServiceMap;
	}

	private boolean isSwitchTd(UserBean user,Map<Integer, List<UserServiceForm>> userServiceMap) {
		return user.getIs_switch_td() == 0 && userServiceMap.containsKey(2);
	}

	/**
	 * 字数校验
	 * 
	 * @param submitBean
	 * @return
	 */
	private int checkWords(SignInfoForm userSign, SubmitBean submitBean) {
		// 错误代码
		int resp = 0;
		// 获取业务通道
		TdInfo tdInfo = TdInfoCache.getTd_info(submitBean.getTd_code());
		int td_submit_type = tdInfo.getSubmit_type();
		 
		if (null != tdInfo) {
			int charge_count = 1;
			// 通道相对的短信长度
			int check_length = 0;
			// NE_普通英文 NC_普通中文 LEF_长短信最后一条英文 LE_长短信非最后一条英文 LCF_长短信最后一条中文
			// LC_长短信非最后一条中文
			String smsType = getSmsType(submitBean);

			if (smsType.equals("NE")) {
				check_length = tdInfo.getMsg_count_en();
				resp = 15;
			} else if (smsType.equals("NC")) {
				check_length = tdInfo.getMsg_count_cn();
				resp = 16;
			} else if (smsType.equals("LEF")) {
				check_length = tdInfo.getLong_charge_count_en();
				resp = 12;
			} else if (smsType.equals("LE")) {
				check_length = tdInfo.getLong_charge_count_pre_en();
				resp = 11;
			} else if (smsType.equals("LCF")) {
				check_length = tdInfo.getLong_charge_count_cn();
				resp = 14;
			} else if (smsType.equals("LC")) {
				check_length = tdInfo.getLong_charge_count_pre_cn();
				resp = 13;
			}

			// 设置短信签名并返回长度(运营商加签名返回通道签名的长度，运营商不加，返回长度为0同时将用户签名加到短信内容后面)
			int sign_length = addSignReturnLength(tdInfo, smsType, userSign, submitBean);
			
			int msg_count = submitBean.getContent().length() + sign_length;
            int tmp_count = submitBean.getContent().length();
			if (submitBean.getSubmit_type().equals("SGIP")) {
				// 整条提交
				int check_count_cn = tdInfo.getMsg_count_cn();//普通短信计费字数
				int check_count_en = tdInfo.getMsg_count_en();
				if (smsType.contains("NC")) {
					if (msg_count <= check_count_cn) {
						charge_count = 1;
					} else {
						if(td_submit_type==2){
							charge_count = getLongCount(msg_count,tdInfo.getMsg_count_all_cn());
						}else {
							charge_count = getLongCount(tmp_count,tdInfo.getMsg_count_cn(),tdInfo.getLong_charge_count_pre_cn(),tdInfo.getLong_charge_count_cn(),sign_length,8);	
						}
						
					}
					resp = 0;
				} else if (smsType.contains("NE")) {
					if (msg_count <= check_count_en) {
						charge_count = 1;
					} else {
						if(td_submit_type==2){
							charge_count = getLongCount(msg_count,tdInfo.getMsg_count_all_en());
						}else {
							charge_count = getLongCount(tmp_count,tdInfo.getMsg_count_en(),tdInfo.getLong_charge_count_pre_en(),tdInfo.getLong_charge_count_en(),sign_length,0);
						}
						
					}
					resp = 0;
				} else if (smsType.contains("L") && msg_count <= check_length) {
					// 长短信字数超出设置返回码，未超出设置计费条数
					resp = 0;
				}
			} else if (!submitBean.getSubmit_type().equals("SGIP")&& msg_count <= check_length) {
				// 字数超出设置返回码，未超出设置计费条数
				resp = 0;
			}
			submitBean.setCheck_length(check_length);
			submitBean.setResp(resp);
			submitBean.setCharge_count(charge_count);
			submitBean.setPrice(submitBean.getPrice());
		}
		return resp;
	}




	/***
	 * 获取整条提交的长短信条数
	 * @param msg_count
	 * @param normal
	 * @param per
	 * @param count_end
	 * @param sign
	 * @param format
	 * @return
	 */
	private int getLongCount(int msg_count,int normal, int per,int count_end,int sign,int format) {
		if(per>normal&&sign==0){
			if(format!=0){
				sign = 70-normal;
				count_end =67;
			}else {
				sign = 140-normal;
				count_end =134;
			}
		
		}
		 
		
		int count = (msg_count+sign)/per ;//拆分后的短信条数
		int num = (msg_count+sign)%per ;
		
		log.debug("msg_count: "+msg_count+ "; check_per_count: "+per+"; check_end: "+count_end+"; sign_length:"+sign);
		//System.out.println("msg_count: "+msg_count+ "; check_per_count: "+per+"; check_end: "+count_end+"; sign_length:"+sign+"; num: "+num);
		
		if(msg_count+sign<=normal){
			count =1;
		}else {
			if(num!=0){
				count=count+1;
			}
			//如果余数大于要就的最后一条的条数，短信需要继续拆分一条
			if(num>count_end){
				count=count+1;
			}
		}
		
		 
		return count;
	}
	
	public static void  main(String[] args){
		BalanceVerifier service = new BalanceVerifier();
		int count = service.getLongCount(258,140,134,124,6,0);
		System.out.println("count: "+count);
		//String tmp = "+15210962358";
		//System.out.println("tmp: "+tmp.substring(1,tmp.length()));
	}
	
	private int getLongCount(int msg_count, int check_count) {
		int charge_count = msg_count / check_count;
		if (msg_count % check_count > 0) {
			charge_count += 1;
		}
		return charge_count;
	}

	/**
	 * 设置短信签名并返回长度
	 * 
	 * @param tdInfo
	 * @param submitBean
	 * @param smsType
	 * @return
	 */
	private int addSignReturnLength(TdInfo tdInfo, String smsType, SignInfoForm userSign, SubmitBean submitBean) {
		
		int with_gate_sign = tdInfo.getWith_gate_sign();
		int sign_length = 0;
		if (!smsType.equals("LE") && !smsType.equals("LC")) {
			String sp_number = submitBean.getSign_ext_code();
			String tagKey = "";
			Map<String, TdSignInfo> sign_map = tdInfo.getSignMap();
			if (null != sign_map) {
				for (String key : sign_map.keySet()) {
					if (sp_number.startsWith(key)) {
						tagKey = key.length() > tagKey.length() ? key : tagKey;
					}
				}
			}
			TdSignInfo td_sign = tdInfo.getSignMap().get(tagKey);
			String operator_signature = "";
			// 运营商加签名，设置通道签名长度
			if(td_sign!=null){
				if (with_gate_sign == 1 && smsType.contains("E")) {
					if (null != td_sign.getSign_eng()) {
						operator_signature = td_sign.getSign_eng();
						sign_length = operator_signature.length();
					}
				} else if (with_gate_sign == 1 && smsType.contains("C")) {
					if (null != tdInfo.getSign_chs()) {
						operator_signature = td_sign.getSign_chs();
						sign_length = operator_signature.length();
					}
				}
				
			}else {
				System.out.println(tagKey+ ": "+ tdInfo.getSignMap());
			}
			
		
			
			
			// 运营商不加签名，设置用户签名
			String sign = "";
			if (null != userSign && with_gate_sign == 0) {
				if (smsType.contains("E")) {
					sign = userSign.getSign_eng();
				} else if (smsType.contains("C")) {
					sign = userSign.getSign_chs();
				}
				sign_length = sign.length();
			}
			submitBean.setWith_gate_sign(with_gate_sign);
			submitBean.setOperator_signature(operator_signature);
			submitBean.setSignature(sign);
			
		}
		
		return sign_length;
	}
	
	
	
	/**
	 * 获取短信类型（NE_普通英文 NC_普通中文 LEF_长短信最后一条英文 LE_长短信非最后一条英文 LCF_长短信最后一条中文
	 * LC_长短信非最后一条中文 ）
	 * 
	 * @param submitBean
	 * @return
	 */
	private String getSmsType(SubmitBean submitBean) {
		String smsType = "";
		int pkTotal = submitBean.getPkTotal();// 拆分总条数
		int pkNumber = submitBean.getPkNumber();// 第几条
		int msg_format = submitBean.getMsg_format();// 0_英文 8、15_中文
		if (pkTotal > 0 && pkNumber > 0) {
			smsType = "L";
		} else {
			smsType = "N";
		}
		if (msg_format == 0||msg_format==0x10) {
			smsType += "E";
		} else {
			smsType += "C";
		}
		if (pkTotal != 0 && pkNumber == pkTotal) {
			smsType += "F";
		}
		return smsType;
	}

	/**
	 * 本次提交需要追加的内容 根据实际下发业务查找签名，根据is_add_msg判断是否需要追加内容，追加内容为add_msg
	 * @param user
	 * @param useableService
	 * @param submitBean
	 * @return
	 */
	private SignInfoForm addMsg(UserBean user, UserServiceForm useableService, SubmitBean submitBean) {
		long time0 =System.currentTimeMillis();
		long time1 = System.nanoTime();
		StringBuffer sb = new StringBuffer();
		SignInfoForm userSign = null;
		// 获取业务通道
		String td_code = "";
		try {
			td_code = useableService.getTd_code();
		} catch (Exception e) {
			td_code = useableService.getTd_code() + "";
		}
		TdInfo tdInfo = TdInfoCache.getTd_info(td_code);
		if (null != tdInfo) {
			// 获取提交短信的主叫号码（集群是业务service_code,自有网关是user_sp_number）
			String sp_number = submitBean.getSp_number();
			// 设置自有网关的service_code = user_sp_number
			submitBean.setService_code(user.getUser_sp_number());
			// 设置替换目标为user_sp_number
			String replaceSpNTarget = user.getUser_sp_number();
			// 当 replaceSpNTarget = NULL 时为集群、sp提交，否则为自有网关提交
			if (null == replaceSpNTarget) {
				// 更改替换目标为 service_code
				replaceSpNTarget = useableService.getService_code();
				// 更改 submitBean 的 service_code
				submitBean.setService_code(useableService.getService_code());
			} else {
				int yw_code =0;
				try {
					yw_code = Integer.parseInt(td_code);
				} catch (Exception e) {
				}
				submitBean.setYw_code(yw_code);
			}
			long time2 = System.nanoTime();
			sb.append("扩展处理： ").append(time2-time1).append(" ns\n");
			 
		 
			// 设置用户提交时所带扩展,此处包含模板自带扩展以及业务线提交的扩展
			if(replaceSpNTarget!=null){
				submitBean.setUser_ext_code(sp_number.substring(replaceSpNTarget.length(), sp_number.length()));
			}else{
				submitBean.setUser_ext_code("");
			}
			
			// 业务扩展
			String ext_code = useableService.getExt_code();
			String sendSpN = tdInfo.getExt() + ext_code+submitBean.getUser_ext_code();
			
			//控制提交的接入号长度不可以超过19位长度
			long time3 = System.nanoTime();
			sb.append("截取处理： ").append(time3-time2).append(" ns\n");
			if(sendSpN.length()>19){
				int tmp_len = sendSpN.length()-19;
				sendSpN = sendSpN.substring(0, 19);
				if(ext_code.length()>tmp_len){
					ext_code = ext_code.substring(0, ext_code.length()-tmp_len);
				}else{
					ext_code = "";
				}
			}
			submitBean.setSrc_number(sp_number);
			submitBean.setTd_code(td_code);
			
			submitBean.setExt_code(ext_code);
			submitBean.setSend_sp_number(sendSpN);
			submitBean.setSign_ext_code(sendSpN);
			submitBean.setSp_number(sendSpN);
			
			if (!useableService.getTd_type().equals("4")) {
				submitBean.setPrice(useableService.getPrice());
			}
			long time4 = System.nanoTime();
			sb.append("基础赋值： ").append(time4-time3).append(" ns\n");
			// 获取用户签名信息
			List<SignInfoForm> userSignList = user.getUserSignInfo();
			if (null != userSignList && userSignList.size() > 0) {
				for (SignInfoForm each : userSignList) {
					String sign_sp_number = each.getSp_number();
					// 根据扩展找到签名信息
					if (sp_number.startsWith(sign_sp_number)) {
						if (null == userSign) {
							userSign = each;
						}
						if (userSign.getSp_number().length() < sign_sp_number.length()) {
							userSign = each;
						}
					}
				}
				// 获取短信类型
				String smsType = getSmsType(submitBean);
				// 判断是否要追加内容(实际要追加内容的类型有NE，NC,LEF,LCF))
				String add_msg = "";
				if (null != userSign && userSign.getIs_add_msg() == 1 && !smsType.equals("LE") && !smsType.equals("LC")) {
					if (submitBean.getMsg_format() == 0) {
						add_msg = userSign.getAdd_eng_msg();
					} else {
						add_msg = userSign.getAdd_chs_msg();
					}
				}
				submitBean.setContent(submitBean.getContent() + add_msg);
			}
			/***
			 * 全局内容追加，此处内容追加根据下发业务代码进行
			 */
			String  service_add = ServiceInfoCache.getServiceAddMsg(submitBean.getService_code());
			submitBean.setContent(submitBean.getContent() + service_add);
			long time5 = System.nanoTime();
			long time = System.currentTimeMillis()-time0;
			sb.append("信息追加： ").append(time5-time4).append(" ns\n");
			sb.append("总耗时： ").append(time).append(" ms\n");
			sb.append("--------------------------------------\n");
			if(time>50){
				log.info(sb.toString());
			}
		}
		return userSign;
	}


	/*
	 * 多签名模式
	 */
	private SignInfoForm addMultiMsg(UserBean user, UserServiceForm useableService, SubmitBean submitBean,SignInfoForm userSign) {
		// 获取业务通道
		String td_code = "";
		try {
			td_code = useableService.getTd_code();
		} catch (Exception e) {
			td_code = useableService.getTd_code() + "";
		}
		TdInfo tdInfo = TdInfoCache.getTd_info(td_code);
		if (null != tdInfo) {
			// 获取提交短信的主叫号码（集群是业务service_code,自有网关是user_sp_number）
			String sp_number = submitBean.getSp_number();
			// 设置自有网关的service_code = user_sp_number
			submitBean.setService_code(user.getUser_sp_number());
			// 设置替换目标为user_sp_number
			String replaceSpNTarget = user.getUser_sp_number();
			// 当 replaceSpNTarget = NULL 时为集群、sp提交，否则为自有网关提交
			if (null == replaceSpNTarget) {
				// 更改替换目标为 service_code
				replaceSpNTarget = useableService.getService_code();
				// 更改 submitBean 的 service_code
				submitBean.setService_code(useableService.getService_code());
			} else {
				int yw_code =0;
				try {
					yw_code = Integer.parseInt(td_code);
				} catch (Exception e) {
				}
				submitBean.setYw_code(yw_code);
			}
			// 设置用户提交时所带扩展
			submitBean.setUser_ext_code(sp_number.replace(replaceSpNTarget, ""));
			// 业务扩展
			String ext_code = useableService.getExt_code();
			String sendSpN = sp_number.replace(replaceSpNTarget, tdInfo.getExt() + ext_code);
			//控制提交的接入号长度不可以超过19位长度
			if(sendSpN.length()>19){
				int tmp_len = sendSpN.length()-19;
				sendSpN = sendSpN.substring(0, 19);
				if(ext_code.length()>tmp_len){
					ext_code = ext_code.substring(0, ext_code.length()-tmp_len);
				}else{
					ext_code = "";
				}
			}
			
			submitBean.setSrc_number(sp_number);
			submitBean.setTd_code(td_code);
		
			submitBean.setExt_code(ext_code);
			submitBean.setSend_sp_number(sendSpN);
			submitBean.setSign_ext_code(sendSpN);
			submitBean.setSp_number(sendSpN);
			if (!useableService.getTd_type().equals("4")) {
				submitBean.setPrice(useableService.getPrice());
			}
		
				
				// 获取短信类型
				String smsType = getSmsType(submitBean);
				// 判断是否要追加内容(实际要追加内容的类型有NE，NC,LEF,LCF))
				String add_msg = "";
				if (null != userSign && userSign.getIs_add_msg() == 1 && !smsType.equals("LE") && !smsType.equals("LC")) {
					if (submitBean.getMsg_format() == 0) {
						add_msg = userSign.getAdd_eng_msg();
					} else {
						add_msg = userSign.getAdd_chs_msg();
					}
				}
				submitBean.setContent(submitBean.getContent() + add_msg);
			/***
			 * 全局内容追加，此处内容追加根据下发业务代码进行
			 */
			String  service_add = ServiceInfoCache.getServiceAddMsg(submitBean.getService_code());
			submitBean.setContent(submitBean.getContent() + service_add);
		}
		return userSign;
	}

	
	

	
	private void dealMobileRemove0(SubmitBean submitBean) {
		String[] mobiles = submitBean.getMobiles();
		for (int i = 0; i < mobiles.length; i++) {
			mobiles[i] = dealMobileRemove0(submitBean,mobiles[i].trim());
		}
	}
	
	private String dealMobileRemove0(SubmitBean submitBean,String mobile) {
		String result="";
		try {
			if(mobile.startsWith("+")){
				mobile=mobile.substring(1,mobile.length());
			}
			result = String.valueOf(Long.parseLong(mobile.trim()));
		} catch (Exception e) {
			log.warn("error mobile: "+mobile);
		}
		
		return result;
	}
	
	private String dealSignType(String sign_type,SubmitBean submitBean){
		int start = 0;
		int end = 0;
		int result = 0;
		String sign = "";
		String msg_content = submitBean.getContent();
		 if(sign_type.equals("0")){
					start = msg_content.indexOf("【");
					 end = msg_content.indexOf("】");
					 if(start==0&&end!=-1){
						  sign = msg_content.substring(start,end+1);
						  String msg  = msg_content.substring(end+1);
						  submitBean.setContent(msg);
					 }else{
						result = 61;//前置没有签名
						submitBean.setResp(result);
					 } 
			 
	
		 }else{
			 
				 start = msg_content.lastIndexOf("【");
				 end = msg_content.lastIndexOf("】");
				 if(start!=-1&&end==(msg_content.length()-1)){
					 sign = msg_content.substring(start,end+1);
					 String msg  = msg_content.substring(0, start);
					  submitBean.setContent(msg);
					 
				 }else{
					 	result = 62;//后置没有签名
						submitBean.setResp(result);
				 }
		 }
		 return sign;
	}
	
	private SignInfoForm validSign(String sign,List<SignInfoForm> signInfoList){
		SignInfoForm signInfo= null;
		if(signInfoList!=null&&signInfoList.size()>0){
			for(SignInfoForm signForm:signInfoList ){
				if(sign.equals(signForm.getSign_chs())){
					signInfo = signForm;
					break;
				}
				
			}
		}
	return signInfo;
	}

	public int do_valid(SubmitBean submitBean) {

		String user_id = submitBean.getUser_id();
		try {
			if(DataCenter.getUse_code_0().contains(user_id)){
				submitBean.setMsg_format(changeFormat(submitBean));
			}
		} catch (Exception e) {
		}
		
		int result = 0;
		UserBean user=null;
		String submitSpNum = "";
		List<UserServiceForm> userServiceList =null;
		String mobile_type ="";
		try {
			user = UserBeanCache.getUserBean(submitBean.getUser_id());
			if(user==null){
				user = UserBeanCache.getUserBeanMap().get(submitBean.getUser_id().trim());
				if(user==null){
					log.warn(submitBean.getUser_id()+" is null ."+UserBeanCache.getUserBeanMap());
					submitBean.setResp(47);
					return 47;	
				}
				
			}
			submitSpNum = submitBean.getSp_number();
			// 从缓存中取得该账户该类型的所有业务
			if(null != user.getUser_sp_number() && !user.getUser_sp_number().equals("")){
				submitSpNum = "";
			}
			// 校验短信类型，设置国际短信单价 处理国家区位号 设置原手机号 和下发手机号
			mobile_type = checkMobileTypeAndSetCountryInfo(submitBean, user, submitSpNum) + "";
			
			if("0".equals(mobile_type.trim())||"47".equals(mobile_type.trim())){
				result = 30004 ;
				submitBean.setResp(result);
				return result;// 无效手机号码
			}
		 
			userServiceList = checkServiceCode(submitSpNum, user.getServiceMap(), mobile_type);
			
			if (null != userServiceList && userServiceList.size() > 0) {
				// 获取实际发送业务（优先级为：号段业务|分流业务|账号备用|通道备用|主用）
				UserServiceForm useableService = getRealTd(user, submitBean, userServiceList);
				// 存在可用业务则设置yw_code、sp_number、add_msg 不是国际通道的设置price
				SignInfoForm userSign = addMsg(user, useableService, submitBean);
				// 字数校验
				result = checkWords(userSign, submitBean);
				if (result == 0) {
					// 扣除费用
					result = BalanceUtil.updateUserBalance(user, submitBean.getCharge_count() * submitBean.getPrice());
				} 

			} else {
				// 手机号码和业务不匹配
				result = 30005;
			}
		} catch (Exception e) {
			// 号码错误
			result = 47;
			log.error("["+submitBean.getUser_id()+"]balanceValidException", e);
		}
		submitBean.setResp(result);
		if(30004==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; mobile_type: "+mobile_type+";Resp_code: 30004; 无效手机号码!");
		}else if(30005==result){
			log.info("["+submitBean .getUser_id()+"] submitSpNum: "+submitSpNum+"; mobile: "+submitBean.getMobilesString()+"; ServiceMap: "+(user!=null?user.getServiceMap():null)+"; "+submitBean+"; mobile_type: "+mobile_type+";Resp_code: 30005; 手机号码与业务不匹配!");
		} else if(result!=0){
			log.info("Resp_code: "+result+";  "+submitBean);
		}
		return result;
	}
}
