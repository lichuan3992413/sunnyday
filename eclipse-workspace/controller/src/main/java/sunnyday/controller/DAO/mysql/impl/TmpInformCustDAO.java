package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.TmpInformCust;
import sunnyday.controller.DAO.ITmpInformCustDAO;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.ResultUtil;

@Repository(value = "mysql_TmpInformCustDAO")
public class TmpInformCustDAO extends SimpleDAO implements ITmpInformCustDAO {

	
	public List<TmpInformCust> getTmpInformCustByMobile(String mobile) {
		List<TmpInformCust> resultList = new ArrayList<TmpInformCust>();
        String sql = "select sn, accno, prim_flag, custno, mobile, status, cust_name, try_times,send_time from  tmp_inform_cust where mobile=? ";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, mobile);
            rs = ps.executeQuery();
            resultList = ResultUtil.assemble(rs, TmpInformCust.class);
        } catch (SQLException e) {
            log.error("sql= " + sql, e);
        } finally {
            DBUtil.freeConnection(conn, ps, rs);
        }
        return resultList;

	 }

	
	public int updateTmpInformCustByMobile(String mobile) {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update tmp_inform_cust set status =1  where mobile = ? ";
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, mobile);
            count = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            log.error("sql= " + sql, e);
            count = -1;
        } finally {
            DBUtil.freeConnection(conn, ps);
        }
        return count;
	}
	
}