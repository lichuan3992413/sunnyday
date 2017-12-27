package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.HistoryMessageCountForm;
import sunnyday.common.model.StatisticsHistoryModel;
import sunnyday.common.model.StatisticsModel;
import sunnyday.controller.DAO.IDataStatisticsDAO;
import sunnyday.controller.util.DBUtil;

@Repository(value="mysql_DataStatisticsDAO")
public class DataStatisticsDAO extends SimpleDAO  implements IDataStatisticsDAO{
	
	/**
	 * 账单统计数据
	 * @param tableName
	 * @param submit_sn
	 * @param limit
	 * @return
	 */
	public List<HistoryMessageCountForm> statisticsCatch(String tableName, long submit_sn, int limit){
		
		String sql = "select submit_sn, charge_count as amount, user_sn, td_code, service_code, response, dest_flag, date(insert_time) as date" +
				" from " + tableName + " where submit_sn < " + submit_sn + " order by submit_sn desc limit " + limit;
		return getBeans(sql, HistoryMessageCountForm.class);
	}
	
	public List<HistoryMessageCountForm> statisticsHistory(String tableName){
		
		String sql = "select user_sn,td_code,service_code,response,err,fail_desc,dest_flag,date(insert_time) as date, sum(charge_count) as amount from " + tableName + " where stat_flag = 1 group by 1,2,3,4,5,6,7,8";
		return getBeans(sql, HistoryMessageCountForm.class);
	}
	public String insertStatisticsByBatch(List<HistoryMessageCountForm> list){
		StringBuffer sb = new StringBuffer("-1");
		String statistic_data = "insert into submit_history_statistic(user_sn,td_code,service_code,response,err,fail_desc,dest_flag,date,amount) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(statistic_data, Statement.RETURN_GENERATED_KEYS);
			for(HistoryMessageCountForm each :list){
				int user_sn = each.getUser_sn();
				int response = each.getResponse();
				String err = each.getErr();
				String dest_flag = each.getDest_flag();
				
				ps.setInt(1, user_sn);
				ps.setString(2, each.getTd_code());
				ps.setString(3, each.getService_code());
				ps.setInt(4, response);
				ps.setString(5, err);
				ps.setString(6, each.getFail_desc());
				ps.setString(7, dest_flag);
				ps.setString(8, each.getDate());
				ps.setInt(9, each.getAmount());

				ps.addBatch();
			}
			
			ps.executeBatch();
			try{
				rs = ps.getGeneratedKeys();
				if(rs != null){
					while(rs.next()){
						sb.append(",").append(rs.getInt(1));
					}
				}
			}catch(Exception e){
				log.error("" , e);
			}
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, ps,rs);
		}
		return sb.toString();
	}
	
	public int insertStatisticsHistoryByBatch(List<StatisticsHistoryModel> list){
		String statistic_data = "insert into submit_history_statistic(user_sn,td_code,service_code,response,err,fail_desc,dest_flag,date,amount) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(statistic_data);
			for(StatisticsHistoryModel each :list){
				String dest_flag = each.getDest_flag();
				ps.setInt(1, each.getUser_sn());
				ps.setString(2, each.getTd_code());
				ps.setString(3, each.getService_code());
				ps.setInt(4, each.getResponse());
				ps.setString(5, each.getErr());
				ps.setString(6, each.getFail_desc());
				ps.setString(7, dest_flag);
				ps.setString(8, each.getSend_date());
				ps.setInt(9, each.getAmount());
				ps.addBatch();
			}
			ps.executeBatch();
			return list.size();
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, ps);
		}
		return 0;
	}
	
	public int insertStatisticsHistoryByBatch(StatisticsHistoryModel each) {
		String statistic_data = "insert into submit_history_statistic(user_sn,td_code,service_code,response,err,fail_desc,dest_flag,date,amount) values(?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(statistic_data);
			String dest_flag = each.getDest_flag();
			ps.setInt(1, each.getUser_sn());
			ps.setString(2, each.getTd_code());
			ps.setString(3, each.getService_code());
			ps.setInt(4, each.getResponse());
			ps.setString(5, each.getErr());
			ps.setString(6, each.getFail_desc());
			ps.setString(7, dest_flag);
			ps.setString(8, each.getSend_date());
			ps.setInt(9, each.getAmount());
			return ps.executeUpdate();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
		return 0;
	}
	
	public int isExist(HistoryMessageCountForm form){
		
		int sn = -1;
		String sql = "select sn from submit_history_statistic where user_sn=? and td_code=? and service_code = ? and response=? and dest_flag=? and date=? and err = ? and fail_desc = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1, form.getUser_sn());
			ps.setString(2, form.getTd_code());
			ps.setString(3, form.getService_code());
			ps.setInt(4, form.getResponse());
			ps.setString(5, form.getDest_flag());
			ps.setString(6, form.getDate());
			ps.setString(7, form.getErr());
			ps.setString(8, form.getFail_desc());
			
			rs = ps.executeQuery();
			if(rs.next()){
				sn = rs.getInt("sn");
			}
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, ps,rs);
		}
		
		return sn;
	}
	
