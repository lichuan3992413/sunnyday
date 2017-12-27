package sunnyday.gateway.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import sunnyday.tools.util.CommonLogFactory;

public class LogUtil {
    private  static Logger cmpp_info = CommonLogFactory.getLog("cmpp_info");
    private  static Logger url_log = CommonLogFactory.getLog("httpPostLog");
    private  static Logger log = CommonLogFactory.getLog(LogUtil.class);
	private  static Logger SMSAPILOG = CommonLogFactory.getLog("smsApiLog");

	public static Logger getLog() {
		return log;
	}

	private  static Logger report_info = CommonLogFactory.getLog("report_info");
    private  static Logger deliver_info = CommonLogFactory.getLog("deliver_info");
    private  static Logger receiver_log = CommonLogFactory.getLog("receiver");
    public static Logger getReceiver_log() {
		return receiver_log;
	}
	public static Logger getCmpp_info() {
		return cmpp_info;
	}

	/**
	 * 协议接口日志记录，公共工具
	 * @param ip
	 * @param desc
	 * @param request
	 * @param reponse
	 * @param time
	 */
	public static void WriteSmsApiLog(String ip,String desc,String request,String reponse,long time) {
		if(SMSAPILOG.isInfoEnabled()){
			SMSAPILOG.info("client_ip: "+ip+" | desc: "+desc+" | request: "+request+" | reponse: "+reponse+" | time: "+time+" ms");
		}
	}

	/**
	 *
	 * @param ip
	 * @param type
	 * @param request
	 * @param reponse
	 * @param code
	 * @param desc
	 * @param time
	 */
	public static void WriteSmsApiLog(String ip,String type,String request,String reponse,String code,String desc,long time) {
		if(SMSAPILOG.isInfoEnabled()){
			SMSAPILOG.info("client_ip: "+ip+"|type: "+type+"|request: "+request+"|reponse: "+reponse+"|code: "+code+"|desc: "+desc+"|time: "+time+" ms");
		}
	}

	public static void report_info(String user_id,String content,String result) {
    	if(cmpp_info.isInfoEnabled()){
    		cmpp_info.info("send report to user_id: " + user_id + "; content: " +content+"; result: "+result);
    	}
    }
    
    public static void deliver_info(String user_id,String content,String result) {
    	if(cmpp_info.isInfoEnabled()){
    		cmpp_info.info("send deliver to user_id: " + user_id + "; content: " +content+"; result: "+result);
    	}
    }
    
    public static void deliver_info(String user_id, HttpServletRequest request,String content,String result,String type,String pull_seq) {
    	if(deliver_info.isInfoEnabled()){
    		String url="http://"+request.getRemoteAddr()+":"+request.getRemotePort()+"/"+request.getRequestURL();
    		deliver_info.info("user_id: " + user_id + "; url: "+url +"; content: " +content+"; result: "+result+"; type: "+type+"; pull_seq: "+pull_seq);
    	}
    }
    
    public static void report_info(String user_id, HttpServletRequest request,String content,String result,String type,String pull_seq) {
    	if(report_info.isInfoEnabled()){
    		String url="http://"+request.getRemoteAddr()+":"+request.getRemotePort()+"/"+request.getRequestURL();
    		report_info.info("user_id: " + user_id + "; url: "+url +"; content: " +content+"; result: "+result+"; type: "+type+"; pull_seq: "+pull_seq);
    	}
    }
    
    public static void writeLog(String user_id,String mobiles,String IP ,String error_code,String content) {
    	cmpp_info.info("user_id: " + user_id + "; user_ip: "+IP+" ; mobile: "+mobiles +"; return_code: "+error_code+"; explain: "+content);
    } 
    
    public static void writeReceiverLog(String user_id,String mobiles,String IP ,String error_code,String content) {
    	receiver_log.info("user_id: " + user_id + "; user_ip: "+IP+" ; mobile: "+mobiles +"; return_code: "+error_code+"; explain: "+content);
    } 
    
    /**
     * 
     * @param user_id
     * @param content
     * @param result
     */
    public static void writeDeliverLog(String user_id,String content,String result) {
    	if(deliver_info.isInfoEnabled()){
    		deliver_info.info("send deliver to user_id: " + user_id + "; content: " +content+"; result: "+result);
    	}
    }
    
    public static void writeReceiverLog(String user_id,String pwd,String IP ,String error_code,String content,String type ) {
    	receiver_log.info("["+type+"] user_id: " + user_id + "; pwd: "+pwd+" ; user_id: "+user_id +"; return_code: "+error_code+"; explain: "+content);
    }
    
    public static void writeLog(HttpServletRequest request,String error_code,String content) {
    	if(receiver_log.isInfoEnabled()){
    		String user_id = request.getParameter("id");
    		String corp_id = request.getParameter("corp_id");
    		String mobiles = request.getParameter("mobile");
    		String IP = request.getRemoteAddr();
    		
    		if(user_id==null){
    			user_id=corp_id;
        	}
        	receiver_log.info("user_id: " + user_id + "; user_ip: "+IP+" ; mobile: "+mobiles +"; return_code: "+error_code+"; explain: "+content);
    	}
    }
    
    public static void writeLogs(String user_id,String mobiles,String IP,String error_code,String content) {
    	if(receiver_log.isInfoEnabled()){
        	receiver_log.info("user_id: " + user_id + "; user_ip: "+IP+" ; mobile: "+mobiles +"; return_code: "+error_code+"; explain: "+content);
    	}
    }
    
    public static void writeLogs(String user_id,String IP,String error_code,String content) {
    	if(receiver_log.isInfoEnabled()){
        	receiver_log.info("user_id: " + user_id + "; user_ip: "+IP+" ; return_code: "+error_code+"; explain: "+content);
    	}
    }
    
    public static void writeLog(HttpServletRequest request,String error_code,String content,String type) {
    	if(receiver_log.isInfoEnabled()){
    		String user_id = request.getParameter("id");
    		String corp_id = request.getParameter("corp_id");
    		String IP = request.getRemoteAddr();
    		String path = request.getContextPath();
        	if(path==null){
        		path ="";
        	}
        	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+request.getRequestURI();
        	
    		
    		if(user_id==null){
    			user_id=corp_id;
        	}
        	receiver_log.info("type: "+type+"; user_id: " + user_id + "; user_ip: "+IP+"; url: "+basePath +"; return_code: "+error_code+"; explain: "+content);
    	}
    }
    public static Logger getUrl_log() {
		return url_log;
	}

	public static Logger getReport_info() {
		return report_info;
	}

	public static Logger getDeliver_info() {
		return deliver_info;
	}
    
     
}
