package sunnyday.gateway.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class CommonParameter  implements Serializable{
	private static final long serialVersionUID = 3560116194171234138L;
	//业务账号
	private String corp_id ;
	//业务代码
	private String corp_service ;
	//密码
	private String corp_pwd;
	//手机号
	private String mobiles ;
	//短信内容
	private String msg_content ;
	private String msg_id ;
	private String ext ;
	
	
	@Override
	public String toString() {
		return "CommonParameter [corp_id=" + corp_id + ", corp_service="
				+ corp_service + ", corp_pwd=" + corp_pwd + ", mobiles="
				+ mobiles + ", msg_content=" + msg_content + ", msg_id="
				+ msg_id + ", ext=" + ext + ", format=" + format
				+ ", request_ip=" + request_ip + ", method=" + method
				+ ", type=" + type + ", request=" + request + ", other="
				+ other + "]";
	}
	/**
	 * 计费编码
	 * 0=iso-8859-1 -->全部英文信息用0
	 * 4=二进制流
	 * 8=utf8 
	 * 15=gbk
	 */
	private String format;
	
	
	public String getFormat() {
		return format;
	}
	public int getFormatInt() {
		 int result = 8 ;
		if(format!=null){
			try {
				result = Integer.parseInt(format);
			} catch (Exception e) {
			}
		}
		return result;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * 客户访问时的IP地址
	 */
	private String request_ip ;
	
	/**
	 * post或者get访问模式
	 */
	private String method ;
	/**
	 * 接口类型
	 */
	private String type ;
	private HttpServletRequest request;
	/**
	 * 扩展参数
	 */
	private Map<Object, Object> other = new HashMap<Object, Object>();
	
	public CommonParameter(){
		
	}
	
    public CommonParameter(String corp_id,String corp_service,String corp_pwd,String mobiles,String msg_content,String msg_id,String ext,String request_ip,String method ,String type){
		this.corp_id=corp_id;
		this.corp_service=corp_service;
		this.corp_pwd=corp_pwd;
		this.mobiles=mobiles;
		this.msg_content=msg_content;
		this.msg_id=msg_id;
		this.ext=ext;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
	}
    
    public CommonParameter(String corp_id,String corp_service,String corp_pwd,String mobiles,String ext,String request_ip,String method ,String type){
 		this.corp_id=corp_id;
 		this.corp_service=corp_service;
 		this.corp_pwd=corp_pwd;
 		this.mobiles=mobiles;
 		this.ext=ext;
 		this.request_ip=request_ip;
 		this.method=method;
 		this.type=type;
 	}
    public CommonParameter(String corp_id,String corp_service,String corp_pwd,String mobiles,String msg_content,String msg_id,String ext,String request_ip,String method ,String type,String format){
		this.corp_id=corp_id;
		this.corp_service=corp_service;
		this.corp_pwd=corp_pwd;
		this.mobiles=mobiles;
		this.msg_content=msg_content;
		this.msg_id=msg_id;
		this.ext=ext;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.format = format;
	}
    
    public CommonParameter(String corp_id,String corp_service,String corp_pwd,String mobiles,String msg_content,String msg_id,String ext,String request_ip,String method ,String type,HttpServletRequest request){
    	this.corp_id=corp_id;
		this.corp_service=corp_service;
		this.corp_pwd=corp_pwd;
		this.mobiles=mobiles;
		this.msg_content=msg_content;
		this.msg_id=msg_id;
		this.ext=ext;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.request=request;
   	}
	
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String type,HttpServletRequest request){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.type=type;
		this.request=request;
   	}
    
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String method,String type){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
   	}
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String method,String type,HttpServletRequest request){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.request = request;
   	}
    public CommonParameter(String corp_id,String corp_pwd,String method,String type){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.method=method;
		this.type=type;
   	}
    
    public CommonParameter(String corp_id,String corp_pwd,String method){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.method=method;
   	}
    
    
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String method,String type,String ext){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.ext=ext;
   	}
    
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String method,String type,String ext,String corp_service){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.ext=ext;
		this.corp_service = corp_service;
   	}
    
    public CommonParameter(String corp_id,String corp_pwd,String request_ip,String method,String type,String ext,HttpServletRequest request){
    	this.corp_id=corp_id;
		this.corp_pwd=corp_pwd;
		this.request_ip=request_ip;
		this.method=method;
		this.type=type;
		this.ext=ext;
		this.request=request;
   	}

	public Map<Object, Object> getOther() {
		return other;
	}

	public void setOther(Map<Object, Object> other) {
		this.other = other;
	}

	public String getRequest_ip() {
		return request_ip;
	}
	public void setRequest_ip(String request_ip) {
		this.request_ip = request_ip;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getCorp_id() {
		return corp_id;
	}
	public void setCorp_id(String corp_id) {
		this.corp_id = corp_id;
	}
	public String getCorp_service() {
		return corp_service;
	}
	public void setCorp_service(String corp_service) {
		this.corp_service = corp_service;
	}
	public String getCorp_pwd() {
		return corp_pwd;
	}
	public void setCorp_pwd(String corp_pwd) {
		this.corp_pwd = corp_pwd;
	}
	public String getMobiles() {
		return mobiles;
	}
	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	 
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
