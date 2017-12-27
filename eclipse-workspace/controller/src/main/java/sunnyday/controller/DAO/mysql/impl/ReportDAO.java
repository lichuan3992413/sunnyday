package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.controller.DAO.IReportDAO;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.InsertSQL;

@Repository(value="mysql_ReportDAO")
public class ReportDAO extends SimpleDAO  implements IReportDAO{

	public void insertIntoReceiveReport(List<Object> list) {
		String sql = "insert into receive_report_info " +"(sn, td_code, mobile, sp_number, status, fail_desc,  msg_id, insert_time, err, stat,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq)" +
					"values(null, ?, ?, ?, ?, ?, ?, now(), ?, ?,?,?,?,?,?,?)";
			Connection conn = null;
			PreparedStatement ps = null;
			try{
				conn = dataSource.getConnection();
				InsertSQL insql = new InsertSQL(sql);
				for(Object each : list){
					ReportBean report = (ReportBean)each;
					insql.setString(1, report.getTd_code());
					insql.setString(2, report.getMobile());
					insql.setString(3, report.getSp_number());
					insql.setInt(4, report.getStatus());
					insql.setString(5, report.getFail_desc());
					insql.setString(6, report.getMsg_id());
					
					insql.setString(7, report.getErr());
					insql.setInt(8, report.getStat());
					insql.setInt(9, report.getSend_status());
					insql.setString(10, report.getRpt_return_time());
					insql.setString(11, report.getRpt_match_time());
					insql.setString(12, report.getRpt_ready_push_time());
					insql.setString(13, report.getRpt_pushed_time());
					insql.setInt(14, report.getSub_seq());
					insql.addBatch();
				}
				 
				ps = conn.prepareStatement(insql.getFinalSql());
				ps.execute();
			}catch(Exception e1) {
				log.error("", e1);
			} finally {
				DBUtil.freeConnection(conn, ps);
			}
		}

