package sunnyday.common.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 账户表信息
 */
public class UserBean implements Serializable{
	
	private static final long serialVersionUID = -765058881549517720L;

	private int sn;
	private String user_id;
	private int is_net_switch; //0 是 ，1否
	private Map<String,HashSet<UserServiceForm>> userMultiServiceMap;
	public Map<String, HashSet<UserServiceForm>> getUserMultiServiceMap() {
		return userMultiServiceMap;
	}


	public void setUserMultiServiceMap(Map<String, HashSet<UserServiceForm>> userMultiServiceMap) {
		this.userMultiServiceMap = userMultiServiceMap;
	}


	public int getIs_net_switch() {
		return is_net_switch;
	}


	public void setIs_net_switch(int is_net_switch) {
		this.is_net_switch = is_net_switch;
	}


	private String user_pwd;
	private String user_name;
	private String user_ip ="";
	private int user_type;
	private int charge_type;
	private String gate_type;
	private String insert_time;
	private String update_time;
	private int status;
	private int deliver_type;
	private int report_type;
	//账户下的业务
	public Map<String, List<UserServiceForm>> getServiceMap() {
		return serviceMap;
	}


	public void setServiceMap(Map<String, List<UserServiceForm>> serviceMap) {
		this.serviceMap = serviceMap;
	}


	//账户签名信息
	private List<SignInfoForm> userSignInfo;
	public List<SignInfoForm> getUserSignInfo() {
		return userSignInfo;
	}


	public void setUserSignInfo(List<SignInfoForm> userSignInfo) {
		this.userSignInfo = userSignInfo;
	}


	//账户下的业务
	private  Map<String, List<UserServiceForm>> serviceMap;
	public int getIs_filter_repeat() {
		return is_filter_repeat;
	}


	public void setIs_filter_repeat(int is_filter_repeat) {
		this.is_filter_repeat = is_filter_repeat;
	}


	public int getFilter_cycle() {
		return filter_cycle;
	}


	public void setFilter_cycle(int filter_cycle) {
		this.filter_cycle = filter_cycle;
	}


	public int getIs_filter_repeat_content() {
		return is_filter_repeat_content;
	}


	public void setIs_filter_repeat_content(int is_filter_repeat_content) {
		this.is_filter_repeat_content = is_filter_repeat_content;
	}


	public int getFilter_cycle_content() {
		return filter_cycle_content;
	}


	public void setFilter_cycle_content(int filter_cycle_content) {
		this.filter_cycle_content = filter_cycle_content;
	}


	public int getRepeat_times_content() {
		return repeat_times_content;
	}


	public void setRepeat_times_content(int repeat_times_content) {
		this.repeat_times_content = repeat_times_content;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	private int is_filter_repeat;
	private int filter_cycle;
	private int is_filter_repeat_content;// 基于内容 是否过滤重复下发（默认1 过滤） 0：不过滤 1：过滤
	private int filter_cycle_content;// 基于内容 间隔时间
	private int repeat_times_content;// 基于内容 重复次数
	private int deliver_version;//账号版本
	private String user_sp_number;
	private int max_allow_count;
	private int limit_speed;
	private int is_switch_td;
 	/*private int is_net_switch; //0 是 ，1否

	 *//**
	 * 
	 * @return 0 是 ，1否
	 *//* 
	public int getIs_net_switch() {
		return is_net_switch;
	}


	public void setIs_net_switch(int is_net_switch) {
		this.is_net_switch = is_net_switch;
	} */


	private int contentCount;
	//账户参数map
	private Map<String, Object> paramMap; 
	
	//账户下的业务
//	private  Map<String, List<UserServiceForm>> serviceMap;
//	private Map<String, HashSet<UserServiceForm>> userMultiServiceMap;

	@Override
	public String toString() {
		return "UserBean [user_id=" + user_id + ", user_pwd=" + user_pwd
				+ ", user_name=" + user_name + ", user_ip=" + user_ip
				+ ", user_type=" + user_type + ", charge_type=" + charge_type
				+ ", gate_type=" + gate_type + ", status=" + status
				+ ", deliver_type=" + deliver_type + ", report_type="
				+ report_type + ", deliver_version=" + deliver_version
				+ ", user_sp_number=" + user_sp_number + "]";
	}


	public int getDeliver_version() {
		return deliver_version;
	}


	public void setDeliver_version(int deliver_version) {
		this.deliver_version = deliver_version;
	}

	public int getReport_type() {
		if(report_type==0){
			report_type = this.deliver_type;
		}
		return report_type;
	}


	public void setReport_type(int report_type) {
		this.report_type = report_type;
	}

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_pwd() {
		return user_pwd;
	}
	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getUser_type() {
		return user_type;
	}
	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}
	public int getCharge_type() {
		return charge_type;
	}
	public void setCharge_type(int charge_type) {
		this.charge_type = charge_type;
	}
	public String getGate_type() {
		return gate_type;
	}
	public void setGate_type(String gate_type) {
		this.gate_type = gate_type;
	}
	public String getInsert_time() {
		return insert_time;
	}
	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * 0 不需要
	 * 1 平台推送
	 * 2 客户自取
	 * @return
	 */
	public int getDeliver_type() {
		return deliver_type;
	}
	public void setDeliver_type(int deliver_type) {
		this.deliver_type = deliver_type;
	}
	public int getContentCount() {
		return contentCount;
	}
	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}
	public String getUser_ip() {
		return user_ip;
	}
	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getSn() {
		return sn;
	}


	public String getUser_sp_number() {
		return user_sp_number;
	}


	public void setUser_sp_number(String user_sp_number) {
		this.user_sp_number = user_sp_number;
	}


	public int getMax_allow_count() {
		return max_allow_count;
	}


	public void setMax_allow_count(int max_allow_count) {
		this.max_allow_count = max_allow_count;
	}


	public int getLimit_speed() {
		return limit_speed;
	}


	public void setLimit_speed(int limit_speed) {
		this.limit_speed = limit_speed;
	}


	public int getIs_switch_td() {
		return is_switch_td;
	}


	public void setIs_switch_td(int is_switch_td) {
		this.is_switch_td = is_switch_td;
	}

	
	public Map<String, Object> getParamMap() {
		return paramMap;
	}


	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	public String getServiceMapString() {
		StringBuffer sb = new StringBuffer("\n");
		if(serviceMap!=null&&serviceMap.size()>0){
			for(String key:serviceMap.keySet() ){
				List<UserServiceForm> list = serviceMap.get(key);
				if(list!=null&&list.size()>0){
					for(UserServiceForm f:list ){
						sb.append(key).append(" --> ").append(f.toString()).append(";").append("\n");
					}
					sb.append("================================").append("\n");
				}
				
			}
		}
		return sb.toString();
	}
}
