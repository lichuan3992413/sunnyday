package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import sunnyday.common.model.BlackMobile;
import sunnyday.controller.DAO.IBlackMobileDAO;
import sunnyday.controller.util.DBUtil;

@Repository(value = "mysql_BlackMobileDAO")
public class BlackMobileDAO extends SimpleDAO implements IBlackMobileDAO {

	 public boolean addBlack(BlackMobile blackMobile) {       
	   boolean result = false;
       int count = 0;
       Connection conn = null;
       PreparedStatement ps = null;
       
       String sql = "declare t_count number; "
       			+ "begin "
       			+ "select count(*) into t_count from dual where exists(select 1 from black_mobile where mobile=? and \"level\"=? and \"type\"=? and user_id=?);"
       			+ "if t_count<1 then "
       			+ "insert into black_mobile " 
       			+"(sn, mobile, insert_time, operator, \"level\", td_code, user_id, \"type\", black_type, black_level)" +
    			" values (s_black_mobile.nextval, ?, sysdate, ?, ?, ?, ?, ?, ?, ?); "
       			+"end if;"
    			+"end;";
       try {
           conn = dataSource.getConnection();
           ps = conn.prepareStatement(sql);
           ps.setString(1, blackMobile.getMobile());
           ps.setInt(2, blackMobile.getLevel());
           ps.setInt(3, blackMobile.getType());
           ps.setString(4, blackMobile.getUser_id());
           
           ps.setString(5, blackMobile.getMobile());
           ps.setString(6, blackMobile.getOperator());
           ps.setInt(7, blackMobile.getLevel());
           ps.setString(8, blackMobile.getTd_code());
           ps.setString(9, blackMobile.getUser_id());
           ps.setInt(10, blackMobile.getType());
           ps.setInt(11, blackMobile.getBlack_type());
           ps.setInt(12, blackMobile.getBlack_level());
           
           count = ps.executeUpdate();
           conn.commit();
           if (count > 0) {
               result = true;
           }
       } catch (SQLException e) {
           log.error("sql= " + sql, e);
       } finally {
           DBUtil.freeConnection(conn, ps);
       }
       return result;
   }

	public int deleteBlack(BlackMobile blackMobile) {
		int result = 0;
		String sql = "delete from black_mobile where mobile = ? and \"level\" = ? and \"type\" = ? ";
		if (StringUtils.isNotBlank(blackMobile.getUser_id())) {
			sql = sql + "and user_id = ?";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, blackMobile.getMobile());
			ps.setInt(2, blackMobile.getLevel());
			ps.setInt(3, blackMobile.getType());
			if (StringUtils.isNotBlank(blackMobile.getUser_id())) {
				ps.setString(4, blackMobile.getUser_id());
			}
			result = ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	
	public int deleteServiceBlack(BlackMobile blackMobile) {
		int result = 0;
		String sql = "delete from black_mobile where mobile = ? and \"level\" = ? and \"type\" = ? ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, blackMobile.getMobile());
			ps.setInt(2, blackMobile.getLevel());
			ps.setInt(3, blackMobile.getType());
			result = ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

//	@Test
//	public void testAddBlack(){
//		BlackMobile blackMobile = new BlackMobile();
//		blackMobile.setMobile("18950142834");
//		blackMobile.setType(1);
//		blackMobile.setLevel(2);
//		blackMobile.setUser_id("linmohui");
//
//		addBlack(blackMobile);
//	}

}
