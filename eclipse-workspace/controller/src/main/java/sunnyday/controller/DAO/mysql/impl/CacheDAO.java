package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import sunnyday.controller.DAO.ICacheDAO;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.ResultUtil;

@Repository(value="mysql_CacheDAO")
public class CacheDAO extends SimpleDAO implements ICacheDAO{
	
	public Map<String, TdInfo> load_td_info() {
		Map<String, TdInfo> resultMap = new ConcurrentHashMap<String, TdInfo>();
		String sql = "select sn, td_name, td_code, status, td_type, td_sp_number, filter_flag, submit_type, msg_count_cn,  long_charge_count_cn, long_charge_count_pre_cn, msg_count_en, long_charge_count_en, long_charge_count_pre_en, msg_count_all_cn, msg_count_all_en, with_gate_sign,sign_type from td_info";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<TdInfo> tmpList = ResultUtil.assemble(rs, TdInfo.class);
			for(TdInfo each : tmpList){
				String key = each.getTd_code();
				resultMap.put(key, each);
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}
	
	public Map<String, UserServiceForm> load_deliver_match_spNumber() {
		Map<String, UserServiceForm> resultMap = new HashMap<String, UserServiceForm>();
		String sql = "select t.td_sp_number,u.ext_code,u.user_id ,u.service_code from td_info t,user_service_info u,user_info ui where ui.status = 0 and  ui.user_id = u.user_id and t.td_code = u.td_code";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				UserServiceForm form = new UserServiceForm();
				String key = rs.getString("td_sp_number")+rs.getString("ext_code");
				form.setUser_id(rs.getString("user_id"));
				form.setService_code(rs.getString("service_code"));
				form.setExt_code(rs.getString("ext_code"));
				form.setTd_sp_number(rs.getString("td_sp_number"));
				resultMap.put(key, form);
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}
	
	public Map<String, String> loadGateConfig() {
		Map<String, String> result = new HashMap<String, String>();
		
		String sql = "select name, value from gate_config where status = 0";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
	
			while(rs.next()){
				String key = rs.getString("name");
				String value = rs.getString("value");
				
				result.put(key, value);
			}
		
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public Map<String, UserBean> loadUserInfo() {
		Map<String, UserBean> result = new HashMap<String, UserBean>();
	  	String sql = "select sn, user_id, user_pwd, user_name, user_type, charge_type, gate_type, insert_time, update_time, status, deliver_type,report_type, user_ip, is_filter_repeat, filter_cycle, repeat_times, max_mass_num, deliver_version ,is_filter_repeat_content,filter_cycle_content,repeat_times_content,is_net_switch from user_info where status = 0";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserBean user = ResultUtil.assembleOneBean(rs, UserBean.class);
				String user_id = rs.getString("user_id");
				result.put(user_id, user);
			}

		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	public Map<String, List<UserServiceForm>> loadUserServiceInfo() {
		Map<String, List<UserServiceForm>> result = new HashMap<String, List<UserServiceForm>>();
		String sql = "select sn as service_sn, user_id, td_code, service_code, ext_code, price, type, td_type, priority, status, level, insert_time, update_time, operator, ratio from user_service_info where status = 0";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserServiceForm userService = ResultUtil.assembleOneBean(rs, UserServiceForm.class);
				String user_id = rs.getString("user_id");
				if(!result.containsKey(user_id)){
					result.put(user_id, new ArrayList<UserServiceForm>());
				}
				result.get(user_id).add(userService);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	public Map<String, ErrCode> load_err_code() {
		List<ErrCode> list = null ;
		String sql = "select sn, code_name, response, status, stat, err, fail_desc,report_fail_desc, comment" +
				" from err_code";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = ResultUtil.assemble(rs, ErrCode.class);
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		Map<String, ErrCode> map = new HashMap<String, ErrCode>();
		for(ErrCode err : list){
			map.put(err.getCode_name(),err);
		}
		return map;
	}

	public Map<String, GateErrCode> loadGateErrCode() {
		List<GateErrCode> list = null ;
		String sql = "select sn, gateway, err_code, err_desc, err_type, remark, operator,update_time,is_record_mobile" +
				" from err_code_info";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = ResultUtil.assemble(rs, GateErrCode.class);
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		Map<String, GateErrCode> map = new HashMap<String, GateErrCode>();
		if(list!=null&&list.size()>0){
			for(GateErrCode err : list){
				map.put(err.getErr_code() + "_" + err.getErr_desc(), err);
			}
		}
		return map;
	}
	
	public Map<String, List<UserCheckType>> load_user_check_mode() {
		Map<String, List<UserCheckType>> result = new HashMap<String, List<UserCheckType>>();
		
		String sql = "select user_sn, user_id, service_code, type, mode, fast_mode from user_check_type";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				String user_id = rs.getString("user_id");
				
				if(!result.containsKey(user_id)){
					List<UserCheckType> list = new ArrayList<UserCheckType>();
					result.put(user_id, list);
				}
				
				UserCheckType u = ResultUtil.assembleOneBean(rs, UserCheckType.class);
				result.get(user_id).add(u);
			}
		}catch(Exception e){
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	
	public Map<String, Set<String>> loadWhiteList(int level) {
		Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
		String sql = null;
		switch(level){
		case 0 : sql = "select user_id as map_key, word from keyword_info where type = 2 and level = " + level; break;
		case 1 : sql = "select td_code as map_key, word from keyword_info where type = 2 and level = " + level; break;
		case 2 : sql = "select user_id as map_key, word from keyword_info where type = 2 and level = " + level; break;
		default : break;
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String key = rs.getString("map_key");
				String white_word = rs.getString("word");
				
				if(!resultMap.containsKey(key)){
					Set<String> whiteSet = new HashSet<String>();
					resultMap.put(key, whiteSet);
				}
				resultMap.get(key).add(white_word);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return resultMap;
	}
	public Map<String, Set<String>> loadWhiteMobile() {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		String sql = "select mobile, user_id from black_mobile where type = 2";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String id = rs.getString("user_id");
				String mobile = rs.getString("mobile");
				
				if(!result.containsKey(id)){
					result.put(id, new HashSet<String>());
				}
				result.get(id).add(mobile);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	public Map<String, Set<String>> loadBlackMobileByCondition(String key, int level) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		String sql = "select mobile, " + key + " from black_mobile where type = 1 and level = ? and length(mobile) >= 11";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, level);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String _key = rs.getString(2);
				String mobile = rs.getString("mobile");
				
				if(!result.containsKey(_key)){
					result.put(_key, new HashSet<String>());
				}
				result.get(_key).add(mobile);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public Map<String, Set<String>> loadBlackScopeByCondition(String key, int level) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		String sql = "select mobile, " + key + " from black_mobile where type = 1 and level = ? and length(mobile) < 11";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, level);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String _key = rs.getString(2);
				String mobile = rs.getString("mobile");
				
				if(!result.containsKey(_key)){
					result.put(_key, new HashSet<String>());
				}
				result.get(_key).add(mobile);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	
	
	public Map<String, Set<String>> load_keyword_info(String key, int level) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		String sql = "select word, " + key + " from keyword_info where type = 1 and level = ? ";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, level);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String _key = rs.getString(2);
				String word = rs.getString("word");
				
				if(!result.containsKey(_key)){
					result.put(_key, new HashSet<String>());
				}
				result.get(_key).add(word);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	
	
	public List<KeywordForm> fetchKeyWord(){
		
		List<KeywordForm> list = null;
		
		String sql = "select word,level,td_code from keyword_info where type = 1";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			list = ResultUtil.assemble(rs, KeywordForm.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		 
		return list;
	}

	/**
	 * Submit_message_check_temp
	 * 抓取审核记录表数据
	 * @return
	 */
	public List<CheckCacheForm> fetchCacheData(int limit){
		List<CheckCacheForm> list = null;
		String sql = "select sn,msg_content,td_code,check_user,status from cache_check where check_status=0 order by insert_time desc limit " + limit;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = ResultUtil.assemble(rs, CheckCacheForm.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return list;
	}
	
	public List<CheckCacheForm> fetchSecondLevelCacheContentFromCheckGroup(int limit) {
		List<CheckCacheForm> list = null;
		String tableName = "submit_mass_message_cache";
//		String sql = "select sn, td_code, md5_index as md5_msg_content, content, status, check_user from " + tableName + " where manual_count >= 3 and check_time > now() - interval 1 day  and  (status = 1 or status = 2) order by check_time desc limit " + limit;
		String sql = "select sn,td_code,md5_index as md5_msg_content ,content as msg_content,status,check_user from " + tableName + " where check_time > now() - interval 1 day  and  ((manual_count >= 3 and status = 1) or status = 2) order by check_time desc limit " + limit;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = ResultUtil.assemble(rs, CheckCacheForm.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return list;
	}

	public Map<String, List<UserSignForm>> loadUserSignInfo() {
		Map<String, List<UserSignForm>> result = new HashMap<String, List<UserSignForm>>();
		String sql = "select sn, user_id, gate_sp_number, td_code, sign_chs, sign_eng, status, insert_time, update_time, operator, flag, add_chs_msg, add_eng_msg, is_add_msg, is_add_msg_use,unsubscribe_msg from sign_info where status = 0";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				UserSignForm bean = ResultUtil.assembleOneBean(rs, UserSignForm.class);
				if(!result.containsKey(bean.getUser_id())){
					result.put(bean.getUser_id(), new ArrayList<UserSignForm>());
				}
				result.get(bean.getUser_id()).add(bean);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return result;
	}
	
	public Map<String, List<SignInfoForm>> loadSignInfo() {
		Map<String, List<SignInfoForm>> result = new HashMap<String, List<SignInfoForm>>();
		String sql = "select sn, user_id, gate_sp_number as sp_number, gate_sp_number, td_code, sign_chs, sign_eng, status, insert_time, update_time, operator, flag, add_chs_msg, add_eng_msg, is_add_msg, is_add_msg_use,unsubscribe_msg from sign_info where status = 0";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				SignInfoForm bean = ResultUtil.assembleOneBean(rs, SignInfoForm.class);
				if(!result.containsKey(bean.getUser_id())){
					result.put(bean.getUser_id(), new ArrayList<SignInfoForm>());
				}
				result.get(bean.getUser_id()).add(bean);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return result;
	}

	public Map<String, List<SpNumberFilterInfo>> loadSpNumberFilterInfo() {
		Map<String, List<SpNumberFilterInfo>> result = new HashMap<String, List<SpNumberFilterInfo>>();
		String sql = "select sn, user_id, sp_number, filter_type from sp_number_filter_info where status = 0";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				SpNumberFilterInfo bean = ResultUtil.assembleOneBean(rs, SpNumberFilterInfo.class);
				if(!result.containsKey(bean.getUser_id())){
					result.put(bean.getUser_id(), new ArrayList<SpNumberFilterInfo>());
				}
				result.get(bean.getUser_id()).add(bean);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return result;
	}

	/**
	 * 查询账户国际业务表价格数据
	 * @return
	 */
	public Map<String, List<CountryPhoneCodeInfo>> loadCountryPhoneCodeMap() {
		Map<String, List<CountryPhoneCodeInfo>> result = new HashMap<String, List<CountryPhoneCodeInfo>>();
		String sql = "select c.sn, c.country_en, c.country_cn, c.short_code, c.phone_pre, c.price, c.user_id, p.phone_province from user_country_phone_code_price c left join country_phone_province_code p on c.country_en=p.country_en";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				CountryPhoneCodeInfo bean = ResultUtil.assembleOneBean(rs, CountryPhoneCodeInfo.class);
				if(!result.containsKey(bean.getUser_id())){
					result.put(bean.getUser_id(), new ArrayList<CountryPhoneCodeInfo>());
				}
				result.get(bean.getUser_id()).add(bean);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		return result;
	}

	public List<LocationInfo> loadLocationInfoMap() {
		String sql = "select mobile_scope, province, sp_type from location_info";
		return getBeans(sql, LocationInfo.class);
	}

	public List<CheckMethod> loadCheckMethodList() {
		String sql = "select sn, check_id, check_id as check_code, check_name, check_method from check_method_info";
		return getBeans(sql, CheckMethod.class);
	}
 
	public List<ChannelRoute> loadChannelRouteList() {
		String sql = "select sn, node_name, td_code, action, status,operator from channel_route";
		return getBeans(sql, ChannelRoute.class);
	}
	
	

	public List<NetSwitchedMobileInfo> loadNumberPortabilityMap(int status) {
		String sql = "select mobile, dest_td_type from net_switched_mobile where status="+status;
		return getBeans(sql, NetSwitchedMobileInfo.class);
	}

	public List<TdInfo> load_td_full_info() {
		String sql = "select i.td_name, i.td_code, i.td_sp_number,i.is_support_flash, i.td_sp_number as ext, i.td_type, i.status, i.filter_flag, i.with_gate_sign, i.submit_type, i.sign_type, i.msg_count_cn, i.msg_count_en, i.msg_count_all_cn, i.msg_count_all_en, i.long_charge_count_pre_cn, i.long_charge_count_pre_en, i.long_charge_count_cn, i.long_charge_count_en, s.sign_chs, s.sign_eng, s.ext_code from td_info i left join td_sign_info s on i.td_code=s.td_code";
		return getBeans(sql, TdInfo.class);
	}
 
	public Map<String,String> load_chargeTermidMap() {
		String sql = "select s.td_code,s.ext_code,t.td_sp_number,s.charge_term_id from td_sign_info s left join td_info t on s.td_code = t.td_code";
		return load_chargeTermid(sql);
	}

	
	public List<WhiteListTemplate> load_whiteTemplateCommon() {
		List<WhiteListTemplate> list = null;
		String sql = "select sn,level,template_name,template_content,template_type,level,attach_user_id,operator,insert_time,update_time from white_list_template where level = 0";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			list = ResultUtil.assemble(rs, WhiteListTemplate.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn,ps,rs);
		}
		 
		return list;
	}

	
	public Map<String, List<WhiteListTemplate>> load_whiteTemplateUser() {
		Map<String, List<WhiteListTemplate>> result = new HashMap<String, List<WhiteListTemplate>>();
		String sql = "select sn,level,template_name,template_content,template_type,level,attach_user_id,operator,insert_time,update_time from white_list_template where level = 1";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String user_id = rs.getString("attach_user_id");
				WhiteListTemplate u = ResultUtil.assembleOneBean(rs, WhiteListTemplate.class);
				if(user_id!=null){
					if (!result.containsKey(user_id)) {
						List<WhiteListTemplate> list = new ArrayList<WhiteListTemplate>();
						result.put(user_id, list);
					}
					result.get(user_id).add(u);
				}else{
					log.warn("非法用户级别的白名单："+u);
					
				}
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public Map<String, MobileHome> load_mobileHome() {
		Map<String, MobileHome> resultMap = new ConcurrentHashMap<String, MobileHome>();
		String sql = "select mobile_no, province, city from mobile_area_info";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<MobileHome> tmpList = ResultUtil.assemble(rs, MobileHome.class);
			for(MobileHome each : tmpList){
				String key = each.getMobile_no();
				resultMap.put(key, each);
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}

 
	
	public List<SmsTemplateInfo> load_SmsTemplate() {
		List<SmsTemplateInfo> list = new ArrayList<SmsTemplateInfo>();
		String sql = "select sn, template_id,template_name,template_content,type, attach_user_id, is_avoid_disturb, disturb_perid_time ,template_ext_code,is_add_unsubscribe from sms_template_info where is_using = 0 and check_status = 1";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Map<String,List<SmsTemplateParam>> map = load_SmsTemplateParam();
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			SmsTemplateInfo info = null;
			while(rs.next()){
				String key = rs.getString("template_id");
				info = new SmsTemplateInfo();
				info.setAttachUserId(rs.getString("attach_user_id"));
				info.setParams(map.get(key));
				info.setSn(rs.getInt("sn"));
				info.setTemplateContent(rs.getString("template_content"));
				info.setTemplateId(key);
				info.setTemplateName(rs.getString("template_name"));
				info.setType(rs.getInt("type"));
				info.setIsAvoidDisturb(rs.getInt("is_avoid_disturb"));
				info.setDisturbPeridTime(rs.getString("disturb_perid_time"));
				info.setTemplate_ext_code(rs.getString("template_ext_code"));
				info.setIs_add_unsubscribe(rs.getInt("is_add_unsubscribe"));
				list.add(info);
			}
		} catch (SQLException e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return list;
	}

	
	public Map<String,List<SmsTemplateParam>> load_SmsTemplateParam() {
		Map<String,List<SmsTemplateParam>> result = new HashMap<String, List<SmsTemplateParam>>();
		String sql = "select sn, template_id,param_name,param_desc,val_max_len from sms_template_param";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			SmsTemplateParam tmp = null;
			while(rs.next()){
				String key = rs.getString("template_id");
				if(!result.containsKey(key)){
					result.put(key, new ArrayList<SmsTemplateParam>());
				}
				tmp = new SmsTemplateParam();
				tmp.setSn(rs.getInt("sn"));
				tmp.setTemplateId(key);
				tmp.setParamName(rs.getString("param_name"));
				tmp.setParamDesc(rs.getString("param_desc"));
				tmp.setMaxLength(rs.getInt("val_max_len"));
				result.get(key).add(tmp);
			}
		
		} catch (SQLException e) {
			log.error("SQL = " + sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
 



	
	public Map<String, ServiceInfo> load_serviceInfo() {
		Map<String, ServiceInfo> resultMap = new HashMap<String, ServiceInfo>();
		String sql = "select  service_code,service_type,description,is_template_ext,service_name from service_info where is_template_ext = 1";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ServiceInfo form = null;
			while(rs.next()){
				form = new ServiceInfo();
				String key = rs.getString("service_code");
				form.setDescription(rs.getString("description"));
				form.setService_code(rs.getString("service_code"));
				form.setService_name(rs.getString("service_name"));
				form.setIs_template_ext(rs.getInt("is_template_ext"));
				resultMap.put(key, form);
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}

	
	public Map<String, String> load_commandInfo() {
		Map<String, String> resultMap = new HashMap<String, String>();
		String sql = "select sn,user_id,command,cmd_desc,operator,inser_time,update_time from deliver_command_info ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String key = rs.getString("command")!=null?rs.getString("command"):"";
				resultMap.put(key, rs.getString("user_id"));
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}

	
	public List<String> load_td_spnumber() {
		List<String> result = new ArrayList<String>();
		String sql = "select  td_sp_number from td_info  where `status`  = 0  group by td_sp_number ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String key = rs.getString("td_sp_number") != null ? rs.getString("td_sp_number") : "";
				result.add(key);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}

		return result;
	}

	
	public Map<String, String> load_template_services() {
		Map<String, String> resultMap = new HashMap<String, String>();
		String sql = "select template_id,service_code from template_services_info";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String key = rs.getString("template_id")!=null?rs.getString("template_id"):"";
				resultMap.put(key, rs.getString("service_code"));
			}
		}catch(Exception e) {
			log.error("SQL = " + sql, e);
			return null;
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultMap;
	}

	
	public Map<String, String> load_admin_user() {
		// TODO Auto-generated method stub
		return null;
	}


	
	public List<AutoSendMsgContent> load_autoSendMsgContent_list() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<String> userIdMatchServiceCode(String service_code) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<AutoSendMsgConfig> load_autoSendMsgConfig_list() {
		// TODO Auto-generated method stub
		return null;
	}



}
