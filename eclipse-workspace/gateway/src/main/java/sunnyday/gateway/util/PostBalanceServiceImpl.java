package sunnyday.gateway.util;

import java.text.DecimalFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import sunnyday.common.model.UserBalanceInfo;
import sunnyday.common.model.UserBean;
import sunnyday.gateway.dc.DataCenter;
import sunnyday.tools.util.DateUtil;
/**
 * 余额查询
 * @author 1307365
 *
 */
@Service
public class PostBalanceServiceImpl implements ISmsService{
	public HSResponse doSomething(CommonParameter param) {
		String corp_id = param.getCorp_id();
		String corp_pwd = param.getCorp_pwd();
		String requestIP = param.getRequest_ip();
		
		HttpServletRequest request = param.getRequest();
		String TYPE = param.getType();
		
		//判断用户是否存在
        Map<String,UserBean> userInfoMap = DataCenter.getUserBeanMap();
       
        if(corp_id == null){
        	LogUtil.writeLog(request, ErrorCodeUtil.common_user_id_error, "参数:id未填写",TYPE);
        	return new HSResponse(ErrorCodeUtil.common_user_id_error,"账号参数非法");
        }
        UserBean userInfo=userInfoMap.get(corp_id.trim());
        if(userInfo==null){
            LogUtil.writeLog(request, ErrorCodeUtil.common_user_id_error, "用户["+corp_id+"]不存在或者已经关闭",TYPE);
            return new HSResponse(ErrorCodeUtil.common_user_id_error,"非法用户");
        }
        
        //校验用户使用状态
        if(userInfo.getStatus() == 1){
        	LogUtil.writeLog(request, ErrorCodeUtil.common_user_id_error, "用户["+corp_id+"]已经关闭",TYPE);
        	return new HSResponse(ErrorCodeUtil.common_user_id_error,"账号已经关闭");
        }
        //校验用户密码
        if (TYPE.contains("JZYH_")){
        	String tmpPwd = MD5.convert(userInfo.getUser_pwd());
        	if(corp_pwd == null || !tmpPwd.equals(corp_pwd)){
   	           LogUtil.writeLog(request, ErrorCodeUtil.common_user_mima_error, "密码["+corp_pwd+"]填写错误，正确密码为["+userInfo.getUser_pwd()==null?"":userInfo.getUser_pwd()+"]",TYPE);
   	           return new HSResponse(ErrorCodeUtil.common_user_mima_error,"密码错误");
   	        }
        } else {
	        if(corp_pwd == null || !userInfo.getUser_pwd().equals(corp_pwd)){
	           LogUtil.writeLog(request, ErrorCodeUtil.common_user_mima_error, "密码["+corp_pwd+"]填写错误，正确密码为["+userInfo.getUser_pwd()==null?"":userInfo.getUser_pwd()+"]",TYPE);
	           return new HSResponse(ErrorCodeUtil.common_user_mima_error,"密码错误");
	        }
        }
        
        //校验用户访问ip
        if (userInfo.getUser_ip() != null && userInfo.getUser_ip().trim().length() > 0) {
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
        		 LogUtil.writeLog(request, ErrorCodeUtil.common_user_ip_error, "非法IP访问,所允许的IP为["+userInfo.getUser_ip()+"]",TYPE);
        		 return new HSResponse(ErrorCodeUtil.common_user_ip_error,"非法IP访问");
        	}
        }
        
		//校验访问频率
		if(Tools.user_balance_count.get(corp_id) == null){
			Tools.user_balance_count.put(corp_id, System.currentTimeMillis());
		}else{
			long time = System.currentTimeMillis() - Tools.user_balance_count.get(corp_id) ;
			if(time < 200){
				LogUtil.writeLog(request, ErrorCodeUtil.common_fast_error, "访问"+TYPE+"超过限制频率（间隔应200ms以上，客户的间隔为["+time+"ms]）",TYPE);
				return new HSResponse(ErrorCodeUtil.common_fast_error,"访问频率过高");
			}else{
				Tools.user_balance_count.put(corp_id, System.currentTimeMillis());
			}
		}
		
                
        UserBalanceInfo ubi = DataCenter.getUserBalanceMap().get(corp_id);
        String user_balance="0";
        if(ubi!=null){
        	if(ubi.getBalance()>0){
        		 user_balance = new DecimalFormat("#.00000").format(ubi.getBalance());
        	}
        }else {
        	LogUtil.getReceiver_log().warn("corp_id: "+corp_id+" is no UserBalanceInfo .");
		}
         
        LogUtil.getReceiver_log().info("user_id:"+corp_id+",corp_pwd"+corp_pwd+",requestIP:"+requestIP+",user_balance:"+user_balance);
		return new HSResponse("ok#" + user_balance,"余额为["+user_balance+"]厘");
	}
	
	public static void main(String[] args) {
		String xx = MD5.convert("123456");
		System.out.println(xx);
	}
}
