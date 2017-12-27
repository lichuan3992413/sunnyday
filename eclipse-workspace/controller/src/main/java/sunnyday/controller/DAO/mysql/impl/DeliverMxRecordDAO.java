package sunnyday.controller.DAO.mysql.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.DeliverMxRecord;
import sunnyday.controller.DAO.IDeliverMxRecordDAO;
import sunnyday.controller.util.DBUtil;

@Repository(value="mysql_DeliverMxRecordDAO")
public class DeliverMxRecordDAO extends SimpleDAO implements IDeliverMxRecordDAO{

	
	public DeliverMxRecord queryDeliverMxRecordByMobile(String mobile) {
		DeliverMxRecord resul = new DeliverMxRecord();
		String sql = "select * from deliver_mx_record where mobile= ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);
			rs = ps.executeQuery();
			while (rs.next()) {
				resul.setAccno(rs.getString("accno"));
				resul.setMobile(rs.getString("mobile"));
				resul.setLast_index(rs.getString("last_index"));
				resul.setStart_date(rs.getString("start_date"));
				resul.setEnd_date(rs.getString("end_date"));
            }

		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resul;
	}
	
	
	public int deleteDeliverMxRecord(String mobile) {
		int result = 0;
		String sql = "delete from deliver_mx_record where mobile = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);
			result = ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	
	public boolean addDeliverMxRecord(DeliverMxRecord deliverMxRecord) {
	   boolean result = false;
       int count = 0;
       Connection conn = null;
       PreparedStatement ps = null;
       String sql = "insert into deliver_mx_record" +
    			"(sn, accno, mobile, last_index, start_date, end_date, insert_time, update_time)" +
    			" values (s_deliver_mx_record.nextval, ?, ?, ?, ?, ?,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
       try {
           conn = dataSource.getConnection();
           ps = conn.prepareStatement(sql);
           ps.setString(1, deliverMxRecord.getAccno());
           ps.setString(2, deliverMxRecord.getMobile());
           ps.setString(3, deliverMxRecord.getLast_index());
           ps.setString(4, deliverMxRecord.getStart_date());
           ps.setString(5, deliverMxRecord.getEnd_date());
           
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
	
	
    public boolean updateDeliverMxRecord(DeliverMxRecord deliverMxRecord) {
        boolean result = false;
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update deliver_mx_record set last_index = ?,update_time = to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') where mobile = ? ";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, deliverMxRecord.getLast_index());
            ps.setString(2, deliverMxRecord.getMobile());
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

	
}