public int isExist(StatisticsHistoryModel form){
		int sn = -1;
		String sql = "select sn from submit_history_statistic where user_sn=? and td_code=? and service_code = ? and response=? and dest_flag=? and date=? and err = ? and fail_desc = ? ";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, form.getUser_sn());
			ps.setString(2, form.getTd_code());
			ps.setString(3, form.getService_code());
			ps.setInt(4, form.getResponse());
			ps.setString(5, form.getDest_flag());
			ps.setString(6, form.getSend_date());
			ps.setString(7, form.getErr());
			ps.setString(8, form.getFail_desc());
			//必达短信批次号
			rs = ps.executeQuery();
			if(rs.next()){
				sn = rs.getInt("sn");
			}
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn, ps,rs);
		}
		return sn;
	}
public void updateStatistics(StatisticsHistoryModel form, int sn){
	String sql = "update submit_history_statistic set amount=amount+? where sn = ?";
	Connection conn = null;
	PreparedStatement ps = null;
	
	try {
		conn = dataSource.getConnection();
		ps = conn.prepareStatement(sql);
		int amount = form.getAmount();
		ps.setInt(1, amount);
		ps.setInt(2, sn);
		ps.execute();
		
	} catch (Exception e) {
		log.error("" , e);
	}finally{
		DBUtil.freeConnection(conn,ps);
	}
	
}

	public void updateStatistics(HistoryMessageCountForm form, int sn){
		
		String sql = "update submit_history_statistic set amount=amount+? where sn = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			
			int amount = form.getAmount();
			ps.setInt(1, amount);
			ps.setInt(2, sn);
			ps.execute();
			
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn,ps);
		}
		
	}
	
	public void updateStatisticsCatch(HistoryMessageCountForm form, int sn){
		String sql = "update submit_history_statistic set amount = ? where sn = ?";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			int amount = form.getAmount();
			ps.setInt(1, amount);
			ps.setInt(2, sn);
			ps.execute();
			
		} catch (Exception e) {
			log.error("" , e);
		}finally{
			DBUtil.freeConnection(conn,ps);
		}
	}
	
	public void updateStatisticsModel(List<StatisticsModel> list, String table_name){
		if(list!=null&&list.size()>0){
			for(StatisticsModel model:list ){
				updateStatisticsModel(model,table_name);
			}
		}
		
	}
	public void updateStatisticsModel(StatisticsModel form, String table_name){
		String sql = "insert into "+table_name+" (user_sn,user_id,td_code,send_time,send_count,success_count,fail_count,unknown_count,report_five_count,report_ten_count,report_twenty_count,report_sixty_count,report_other_count,update_time) " +
				"values ("+form.getUser_sn()+",'"+form.getUser_id()+"','"+form.getTd_code()+"','"+form.getSend_time()+"',"+form.getSend_count()+","+form.getSuccess_count()+","+form.getFail_count()+","
				+form.getUnknown_count()+","+form.getReport_five_count()+","+form.getReport_ten_count()+","+form.getReport_twenty_count()+","+form.getReport_sixty_count()+","+form.getReport_other_count()+",now()) " +
				"on DUPLICATE KEY update " +
				"send_count =send_count+" +form.getSend_count()+
				",success_count=success_count+" +form.getSuccess_count()+
				",fail_count=fail_count+" +form.getFail_count()+
				",unknown_count=unknown_count+" +form.getUnknown_count()+
				",report_five_count=report_five_count+" +form.getReport_five_count()+
				",report_ten_count=report_ten_count+" +form.getReport_ten_count()+
				",report_twenty_count=report_twenty_count+" +form.getReport_twenty_count()+
				",report_sixty_count=report_sixty_count+" +form.getReport_sixty_count()+
				",report_other_count=report_other_count+" +form.getReport_other_count()+
				",update_time=now()";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			log.error("sql: "+sql , e);
		}finally{
			DBUtil.freeConnection(conn,ps);
		}
	}
}
