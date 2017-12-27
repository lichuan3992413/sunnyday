package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.BalanceNoticeForm;
import sunnyday.common.model.UserBalanceForm;
import sunnyday.controller.DAO.IBalanceDAO;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.ResultUtil;

@Repository(value="mysql_BalanceDAO")
public class BalanceDAO extends SimpleDAO  implements IBalanceDAO{

	public List<BalanceNoticeForm> getAllBalanceNotice() {
		List<BalanceNoticeForm> resultList = new ArrayList<BalanceNoticeForm>();
		String sql = "select sn, user_id, user_name, notice_count, mobiles, warning_status from  balance_notice "; 
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, BalanceNoticeForm.class);
		}catch(Exception e) {
			log.error("sql= " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public Map<String, UserBalanceForm> getAllUserBalanceForm() {
		Map<String, UserBalanceForm> result = new HashMap<String, UserBalanceForm>();
		String sql = "select user_id , user_balance  from user_balance_info ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				UserBalanceForm fm = new UserBalanceForm();
				String user_id = rs.getString("user_id") ;
				fm.setUser_id(user_id);
				fm.setCur_balance(rs.getDouble("user_balance"));
				result.put(user_id, fm);
			}
				
		} catch (Exception e) {
			log.error("sql= "+sql+"",e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public boolean updateBalanceNoticeSarningStatus(long sn, int status) {
		boolean result = false  ;
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "update balance_notice set  warning_status =?  where sn = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setLong(2, sn);
			count = ps.executeUpdate();
			if(count>0){
				result = true ;
			}
		} catch (Exception e) {
			log.error("sql= "+sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps);
		}
		return result;
	}

}
