package sunnyday.gateway.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.gateway.threadPool.Task;

/**
 * 短信模板缓存
 * 
 * @author 1307365
 *
 */
@Service
public class SmsTemplateCache extends Task {
	
	private static Map<String, SmsTemplateInfo> sms_user_template = new HashMap<String, SmsTemplateInfo>();
	
	private static Map<String, SmsTemplateInfo> sms_common_template = new HashMap<String, SmsTemplateInfo>();
	
	private static Map<String, String> template_services = new HashMap<String, String>();
	
	
	

	@Override
	public void reloadCache() {
		load_sms_template();
	}

	private boolean load_sms_template() {
		try {
			Map<String, SmsTemplateInfo> tmp_1 =  dao.getTemplateCommon();
			if(tmp_1!=null&&!tmp_1.isEmpty()){
				sms_common_template = tmp_1;
			}
			Map<String, SmsTemplateInfo> tmp_2 = dao.getTemplateUser();
			if(tmp_2!=null&&!tmp_2.isEmpty()){
				sms_user_template = tmp_2;
			}
			Map<String, String> tmp_3 = dao.getTemplateServices();
			if(tmp_3!=null&&!tmp_3.isEmpty()){
				template_services = tmp_3;
			}
			 
		} catch (Exception e) {
		}
		return true;
	}

	/**
	 * 通过用户id和模板Id获取对应的模板信息
	 * 
	 * @param user_id
	 * @param templateId
	 * @return
	 */
	public static SmsTemplateInfo getSmsTemplate(String user_id, String templateId) {
		SmsTemplateInfo stp = null;
		if (sms_common_template != null && !sms_common_template.isEmpty()) {
			stp = sms_common_template.get(templateId);
		}
		if (stp == null&&sms_user_template!=null) {
			stp = sms_user_template.get(user_id+"_"+templateId);
		}
		return stp;
	}
	
	/**
	 * 根据模板ID获取下发该模板需要使用的业务代码
	 * @param key
	 * @return
	 */
	public static String getTemplateServices(String key) {
		if(template_services!=null){
			return template_services.get(key);
		}
		return null;
	}

}
