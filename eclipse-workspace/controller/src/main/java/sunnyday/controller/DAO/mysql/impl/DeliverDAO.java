package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.DeliverBean;
import sunnyday.controller.DAO.IDeliverDAO;
import sunnyday.controller.cache.MobileAreaCache;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.InsertSQL;

@Repository(value="mysql_DeliverDAO")
public class DeliverDAO extends SimpleDAO  implements IDeliverDAO{
	@Resource
	protected MobileAreaCache mobileAreaCache;
	public void insertIntoDeliverMessage(List<Object> list) {
		String sql = "insert into deliver_message " +
		"(sn, user_id, sp_number, mobile, msg_content, status, try_times, insert_time, update_time, sub_msg_id, pk_total, pk_number, msg_format,send_status,is_encode,province,city,src_sp_number,ext_code)" +
		" values (null, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?,?,?,?,?,?,?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			for(Object eachObj : list){
				DeliverBean each = (DeliverBean)eachObj;
				
				insql.setString(1, each.getUser_id());
				insql.setString(2, each.getSp_number());
				insql.setString(3, each.getMobile());
				insql.setString(4, each.getMsg_content());
				insql.setInt(5, each.getStatus());
				insql.setInt(6, each.getTry_times());
				insql.setInt(7, each.getSub_msg_id());
				insql.setInt(8, each.getPk_total());
				insql.setInt(9, each.getPk_number());
				insql.setInt(10, each.getMsg_format());
				insql.setInt(11, each.getSend_status());
				insql.setInt(12, each.getIs_encode());
				
				// 写入省市
//				DaoUtil.setDeliverArea(mobileAreaCache.getMobileArea(), insql, each, 13, 14);
				insql.setString(15, each.getSrc_spNumner());
				insql.setString(16, each.getExt_code());
				insql.addBatch();
			}
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
		}catch(Exception e) {
			log.error("" , e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}
	
	public void insertIntoDeliverMessage(DeliverBean each) {
		String sql = "insert into deliver_message " +
		"(sn, user_id, sp_number, mobile, msg_content, status, try_times, insert_time, update_time, sub_msg_id, pk_total, pk_number, msg_format,send_status,is_encode,province,city,src_sp_number,ext_code)" +
		" values (null, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?,?,?,?,?,?,?)";
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			insql.setString(1, each.getUser_id());
			insql.setString(2, each.getSp_number());
			insql.setString(3, each.getMobile());
			insql.setString(4, each.getMsg_content());
			insql.setInt(5, each.getStatus());
			insql.setInt(6, each.getTry_times());
			insql.setInt(7, each.getSub_msg_id());
			insql.setInt(8, each.getPk_total());
			insql.setInt(9, each.getPk_number());
			insql.setInt(10, each.getMsg_format());
			insql.setInt(11, each.getSend_status());
			insql.setInt(12, each.getIs_encode());
			// 写入省市
//			DaoUtil.setDeliverArea(mobileAreaCache.getMobileArea(), insql, each, 13, 14);
			insql.setString(15, each.getSrc_spNumner());
			insql.setString(16, each.getExt_code());
			insql.addBatch();
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
		}catch(Exception e) {
			log.error("" , e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}
	
	
	public void updateDeliverMessage(DeliverBean each) {
		String sql = "update deliver_message  set update_time=now(),send_status=?,status=?,try_times=? where user_id=? and sp_number=? and mobile=? and msg_content=?";
		info_log.info("updateDeliverMessage: "+each);
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, each.getSend_status());
			ps.setInt(2, each.getStatus());
			ps.setInt(3, each.getTry_times());
			ps.setString(4, each.getUser_id());
			ps.setString(5, each.getSp_number());
			ps.setString(6, each.getMobile());
			ps.setString(7, each.getMsg_content());
			int result=ps.executeUpdate();
			info_log.info("sql: "+sql+"result: "+result);
			if(result==0){
				insertIntoDeliverMessage(each);
			}
		}catch(Exception e) {
			log.error("" , e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}
	
	public void updateDeliverMessage(List<Object> list) {
		try{
			for(Object eachObj : list){
				DeliverBean each = (DeliverBean)eachObj;
				updateDeliverMessage(each);
			}
		}catch(Exception e) {
			log.error("" , e);
		}  
	}
	

	public void saveBlackMobile(String mobile, String user_id,int type) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			String sql = "insert ignore into black_mobile (mobile, insert_time, operator, level, type, black_type, black_level,user_id) values(?,now(),?,?,?,?,?,?)";
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, mobile);
			stmt.setString(2, "system");
			stmt.setInt(3, type);
			stmt.setInt(4, 1);
			stmt.setInt(5, 9);
			stmt.setInt(6, 12);
			stmt.setString(7,user_id);
			stmt.executeUpdate();
			
		}catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, stmt);
		}
	}

}
