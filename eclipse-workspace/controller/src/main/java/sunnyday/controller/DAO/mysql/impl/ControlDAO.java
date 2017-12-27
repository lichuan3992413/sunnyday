package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.ThreadControllerForm;
import sunnyday.common.model.UserBalanceForm;
import sunnyday.controller.DAO.IControlDAO;
import sunnyday.controller.util.DBUtil;

@Repository(value="mysql_ControlDAO")
public class ControlDAO extends SimpleDAO implements IControlDAO{

	public Map<String, ThreadControllerForm> getNewThreadControllerInfo(String app_name) {
		Map<String, ThreadControllerForm> result = new HashMap<String, ThreadControllerForm>();
		String sql = "select sn, server_ip, thread_name, status, action, thread_param, thread_type, group_id from thread_controller ";
		List<ThreadControllerForm> list = super.getBeans(sql, ThreadControllerForm.class);
		for(ThreadControllerForm each : list){
			String key = each.getCacheKey();
			result.put(key, each);
		}
		return result;
	}

	public void updateThreadControllerStatus(List<ThreadControllerForm> updateList) {
		String sql = "update thread_controller set status = ? where sn = ?";
		if(updateList != null){
			for(ThreadControllerForm each : updateList){
				super.executeUpdate(sql, new Object[]{each.getStatus(), each.getSn()});
			}
		}
	}

 

	public Map<String, Map<String, Object>> getUserParam() {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		String sql = "select sn, user_id, param_key, param_value from user_service_param";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String key = rs.getString("user_id");
				if(!result.containsKey(key)){
					result.put(key, new HashMap<String, Object>());
				}
				result.get(key).put(rs.getString("param_key"), rs.getObject("param_value"));
			}
		}catch(Exception e) {
			log.error("ControlDAO getUserParam exception", e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	
 

	public Map<String, UserBalanceForm> getDbBalanceInfo() {
		Map<String, UserBalanceForm> result = new HashMap<String, UserBalanceForm>();
		String sql = "select user_id, user_balance from user_balance_info";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				UserBalanceForm ubf = new UserBalanceForm();
				ubf.setUser_id(rs.getString("user_id"));
				ubf.setCur_balance(rs.getDouble("user_balance"));
				ubf.setLast_balance(rs.getDouble("user_balance"));
				if(ubf.getUser_id()==null||"".equals(ubf.getUser_id())){
					   log.warn("db user is null ["+ubf+"],sql:["+sql+"]");
						continue;
					}
 
				result.put("balance:" + ubf.getUser_id(), ubf);
			}
		}catch(Exception e) {
			log.error("ControlDAO getDbBalanceInfo exception", e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public void updateUserBalanceInDb(List<UserBalanceForm> dbUpdateList) {
		if(dbUpdateList != null){
			for(UserBalanceForm each : dbUpdateList){
				String sql = "update user_balance_info set user_balance = user_balance + "+each.getChangeBalance_string()+" where user_id = '"+each.getUser_id()+"'";	
				super.executeUpdate(sql);
				info_log.info("sql: "+sql);
			}
		}
	}
}
