package sunnyday.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import sunnyday.common.model.SmsTemplateInfo;
import sunnyday.tools.util.ParamUtil;

/**
 *短信模板缓存
 * 
 * @author 1307365
 *
 */
@Service
public class SmsTemplateCache extends Cache{
	private static Map<String, SmsTemplateInfo> sms_user_template=  new HashMap<String, SmsTemplateInfo>();
	private static Map<String, SmsTemplateInfo>  sms_common_template=  new HashMap<String, SmsTemplateInfo>();
	private static Map<String, SmsTemplateInfo> ext_template=  new HashMap<String, SmsTemplateInfo>();
	private static List<SmsTemplateInfo> filter_sms_template=  new ArrayList<SmsTemplateInfo>();
	 
	@Override
	public boolean reloadCache() {
		load_sms_template();
		load_ext_template();
		load_filter_sms_template();
		return true;
	}
	
	private void load_filter_sms_template() {
		List<SmsTemplateInfo> tmp = (List<SmsTemplateInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_SMS_FILTER_SMS_TEMPLATE);
		if(tmp!=null&&!tmp.isEmpty()){
			for(SmsTemplateInfo each : tmp){
				String template_content = each.getTemplateContent();
				if(StringUtils.isNotBlank(template_content)){
					template_content = template_content.replaceAll("\\$\\{\\d+\\}", ".*");
					each.setTemplateContent(template_content);
				}
			}
			filter_sms_template = tmp;
		}
		
	}

	@SuppressWarnings("unchecked")
	private boolean load_sms_template(){
		
		Map<String, SmsTemplateInfo> tmp1 = (Map<String, SmsTemplateInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_SMS_USER_TEMPLATE_COMMON);
		if(tmp1!=null&&!tmp1.isEmpty()){
			sms_user_template =  tmp1;
		}
		
		Map<String, SmsTemplateInfo> tmp2 = (Map<String, SmsTemplateInfo>) gateRAO.getCacheData(ParamUtil.REDIS_KEY_SMS_COMMON_TEMPLATE_COMMON);
		if(tmp2!=null&&!tmp2.isEmpty()){
			sms_common_template = tmp2 ;
		}
		return true;
	}
	 
	void load_ext_template(){
		Map<String, SmsTemplateInfo> tmp=  new HashMap<String, SmsTemplateInfo>();
		
		if (sms_user_template != null && !sms_user_template.isEmpty()) {
			try {
				for (SmsTemplateInfo info : sms_user_template.values()) {
					String key = info.getTemplate_ext_code() != null ? info.getTemplate_ext_code() : "";
					tmp.put(key, info);
				}
			} catch (Exception e) {

			}
		}
		
		if (sms_common_template != null && !sms_common_template.isEmpty()) {
			try {
				for (SmsTemplateInfo info : sms_common_template.values()) {
					String key = info.getTemplate_ext_code() != null ? info.getTemplate_ext_code() : "";
					tmp.put(key, info);
				}
			} catch (Exception e) {

			}
		}
		if(!tmp.isEmpty()){
			 ext_template = tmp;
		}
		
		
	}
	/**
	 * 通过用户id和模板Id获取对应的模板信息
	 * @param user_id
	 * @param templateId
	 * @return
	 */
	public static SmsTemplateInfo getSmsTemplate(String user_id,String templateId) {
		SmsTemplateInfo stp = null;
		if(sms_common_template!=null&&!sms_common_template.isEmpty()){
			stp = sms_common_template.get(templateId);
		}
		if(stp==null){
			stp = sms_user_template.get(user_id + "_" + templateId);
		}
		
		return stp;
	}
	
	/**
	 * 通过模板的扩展来获取到对应的模板信息
	 * @param templateExtCode
	 * @return
	 */
	public static SmsTemplateInfo getSmsExtTemplate(String templateExtCode) {
		if(ext_template!=null&&!ext_template.isEmpty()){
			return ext_template.get(templateExtCode);
		}
		return  null;
	}

	public static Map<String, SmsTemplateInfo> getSms_common_template() {
		return sms_common_template;
	}


	public static List<SmsTemplateInfo> getFilter_sms_template() {
		return filter_sms_template;
	}
}
