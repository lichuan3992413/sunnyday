package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import sunnyday.controller.DAO.ICheckMsgBatchDAO;
import sunnyday.controller.util.DBUtil;

@Repository(value = "mysql_CheckMsgBatchDAO")
public class CheckMsgBatchDAO extends SimpleDAO implements ICheckMsgBatchDAO {

	
	public boolean isBatchNumberLegal(String batchNumber) {
		boolean result = false;
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select count(sn) from check_msg_batch where batch_number = ? ";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, batchNumber);
            rs = ps.executeQuery();
            if(rs.next()){
            	count = rs.getInt(1);
            }
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

	
	public boolean updateStatusAndRemarkByBatchNumber(String batchNumber, int status, String remark,String mobile) {
		boolean result = false;
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update check_msg_batch set  check_status = ?,check_user = ?,check_time = to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),update_time = to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')";
        String addCols = " ,remark = remark + ?";
        if(remark!=null){
        	sql = sql + addCols;
        }
        sql = sql + " where batch_number = ?";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            if(remark!=null){
            	ps.setString(2, mobile);
            	ps.setString(3, remark);
                ps.setString(4, batchNumber);
            }else{
            	ps.setString(2, mobile);
            	ps.setString(3, batchNumber);
            }
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

	
	public int getCheckMsgCountByBatchNumber(String batchNumber) {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select count(sn) from check_message where batch_number = ? and check_status = 0";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, batchNumber);
            rs = ps.executeQuery();
            if(rs.next()){
            	count = rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("sql= " + sql, e);
        } finally {
            DBUtil.freeConnection(conn, ps);
        }
        return count;
	}

	
	public String getCheckMsgBatchDescByBatchNumber(String batchNumber) {
		String result = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select remark from check_msg_batch where batch_number = ? ";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, batchNumber);
            rs = ps.executeQuery();
            if(rs.next()){
            	result = rs.getString(1);
            }
        } catch (SQLException e) {
            log.error("sql= " + sql, e);
        } finally {
            DBUtil.freeConnection(conn, ps);
        }
        return result;
	}
	
//	@Test
//	public void testIsBatchNumberLegal(){
//		boolean result = isBatchNumberLegal("PC201709121231");
//		System.out.println(result);
//		boolean result2 = updateStatusAndRemarkByBatchNumber("PC201709121",2,"我要驳回，谢谢！");
//		System.out.println(result2);
//		boolean result3 = updateStatusAndRemarkByBatchNumber("PC201709121",1,null);
//		System.out.println(result3);
//	}
}