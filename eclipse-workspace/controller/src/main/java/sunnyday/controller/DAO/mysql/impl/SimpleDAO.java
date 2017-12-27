package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;

import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.ResultUtil;
import sunnyday.tools.util.CommonLogFactory;

public class SimpleDAO {
	protected Logger log = CommonLogFactory.getCommonLog("daoLog");
	protected Logger info_log = CommonLogFactory.getCommonLog("infoLog");
	
	@Resource(name="dataSource")
	protected DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * 基本的sql执行方法，没有返回值，方法负责将输入sql传如数据库执行
	 * @param sql
	 */
	public void execute(String sql){
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			
			stmt.execute(sql);
		} catch (Exception e) {
			log.error("Base DAO execute Exception!", e);
		}finally{
			DBUtil.freeConnection(conn, stmt);
		}
	}
	/**
	 * 执行sql语句（带参数）
	 * @param sql
	 * @param params
	 */
	public void execute(String sql, Object[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			for(int i = 0;params != null&&i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			ps.execute();
		} catch (Exception e) {
			log.error("Base DAO execute Exception!", e);
		}finally{
			DBUtil.freeConnection(conn, ps);
		}
	}

	/**
	 * 执行更新(带参数)
	 * @param sql
	 * @param params
	 */
	public int executeUpdate(String sql,Object[] params){
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			for(int i = 0;params != null&&i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			result = ps.executeUpdate();
		} catch (Exception e) {
			result = -1;
			log.error("Base DAO executeUpdate Exception! "+sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps);
		}
		return result;
	}
		
	public int executeUpdate(String sql){
		return this.executeUpdate(sql, null);
	}
	/**
	 * 判断submit_message_send_history数据要插入的表是否存在
	 * @param tableName
	 * @return
	 */
	public boolean hasTable(String tableName){
		boolean result = false;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "show tables like '" + tableName + "'";
		try {
			conn = dataSource.getConnection();
			prep = conn.prepareStatement(sql);
			rs = prep.executeQuery(sql);
			if(rs.next()){
				result = true;
			}
		} catch (Exception e) {
			log.error("Base DAO hasTable Exception!", e);
		} finally {
			DBUtil.freeConnection(conn, prep, rs);
		}
		return result;
	}
	
	public Map<String, String> load_chargeTermid(String sql){
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		Map<String,String> map = new HashMap<String, String>();
		try {
			conn = dataSource.getConnection();
			prep = conn.prepareStatement(sql);
			rs = prep.executeQuery(sql);
			while(rs.next()){
				map.put(rs.getString("s.td_code")+"_"+rs.getString("t.td_sp_number") + rs.getString("s.ext_code"), rs.getString("s.charge_term_id"));
			}

		} catch (Exception e) {
			log.error("Base DAO hasTable Exception!", e);
		} finally {
			DBUtil.freeConnection(conn, prep, rs);
		}
		return map;
	}
	
	public void copyToCacheTable(String srcTable, String destTable, String conditions) {
		String sql = "insert into " + destTable + " select * from " + srcTable + " where " + conditions; 
		this.executeUpdate(sql);
	}
	
	public int deleteFromTable(String srcTable, String conditions) {
		String sql = "delete from " + srcTable + " where " + conditions;
		return this.executeUpdate(sql);
		
	}
	public <T> List<T> getBeans(String sql, Class<T> clazz){
		List<T> resultList = null;
		if(sql != null){
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				conn = dataSource.getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				resultList = ResultUtil.assemble(rs, clazz);
			}catch(Exception e) {
				log.error("common getBeans exception", e);
			} finally {
				DBUtil.freeConnection(conn, ps, rs);
			}
		}
		return resultList;
	}
}
