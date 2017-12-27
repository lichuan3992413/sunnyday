package sunnyday.gateway.util;

import java.net.URL;

import org.slf4j.Logger;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import sunnyday.common.model.UserBean;
import sunnyday.tools.util.CommonLogFactory;
import sunnyday.tools.util.Spring;

public class SmsServiceFactory {
	private static  Logger log = CommonLogFactory.getCommonLog(SmsServiceFactory.class);
	private SmsServiceFactory(){
	}
	public static ISmsService initSmsService(String type){
		ISmsService service = null;
		if(ParamUtil.HTTP_INTERFACE_SMSSEND.equals(type)){
			service = Spring.getApx().getBean(SendSmsServiceImpl.class);
		}else if(ParamUtil.HTTP_INTERFACE_POSTBALANCE.equals(type)){
			service = Spring.getApx().getBean(PostBalanceServiceImpl.class);
		} else if(ParamUtil.INTERFACE_POST_DELIVERS.equals(type)){
			service = Spring.getApx().getBean(PostDeliverServiceImpl.class);
		} else if(ParamUtil.INTERFACE_POST_REPORTS.equals(type)){
			service = Spring.getApx().getBean(PostReportServiceImpl.class);
		} else if(ParamUtil.HTTP_INTERFACE_SMSSEND_SERVICES.equals(type)){
			service = Spring.getApx().getBean(SubmitServiceImpl.class);
		}
		else if(ParamUtil.REPORT_SERVICE.equals(type)){
			service = Spring.getApx().getBean(ReportServiceImpl.class);
		}
		else if(ParamUtil.DELIVER_SERVICE.equals(type)){
			service = Spring.getApx().getBean(DeliverServiceImpl.class);
		}else if(ParamUtil.TEMPLATE_SMS_SERVICES.equals(type)){
			service = Spring.getApx().getBean(TemplateSmsServiceImpl.class);
		}else if(ParamUtil.TEMPLATE_BATCH_SMS_SERVICES.equals(type)){
			service = Spring.getApx().getBean(TemplateBatchServiceImpl.class);
		}
		
		
		
		return service;
	}
	
	/**
	 * 获取计费接口的返回值
	 * @param resp
	 * @param requestIP
	 * @param corp_id
	 * @param corp_service
	 * @return
	 */
	public static String getValidResult(int resp, String requestIP, String corp_id, String corp_service) {
		String result = "";
		switch (resp) {
		case 0:
			result = "";
			break;
		case 9:
			result = ErrorCodeUtil.common_balance_not_enough;
			break;
		case 47:
			result = ErrorCodeUtil.common_corpServiceError_or_statusClose;
			break;
		case 48:
			result = ErrorCodeUtil.common_mobile_number_error;
			break;
		case 30004:
			result = ErrorCodeUtil.common_mobile_number_error;
			break;
		case 30005:
			result = ErrorCodeUtil.common_umber_match_services_error;
			break;
		case 61:
			result = ErrorCodeUtil.common_no_before_sign;
			break;
		case 62:
			result = ErrorCodeUtil.common_no_after_sign;
			break;
		case 63:
			result = ErrorCodeUtil.common_sign_no_record;
			break;
		default:
			result = "-1";
			break;
		}
		return result;
	}
	
	/**
	 * 获取计费接口的返回值
	 * @param resp
	 * @param requestIP
	 * @param corp_id
	 * @param corp_service
	 * @return
	 */
	public static String getValidResult(int resp, String requestIP, String corp_id, String corp_service,String mobiles,String ext,int result_) {
		String result = "";
		switch (resp) {
		case 0:
			result = "";
			break;
		case 9:
			result = ErrorCodeUtil.common_balance_not_enough;
			break;
		case 47:
			result = ErrorCodeUtil.common_corpServiceError_or_statusClose;
			break;
		case 48:
			result = ErrorCodeUtil.common_mobile_number_error;
			break;
		case 61:
			result = ErrorCodeUtil.common_no_before_sign;
			break;
		case 62:
			result = ErrorCodeUtil.common_no_after_sign;
			break;
		case 63:
			result = ErrorCodeUtil.common_sign_no_record;
			break;	
		default:
			result = ErrorCodeUtil.common_msg_content_error;
			break;
		}
		return result;
	}

	/**
	 * 校验用户使用状态
	 * @param userInfo
	 * @param requestIP
	 * @param corp_id
	 * @return
	 */
	public static String checkUserStatus(UserBean userInfo, String requestIP, String corp_id) {
		String result = "";
		if (userInfo == null) {
			result = ErrorCodeUtil.common_user_id_error;
		} else if (userInfo != null && userInfo.getStatus() == 1) {
			  result = ErrorCodeUtil.common_user_status_close;
		}
		return result;
	}
	
	/**
	 * 校验IP
	 * @param ip
	 * @param requestIP
	 * @param corp_id
	 * @return
	 */
	public static boolean checkUserIP(String ip, String requestIP, String corp_id) {
		boolean result = true;
		if (ip != null && ip.trim().length() > 0) {
			String[] ips = ip.split(",");
			boolean flag = false;
			for (String ip_every : ips) {
				ip_every = ip_every.replace(".", "\\.").replace("*","[0-9]{1,3}");
				if (requestIP.matches(ip_every)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				  result = false;
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static <T> T inintServices(String path,String jar_name){
		T service  = null;
		try {
			String thread_name = jar_name.split("-")[0];
			// 加载路径
			String loadURL = "file:" +path+"/"+jar_name;
			// 类加载器
			MyClassLoader classLoader = new MyClassLoader(new URL[]{new URL(loadURL)});
			// 加载类，得到IControlService对象
			Thread.currentThread().setContextClassLoader(classLoader);
			Class clz = Class.forName(thread_name, true, classLoader);
			try {
				service = (T)Spring.getApx().getBean(clz);
			} catch (Exception e) {
				log.info("Spring 容器中未找到 " + clz.getName() + "类!");
			}
			
			if(service == null){
				service = (T) clz.newInstance();
			}else{
				//定制接口有定时任务时，需要开启定时任务线程
				ScheduledAnnotationBeanPostProcessor processor = (ScheduledAnnotationBeanPostProcessor) Spring.getApx().getBean(AnnotationConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME);
		    	if(processor != null){
		    		processor.onApplicationEvent(new ContextRefreshedEvent(Spring.getApx()));
		    	}
			}
		} catch (Exception e) {
			log.error("jar_name: "+jar_name,e);
		}
		return service;
	}

}
