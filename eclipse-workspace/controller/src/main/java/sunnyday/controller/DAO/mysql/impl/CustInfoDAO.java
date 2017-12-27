package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import sunnyday.common.model.CustInfo;
import sunnyday.controller.DAO.ICustInfoDAO;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.ResultUtil;

@Repository(value = "mysql_CustInfoDAO")
public class CustInfoDAO extends SimpleDAO implements ICustInfoDAO {

	
	public int updateCustInfoByMobileAndStatus(String mobile,int status) {
	 	int result = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        //String sql = "update cust_info set status = ?,modi_time = sysdate where accno = ? and status = 0";
		String sql = "update cust_info set status = ?,modi_time = sysdate where mobile = ? and cust_type = '0'";
		try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, mobile);
            result = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            log.error("sql= " + sql, e);
            result = -1;
        } finally {
            DBUtil.freeConnection(conn, ps);
        }
        
        return result;
	}

	
	public CustInfo queryPersonalCustInfoByMobileAccno(String mobile, String accno) {
		CustInfo resul = new CustInfo();
		//select x.* from (select * from  cust_info where mobile= ? and accno like concat('%',?) ORDER BY modi_time desc ) x where rownum <= 1
		String sql = "select * from cust_info where mobile=? and status = 1 ORDER BY modi_time desc";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);
			rs = ps.executeQuery();
			while (rs.next()) {
				String dbAccno = rs.getString("accno");
				if(StringUtils.isNotBlank(dbAccno) && dbAccno.endsWith(accno)){
					resul.setAccno(rs.getString("accno"));
					resul.setStatus(rs.getString("status"));
					break;
				}
            }
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resul;
	}
	
	
	public boolean existCustInfoByMobile(String mobile) {
		String sql = "select count(1) as cnt from cust_info where mobile=? and cust_type='0'";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);
			rs = ps.executeQuery();
			while (rs.next()) {
				int cnt = rs.getInt("cnt");
				return cnt > 0;
            }
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		
		return false;
	}
	
	
	public CustInfo queryCommonCustInfoByMobileAccno(String mobile, String accno) {
		CustInfo resul = new CustInfo();
		//select x.* from ( select c.accno,cust_no,status from cust_info c join accno_mobile a on a.accno = c.accno"
		//+ " where a.mobile = ? and c.accno like concat('%',?) ORDER BY modi_time desc ) x where rownum <= 1
		String sql = "select c.accno, cust_no, status from cust_info c join accno_mobile a on a.accno = c.accno where a.mobile = ? and c.status = 1 ORDER BY modi_time desc ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);
            //ps.setString(2, accno);
			rs = ps.executeQuery();
			while (rs.next()) {
				String dbAccno = rs.getString("accno");
				if(StringUtils.isNotBlank(dbAccno) && dbAccno.endsWith(accno)){
					resul.setAccno(rs.getString("accno"));
					resul.setCustNo(rs.getString("cust_no"));
					resul.setStatus(rs.getString("status"));
					break;
				}
            }
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resul;
	}
	
	
	public List<CustInfo> queryAllPersonalCustInfo(String mobile, String custType, String status) {
		String sql = "select * from cust_info where mobile=? ";
		if (StringUtils.isNotBlank(custType)) {
			sql += "and cust_type=? ";
		}

		if (StringUtils.isNotBlank(status)) {
			sql += "and status=?";
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, mobile);

			if (StringUtils.isNotBlank(custType)) {
				ps.setString(2, custType);
			}

			if (StringUtils.isNotBlank(status)) {
				ps.setString(3, status);
			}

			rs = ps.executeQuery();
			return ResultUtil.assemble(rs, CustInfo.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}

		return null;
	}
}