	public Map<ReportBean, SmsMessage> fetchReceiveReport(long receive_sn, int limit) {
		Map<ReportBean, SmsMessage> result = new HashMap<ReportBean, SmsMessage>();
		
		String sql = "select c.submit_sn, c.user_sn, c.user_id, c.service_code, c.ext_code, c.user_ext_code, " +
							"c.td_code, c.sp_number, c.filter_flag, " +
							"c.msg_content, c.msg_id," +
							"c.status, c.response, c.fail_desc," + 
							"c.pknumber, c.pktotal, c.sub_msg_id," +
							"c.price, c.charge_count," +
							"c.insert_time, c.msg_format," +
							"c.err, c.dest_flag," +
							"c.msg_receive_time, c.msg_deal_time, c.msg_scan_time, c.msg_send_time," +
							"c.check_user,  c.cache_sn, c.country_cn, c.ori_mobile,c.ori_mobile,c.is_encode,c.complete_content,c.msg_guid, " +
							
							"r.sn,r.rpt_return_time, r.td_code, r.sp_number," +
							"r.mobile,r.msg_id, r.fail_desc, r.insert_time, r.err, r.stat " +
		
		" from (select * from  receive_report_info where sn >= ? group by mobile, msg_id order by sn asc  limit ?) r left join submit_message_send_catch c " +
		" on  r.mobile = c.mobile  and  r.msg_id = c.tmp_msg_id ";
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setLong(1, receive_sn);
			ps.setInt(2, limit);
			rs = ps.executeQuery();
			
			while(rs.next()){
//				SmsMessage form = ResultUtil.assembleOneBean(rs, SmsMessage.class);
				SmsMessage form = new SmsMessage();
				form.setSubmit_sn(rs.getInt("c.submit_sn"));
				form.setUser_sn(rs.getInt("c.user_sn"));
				form.setUser_id(rs.getString("c.user_id"));
				form.setService_code(rs.getString("c.service_code"));
				form.setExt_code(rs.getString("c.ext_code"));
				form.setUser_ext_code(rs.getString("user_ext_code"));
				form.setTd_code(rs.getString("c.td_code"));
				form.setSp_number(rs.getString("c.sp_number"));
				form.setFilter_flag(rs.getInt("c.filter_flag"));
				form.setMobile(rs.getString("r.mobile"));
				form.setMsg_content(rs.getString("c.msg_content"));
				form.setMsg_id(rs.getString("c.msg_id"));
				form.setInsert_time(rs.getString("c.insert_time"));
				form.setStatus(rs.getInt("c.status"));
				form.setResponse(1000);
				form.setFail_desc(rs.getString("c.fail_desc"));
				form.setTmp_msg_id(rs.getString("r.msg_id"));
				form.setPknumber(rs.getInt("c.pknumber"));
				form.setPktotal(rs.getInt("c.pktotal"));
				form.setSub_msg_id(rs.getInt("c.sub_msg_id"));
				form.setPrice(rs.getDouble("c.price"));
				form.setCharge_count(rs.getInt("c.charge_count"));
				form.setMsg_format(rs.getInt("c.msg_format"));
				form.setDest_flag(rs.getString("c.dest_flag"));
				form.setMsg_receive_time(rs.getString("c.msg_receive_time"));
				form.setMsg_deal_time(rs.getString("c.msg_deal_time"));
				form.setMsg_scan_time(rs.getString("c.msg_scan_time"));
				form.setMsg_send_time(rs.getString("c.msg_send_time"));
				form.setMsg_report_time(rs.getString("r.insert_time"));
				form.setCheck_user(rs.getString("c.check_user"));
				form.setCache_sn(rs.getInt("c.cache_sn"));
				form.setCountry_cn(rs.getString("c.country_cn"));
				form.setOri_mobile(rs.getString("ori_mobile"));
				form.setIs_encode(rs.getInt("c.is_encode"));
				form.setComplete_content(rs.getString("c.complete_content"));
				form.setMsg_guid(rs.getString("c.msg_guid"));
				 
				 
				

				ReportBean report = new ReportBean();
				report.setSn(rs.getInt("r.sn"));
				report.setTd_code(rs.getString("r.td_code"));
				report.setMobile(rs.getString("r.mobile"));
				report.setSp_number(rs.getString("r.sp_number"));
				report.setFail_desc(rs.getString("r.fail_desc"));
				report.setErr(rs.getString("r.err"));
				report.setStat(rs.getInt("r.stat"));
				report.setMsg_id(rs.getString("r.msg_id"));
				report.setRpt_return_time(rs.getString("r.rpt_return_time"));
				result.put(report, form);
			}
				
		} catch (Exception e) {
			log.error("",e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}
	
	public Map<String ,Integer> getReceiveReportSn() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		String sql = "select max(sn) max,min(sn) min from  receive_report_info ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result.put("max", rs.getInt("max"));
				result.put("min", rs.getInt("min"));
			}
				
		} catch (Exception e) {
			log.error("",e);
		}finally{
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	
	

	public void insertIntoReportMessage(List<Object> list, String tableName) {
		String sql = "insert into " + tableName + 
		" (sn, user_id, td_code, sp_number, mobile, msg_id, status, try_times, insert_time, update_time, fail_desc, err , stat, submit_time,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq,rpt_seq)" +
		" values (null, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?,?,?,?,?,?,?,?)";
		 
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			for(Object eachObj : list){
				ReportBean each = (ReportBean)eachObj;
				
				insql.setString(1, each.getUser_id());
				insql.setString(2, each.getTd_code());
				insql.setString(3, each.getSp_number());
				insql.setString(4, each.getMobile());
				insql.setString(5, each.getMsg_id());
				insql.setInt(6, each.getStatus());
				insql.setInt(7, each.getTry_times());
				insql.setString(8, each.getFail_desc());
				insql.setString(9, each.getErr());
				insql.setInt(10, each.getStat());
				insql.setString(11, each.getSubmit_time());
				insql.setInt(12, each.getSend_status());
				insql.setString(13, each.getRpt_return_time());
				insql.setString(14, each.getRpt_match_time());
				insql.setString(15, each.getRpt_ready_push_time());
				insql.setString(16, each.getRpt_pushed_time());
				insql.setInt(17, each.getSub_seq());
				insql.setLong(18, each.getRpt_seq());
				insql.addBatch();
			}
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
		}catch(Exception e1) {
			log.error("", e1);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}
	
	
	public boolean insertIntoReportMessageNew(List<ReportBean> list, String tableName) {
		boolean result = false ;
		String sql = "insert into " + tableName + 
		" (sn, user_id, td_code, sp_number, mobile, msg_id, status, try_times, insert_time, update_time, fail_desc, err , stat, submit_time,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq,rpt_seq)" +
		" values (null, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?,?,?,?,?,?,?,?)";
		 
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			for(ReportBean each : list){
				insql.setString(1, each.getUser_id());
				insql.setString(2, each.getTd_code());
				insql.setString(3, each.getSp_number());
				insql.setString(4, each.getMobile());
				insql.setString(5, each.getMsg_id());
				insql.setInt(6, each.getStatus());
				insql.setInt(7, each.getTry_times());
				insql.setString(8, each.getFail_desc());
				insql.setString(9, each.getErr());
				insql.setInt(10, each.getStat());
				insql.setString(11, each.getSubmit_time());
				insql.setInt(12, each.getSend_status());
				insql.setString(13, each.getRpt_return_time());
				insql.setString(14, each.getRpt_match_time());
				insql.setString(15, each.getRpt_ready_push_time());
				insql.setString(16, each.getRpt_pushed_time());
				insql.setInt(17, each.getSub_seq());
				insql.setLong(18, each.getRpt_seq());
				insql.addBatch();
			}
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
			result = true ;
		}catch(Exception e1) {
			log.error("", e1);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
		return  result ;
	}
	
	
	public void insertIntoReportMessage(ReportBean each, String tableName) {
		info_log.info("insert into  report: "+each);
		String sql = "insert into " + tableName + 
		" (sn, user_id, td_code, sp_number, mobile, msg_id, status, try_times, insert_time, update_time, fail_desc, err , stat, submit_time,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq,rpt_seq)" +
		" values (null, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			insql.setString(1, each.getUser_id());
			insql.setString(2, each.getTd_code());
			insql.setString(3, each.getSp_number());
			insql.setString(4, each.getMobile());
			insql.setString(5, each.getMsg_id());
			insql.setInt(6, each.getStatus());
			insql.setInt(7, each.getTry_times());
			insql.setString(8, each.getFail_desc());
			insql.setString(9, each.getErr());
			insql.setInt(10, each.getStat());
			insql.setString(11, each.getSubmit_time());
			insql.setInt(12, each.getSend_status());
			insql.setString(13, each.getRpt_return_time());
			insql.setString(14, each.getRpt_match_time());
			insql.setString(15, each.getRpt_ready_push_time());
			insql.setString(16, each.getRpt_pushed_time());
			insql.setInt(17, each.getSub_seq());
			insql.setLong(18, each.getRpt_seq());
			insql.addBatch();
			ps = conn.prepareStatement(insql.getFinalSql());
			int count = ps.executeUpdate();
			log.info("["+insql.getFinalSql()+"] "+count);
		}catch(Exception e) {
			log.error("", e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}
	
	
	public void updateReportMessage(List<Object> list, String tableName) {
		if(list!=null){
			for(Object obj:list){
				ReportBean each = (ReportBean)obj;
				updateReportMessage(each,tableName);
			}
		}
		
		
	}
	
	public void updateReportMessage(ReportBean report, String tableName) {
		info_log.info("update report: "+report);
		String sql = "update " + tableName + " set send_status = ?,status = ?,update_time=now() where  mobile = ? and msg_id =? ";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, report.getSend_status());
			ps.setInt(2, report.getStatus());
			ps.setString(3, report.getMobile());
			ps.setString(4, report.getMsg_id());
			
			int result= ps.executeUpdate();
			info_log.info("update report result-> "+result);
			if(result<1){
				insertIntoReportMessage(report,tableName);
			}
		}catch(Exception e1) {
			log.error("", e1);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}

	public void insertIntoReceiveReportCache(List<Object> list) {
		String sql = "insert into receive_report_info_cache " +
		"(sn, td_code, mobile, sp_number, status, fail_desc,  msg_id, insert_time, err, stat,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq)" +
		"values(null, ?, ?, ?, ?, ?, ?, now(), ?, ?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			for(Object each : list){
				ReportBean report = (ReportBean)each;
				insql.setString(1, report.getTd_code());
				insql.setString(2, report.getMobile());
				insql.setString(3, report.getSp_number());
				insql.setInt(4, report.getStatus());
				insql.setString(5, report.getFail_desc());
				insql.setString(6, report.getMsg_id());
				
				insql.setString(7, report.getErr());
				insql.setInt(8, report.getStat());
				insql.setInt(9, report.getSend_status());
				insql.setString(10, report.getRpt_return_time());
				insql.setString(11, report.getRpt_match_time());
				insql.setString(12, report.getRpt_ready_push_time());
				insql.setString(13, report.getRpt_pushed_time());
				insql.setInt(14, report.getSub_seq());
				insql.addBatch();
			}
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
		}catch(Exception e1) {
			log.error("", e1);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}	
	
	public boolean insertIntoReceiveReportCacheNew(List<ReportBean> list) {
		boolean result = false ;
		String sql = "insert into receive_report_info_cache " +
		"(sn, td_code, mobile, sp_number, status, fail_desc,  msg_id, insert_time, err, stat,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq)" +
		"values(null, ?, ?, ?, ?, ?, ?, now(), ?, ?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = dataSource.getConnection();
			InsertSQL insql = new InsertSQL(sql);
			for(ReportBean report : list){
				insql.setString(1, report.getTd_code());
				insql.setString(2, report.getMobile());
				insql.setString(3, report.getSp_number());
				insql.setInt(4, report.getStatus());
				insql.setString(5, report.getFail_desc());
				insql.setString(6, report.getMsg_id());
				
				insql.setString(7, report.getErr());
				insql.setInt(8, report.getStat());
				insql.setInt(9, report.getSend_status());
				insql.setString(10, report.getRpt_return_time());
				insql.setString(11, report.getRpt_match_time());
				insql.setString(12, report.getRpt_ready_push_time());
				insql.setString(13, report.getRpt_pushed_time());
				insql.setInt(14, report.getSub_seq());
				insql.addBatch();
			}
			ps = conn.prepareStatement(insql.getFinalSql());
			ps.execute();
			result = true ;
		}catch(Exception e1) {
			log.error("", e1);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
		
		return result;
	}		
}
