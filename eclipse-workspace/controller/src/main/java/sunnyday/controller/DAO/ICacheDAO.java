package sunnyday.controller.DAO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.AutoSendMsgConfig;
import sunnyday.common.model.AutoSendMsgContent;
import sunnyday.common.model.ChannelRoute;
import sunnyday.common.model.CheckCacheForm;
import sunnyday.common.model.CheckMethod;
import sunnyday.common.model.CountryPhoneCodeInfo;
import sunnyday.common.model.ErrCode;
import sunnyday.common.model.GateErrCode;
import sunnyday.common.model.KeywordForm;
import sunnyday.common.model.LocationInfo;
import sunnyday.common.model.MobileHome;
import sunnyday.common.model.NetSwitchedMobileInfo;
import sunnyday.common.model.ServiceInfo;
import sunnyday.common.model.SignInfoForm;
import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.common.model.SmsTemplateParam;
import sunnyday.common.model.SpNumberFilterInfo;
import sunnyday.common.model.TdInfo;
import sunnyday.common.model.UserBean;
import sunnyday.common.model.UserCheckType;
import sunnyday.common.model.UserServiceForm;
import sunnyday.common.model.UserSignForm;
import sunnyday.common.model.WhiteListTemplate;

@Repository
public interface ICacheDAO {
	
	public Map<String, TdInfo> load_td_info() ;
	
	public Map<String, UserServiceForm> load_deliver_match_spNumber();
	
	public Map<String, String> loadGateConfig() ;

	public Map<String, UserBean> loadUserInfo() ;
	public Map<String, List<UserServiceForm>> loadUserServiceInfo() ;
	public Map<String, ErrCode> load_err_code();
	
	/**
	 * 加载gate_err_code网关错误码表信息 
	 * @return
	 */
	public Map<String, GateErrCode> loadGateErrCode();
	
	public Map<String, List<UserCheckType>> load_user_check_mode() ;
	
	public Map<String, Set<String>> loadWhiteList(int level);
	public Map<String, Set<String>> loadWhiteMobile() ;
	public Map<String, Set<String>> loadBlackMobileByCondition(String key, int level) ;

	public Map<String, Set<String>> loadBlackScopeByCondition(String key, int level) ;
	public List<KeywordForm> fetchKeyWord();
	public  Map<String, Set<String>> load_keyword_info(String key,int level);

	/**
	 * Submit_message_check_temp
	 * 抓取审核记录表数据
	 * @return
	 */
	public List<CheckCacheForm> fetchCacheData(int limit);
	
	public List<CheckCacheForm> fetchSecondLevelCacheContentFromCheckGroup(int limit) ;

	public Map<String, List<UserSignForm>> loadUserSignInfo() ;
	
	public Map<String, List<SignInfoForm>> loadSignInfo() ;

	public Map<String, List<SpNumberFilterInfo>> loadSpNumberFilterInfo() ;

	/**
	 * 查询账户国际业务表价格数据
	 * @return
	 */
	public Map<String, List<CountryPhoneCodeInfo>> loadCountryPhoneCodeMap();
	
	public List<LocationInfo> loadLocationInfoMap();

	public List<CheckMethod> loadCheckMethodList() ;
 
	public List<ChannelRoute> loadChannelRouteList() ;
	
	public List<NetSwitchedMobileInfo> loadNumberPortabilityMap(int status) ;

	public List<TdInfo> load_td_full_info() ;
 
	public Map<String,String> load_chargeTermidMap();
 
	/**
	 * 加载公共级别的内容白名单模板
	 * @return
	 */
	public List<WhiteListTemplate> load_whiteTemplateCommon();
	
	/**
	 * 加载用户级别内容白名单模板
	 * @return
	 */
	public Map<String, List<WhiteListTemplate>> load_whiteTemplateUser();
	
	public Map<String, MobileHome> load_mobileHome();

	
	/**
	 * 获取短信模板信息
	 * @return
	 */
	public List<SmsTemplateInfo> load_SmsTemplate();
	
	public Map<String,List<SmsTemplateParam>> load_SmsTemplateParam();

	public Map<String, ServiceInfo> load_serviceInfo();

	public Map<String, String> load_commandInfo();
	public List<AutoSendMsgConfig> load_autoSendMsgConfig_list();
	/**
	 * 获取模板ID和业务的对接关系
	 * @return
	 */
	public Map<String, String> load_template_services();
	
	/**
	 * 获取通道的原始接入号码
	 * @return
	 */
	public List<String> load_td_spnumber();
	
	public  Map<String, String> load_admin_user();
	
	public List<AutoSendMsgContent> load_autoSendMsgContent_list();
	
	public List<String> userIdMatchServiceCode(String service_code);
	
}
