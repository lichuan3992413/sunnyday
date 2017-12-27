package sunnyday.controller.DAO.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.BatchModel;
import sunnyday.common.model.CheckMessage;
import sunnyday.common.model.CheckMsgBatch;
import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.ErrCode;
import sunnyday.common.model.GroupCheckForm;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.TmpInformCust;
import sunnyday.controller.DAO.IMessageDAO;
import sunnyday.controller.cache.ErrCodeCache;
import sunnyday.controller.cache.MobileAreaCache;
import sunnyday.controller.util.DBUtil;
import sunnyday.controller.util.InsertSQL;
import sunnyday.controller.util.ResultUtil;
import sunnyday.tools.util.DateUtil;

@Repository(value = "mysql_MessageDAO")
public class MessageDAO extends SimpleDAO implements IMessageDAO {
	@Resource
	protected MobileAreaCache mobileAreaCache;
	public List<ReportBean> queryReportsByStatus(int send_status) {
		List<ReportBean> resultList = new ArrayList<ReportBean>();
		String sql = "select sn, user_id, td_code, sp_number, mobile, msg_id, status, try_times, insert_time, update_time, fail_desc, err , stat, submit_time,send_status,rpt_return_time,rpt_match_time,rpt_ready_push_time,rpt_pushed_time,sub_seq,rpt_seq from  report_message where send_status= ? limit 500";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, send_status);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, ReportBean.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public List<DeliverBean> queryDeleversByStatus(int send_status) {
		List<DeliverBean> resultList = new ArrayList<DeliverBean>();
		String sql = "select sn, user_id, sp_number, mobile, msg_content, status, try_times, insert_time, update_time, sub_msg_id, pk_total, pk_number, msg_format,send_status,is_encode,province,city,src_sp_number ,ext_code from  deliver_message where send_status= ? limit 500";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, send_status);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, DeliverBean.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public int deleteTableSendStatus(String snList, String tableName,
			int sendStatus) {
		int result = 0;
		String sql = "delete from  " + tableName + " where sn in " + snList;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			result = ps.executeUpdate();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return result;
	}

	public List<SmsMessage> querySubmitMessageTest(int limit) {
		List<SmsMessage> resultList = new ArrayList<SmsMessage>();
		String sql = "select submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, "
				+ "sp_number, mobile, msg_content, msg_id, insert_time, update_time, status, sub_msg_id, "
				+ "pknumber, pktotal, price, charge_count, msg_format, dest_flag, msg_receive_time, country_cn, ori_mobile, is_encode,complete_content"
				+ " from submit_message_test where status = 0 limit " + limit;// 时间对应拼接超时处理的时间

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, SmsMessage.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public void insertIntoSubmitMessageLong(List<?> smsList, String tableName) {
		if (smsList != null && smsList.size() > 0) {
			String sql = "insert ignore into "
					+ tableName
					+ "(submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, "
					+ "filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, pknumber,"
					+ " pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, msg_receive_time,"
					+ " country_cn, ori_mobile, signature,istest, province, city)"
					+ "values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Connection conn = null;
			PreparedStatement insql = null;
			try {
				conn = dataSource.getConnection();
				conn.setAutoCommit(false);
				insql = conn.prepareStatement(sql);
				for (Object obj : smsList) {
					SmsMessage each = (SmsMessage) obj;
					insql.setInt(1, each.getUser_sn());// user_sn
					insql.setString(2, each.getUser_id());
					insql.setString(3, each.getService_code());
					insql.setString(4, each.getExt_code());
					insql.setString(5, each.getUser_ext_code());
					insql.setString(6, each.getTd_code());
					insql.setString(7, each.getSp_number());
					insql.setInt(8, each.getFilter_flag());
					insql.setString(9, each.getMobile());
					insql.setString(10, each.getMsg_content());
					insql.setString(11, each.getMsg_id());
					insql.setInt(12, each.getStatus());
					insql.setInt(13, each.getPknumber());
					insql.setInt(14, each.getPktotal());
					insql.setInt(15, each.getSub_msg_id());
					insql.setDouble(16, each.getPrice());
					insql.setInt(17, each.getCharge_count());
					insql.setInt(18, each.getMsg_format());
					insql.setString(19, each.getDest_flag());
					insql.setString(20, each.getMsg_receive_time());
					insql.setString(21, each.getCountry_cn());
					insql.setString(22, each.getOri_mobile());
					insql.setString(23, "");
//					insql.setString(24, each.getIstest());
					// 写入省市
//					DaoUtil.setArea(mobileAreaCache.getMobileArea(), insql, each, 25, 26);
					 
					insql.addBatch();
				}
				insql.executeBatch();
				conn.commit();
			} catch (Exception e) {
				log.error("SQL = " + sql, e);
			} finally {
				DBUtil.freeConnection(conn, insql);
			}
		}
	}

	public List<SmsMessage> querySplitLongMsg(int limit) {
		List<SmsMessage> resultList = new ArrayList<SmsMessage>();
		String sql = "select submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, msg_receive_time, country_cn, ori_mobile, signature,istest"
				+ " from submit_message_long  where insert_time > now() - interval 1 day order by mobile, insert_time desc limit  "
				+ limit;// 时间对应拼接超时处理的时间

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, SmsMessage.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public void insertIntoSubmitMessageCheck(List<Object> smsList) {
		StringBuffer sb = new StringBuffer();
		if (smsList != null && smsList.size() > 0) {
			sb.append("insert_into_submit_message_check: ");
			String sql = "insert ignore into submit_message_check("
					+ "user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, "
					+ "insert_time, update_time, status, response, fail_desc, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, "
					+ "msg_receive_time, msg_deal_time, check_user, cache_sn, md5_index, complete_content, country_cn, ori_mobile "
					+ ") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  now(), ?, ?, ?, ? )";

			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = dataSource.getConnection();
				InsertSQL insql = new InsertSQL(sql);
				for (Object obj : smsList) {
					SmsMessage each = (SmsMessage) obj;
					sb.append(each);
					insql.setInt(1, each.getUser_sn());
					insql.setString(2, each.getUser_id());
					insql.setString(3, each.getService_code());
					insql.setString(4, each.getExt_code());
					insql.setString(5, each.getUser_ext_code());
					insql.setString(6, each.getTd_code());
					insql.setString(7, each.getSp_number());
					insql.setInt(8, each.getFilter_flag());
					insql.setString(9, each.getMobile());
					insql.setString(10, each.getMsg_content());
					insql.setString(11, each.getMsg_id());
					insql.setInt(12, each.getStatus());
					insql.setInt(13, each.getResponse());
					insql.setString(14, each.getFail_desc());
					insql.setInt(15, each.getPknumber());
					insql.setInt(16, each.getPktotal());
					insql.setInt(17, each.getSub_msg_id());
					insql.setDouble(18, each.getPrice());
					insql.setInt(19, each.getCharge_count());
					insql.setInt(20, each.getMsg_format());
					insql.setString(21, each.getDest_flag());
					insql.setString(22, each.getMsg_receive_time());
					// insql.setString(23, each.getMsg_deal_time());
					insql.setString(23, each.getCheck_user());
					insql.setInt(24, each.getCache_sn());
					insql.setString(25, each.getMd5_index());
					insql.setString(26, each.getComplete_content());
					insql.setString(27, each.getCountry_cn());
					insql.setString(28, each.getOri_mobile());
					 
					insql.addBatch();
				}
				ps = conn.prepareStatement(insql.getFinalSql());
				int count = ps.executeUpdate();
				if (count != smsList.size()) {
					log.warn("带插入条数：[" + smsList.size() + "]" + "实际插入条数：["
							+ count + "] " + sb.toString());
				}
			} catch (Exception e) {
				log.error("SQL = " + sql, e);
			} finally {
				DBUtil.freeConnection(conn, ps);
			}
		}
	}

	public void insertCheckGroupByBatch(List<Object> checkList) {
		String sql = "insert ignore into submit_message_check_group"
				+ "(user_sn,user_id, content, md5_index, td_code, count, status, check_user,fail_desc,response, check_time, check_sp_number,src_number)"
				+ " values(?,?,?,?,?,?,?,?,?,?,now(),?,?) on duplicate key update count = count + 1";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			for (Object obj : checkList) {
				SmsMessage message = (SmsMessage) obj;
				if (message.getStatus() == 0) {
					ps.setInt(1, message.getUser_sn());
					ps.setString(2, message.getUser_id());
					ps.setString(3, message.getComplete_content());
					ps.setObject(4, message.getMd5_index());
					ps.setString(5, message.getTd_code());
					ps.setObject(6, message.getExtraField("mass_id"));// count
					ps.setInt(7, message.getStatus());
					ps.setObject(8, message.getCheck_user());
					ps.setString(9, message.getFail_desc());
					ps.setInt(10, message.getResponse());
					ps.setString(11, message.getSp_number());
					ps.setString(12, message.getSrc_number());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}

	public boolean queryCheckedData(Map<Integer, List<SmsMessage>> resultMap,
			int limit) {
		boolean result = false;
		String sql = "select submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, response, fail_desc, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, msg_receive_time, msg_deal_time, check_user, cache_sn, md5_index, complete_content, country_cn, ori_mobile "
				+ " from submit_message_check where status = 1 or status = 2 order by submit_sn limit "
				+ limit;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				SmsMessage message = ResultUtil.assembleOneBean(resultSet,
						SmsMessage.class);
				message.setMsg_deal_time(DateUtil.currentTimeToMs());
				resultMap.get(message.getStatus()).add(message);
				result = true;
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {

			DBUtil.freeConnection(conn, ps, resultSet);
		}
		return result;
	}

	public List<GroupCheckForm> queryCheckedMsgInGroupTable() {
		List<GroupCheckForm> resultList = new ArrayList<GroupCheckForm>();
		// or status = 11 or status = 12 暂且不处理
		String sql = "select sn, user_sn, check_sp_number, content, md5_index, td_code, count,manual_count, status, check_user, check_time,fail_desc"
				+ " from submit_message_check_group where status = 1 or status = 2 ";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				GroupCheckForm tmpForm = new GroupCheckForm();
				tmpForm.setSn(rs.getInt("sn"));
				tmpForm.setUser_sn(rs.getInt("user_sn"));
				tmpForm.setContent(rs.getString("content"));
				tmpForm.setMd5_index(rs.getString("md5_index"));
				tmpForm.setTd_code(rs.getString("td_code"));
				tmpForm.setCount(rs.getInt("count"));
				tmpForm.setManual_count(rs.getInt("manual_count"));
				tmpForm.setStatus(rs.getInt("status"));
				tmpForm.setCheck_sp_number(rs.getString("check_sp_number"));
				tmpForm.setCheck_user(rs.getString("check_user"));
				tmpForm.setCheck_time(rs.getString("check_time"));
				tmpForm.setFail_desc(rs.getString("fail_desc"));
				resultList.add(tmpForm);
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public void updateCheckTableByMd5(List<GroupCheckForm> groupList) {
		// 多个条件一次更新更快呢？　 还是一次一条更快，可以验证一下，然后采用较为高效的办法
		if (groupList != null && groupList.size() > 0) {
			String sql = "update submit_message_check set status = ?, response = ?, check_user = ? ,fail_desc = ?, msg_deal_time = now() where md5_index = ?";
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = dataSource.getConnection();
				ps = conn.prepareStatement(sql);

				for (GroupCheckForm tmpForm : groupList) {
					// System.out.println("CheckTableByMd5: "+tmpForm);
					ps.setInt(1, tmpForm.getStatus());
					ps.setInt(2, tmpForm.getResponse());// response
					ps.setString(3, tmpForm.getCheck_user());

					if (tmpForm.getStatus() == 1) {// 审核通过
						ps.setString(4, tmpForm.getFail_desc() + "->人工通过");
					} else if (tmpForm.getStatus() == 2) {// 审核驳回
						ps.setString(4, tmpForm.getFail_desc() + "->人工驳回");
					} else {
						ps.setString(4, tmpForm.getFail_desc() + "-test");
					}
					ps.setString(5, tmpForm.getMd5_index());
					int er = ps.executeUpdate();

					if (er != tmpForm.getCount()) {
						log.warn("md5_index = [" + tmpForm.getMd5_index()
								+ "] group count is [" + tmpForm.getCount()
								+ "] --- really effact rows = [" + er + "]");
					}
				}
			} catch (Exception e) {
				log.error("SQL = " + sql, e);
			} finally {
				DBUtil.freeConnection(conn, ps);
			}
		}
	}

	public void insertIntoGroupHistory(List<GroupCheckForm> groupList) {
		String sql = "insert ignore into submit_message_check_group_cache (sn, user_sn, content, md5_index, td_code, count,manual_count ,response,fail_desc, status,"
				+ " check_user, check_time, check_sp_number) values(?,?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key update count = count + ? , manual_count = if(status = ?,manual_count + ?, 1)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			for (GroupCheckForm tmpForm : groupList) {
				ps.setInt(1, tmpForm.getSn());
				ps.setInt(2, tmpForm.getUser_sn());
				ps.setString(3, tmpForm.getContent());
				ps.setString(4, tmpForm.getMd5_index());
				ps.setString(5, tmpForm.getTd_code());
				ps.setInt(6, tmpForm.getCount());
				ps.setInt(7, tmpForm.getManual_count());

				ps.setInt(8, tmpForm.getResponse());
				ps.setString(9, tmpForm.getFail_desc());

				ps.setInt(10, tmpForm.getStatus());
				ps.setString(11, tmpForm.getCheck_user());
				ps.setString(12, tmpForm.getCheck_time());
				ps.setString(13, tmpForm.getCheck_sp_number());
				ps.setInt(14, tmpForm.getCount());

				ps.setInt(15, tmpForm.getStatus());
				ps.setInt(16, tmpForm.getManual_count());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
			if (!hasTable("submit_message_check_group_cache")) {
				try {
					// here can't ensure data insert into history, solve it
					// tomrrow[by this method, problem solved]
					dataSource
							.getConnection()
							.createStatement()
							.execute(
									"create table submit_message_check_group_cache like submit_message_check_group");
					insertIntoGroupHistory(groupList);
				} catch (Exception e1) {
					log.error("SQL = " + sql, e1);
				}
			}
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}

	public void insertIntoMassCache(List<GroupCheckForm> groupList) {
		String sql = "insert ignore into submit_mass_message_cache (sn, user_sn, content, md5_index, td_code, count, status,"
				+ "response, fail_desc, check_user, check_time ,manual_count) values(?,?,?,?,?,?,?,?,?,?,?,?)"
				+ " on duplicate key update count = count + ?,check_time = ?, check_user = ?,"
				+ "status = ?, manual_count = if(status = ?,manual_count + ?, 1)";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			for (GroupCheckForm tmpForm : groupList) {
				boolean isIllegal = tmpForm.getResponse() == ErrCodeCache
						.getErrCode(ErrCode.codeName.illegalReject)
						.getResponse();
				if (tmpForm.getCount() < 2 || isIllegal)
					continue;// 违法驳回不进入缓存：response=123
				ps.setInt(1, tmpForm.getSn());
				ps.setInt(2, tmpForm.getUser_sn());
				ps.setString(3, tmpForm.getContent());
				ps.setString(4, tmpForm.getMd5_index());
				ps.setString(5, tmpForm.getTd_code());
				ps.setInt(6, tmpForm.getCount());
				ps.setInt(7, tmpForm.getStatus());
				ps.setInt(8, tmpForm.getResponse());
				ps.setString(9, tmpForm.getFail_desc());
				ps.setString(10, tmpForm.getCheck_user());
				ps.setString(11, tmpForm.getCheck_time());
				ps.setInt(12, tmpForm.getManual_count());

				ps.setInt(13, tmpForm.getCount());
				ps.setString(14, tmpForm.getCheck_time());
				ps.setString(15, tmpForm.getCheck_user());

				ps.setInt(16, tmpForm.getStatus());
				ps.setInt(17, tmpForm.getStatus());
				ps.setInt(18, tmpForm.getManual_count());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}
	}

	public void deleteFormGroupTable(List<GroupCheckForm> groupList) {
		StringBuilder sb = new StringBuilder();
		for (GroupCheckForm each : groupList) {
			sb.append(each.getSn()).append(",");
		}
		sb.append(-1);
		String sql = "delete from submit_message_check_group where sn in ("
				+ sb.toString() + ")";
		super.executeUpdate(sql);
	}

	public boolean insertIntoSubmitMessageHistory(List<Object> list) {
		boolean result = false;
		String sql = "insert  into submit_message_send_history "
				+ "(sn, submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, "
				+ "insert_time, update_time, status, response, fail_desc, tmp_msg_id, stat_flag, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, "
				+ "err , dest_flag, msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time,"
				+ " msg_report_time, check_user, cache_sn, country_cn, ori_mobile,rpt_seq,complete_content,is_encode, province, city,msg_guid,template_id)"
				+ " values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement insql = null;
			insql = conn.prepareStatement(sql);
			for (Object eachObj : list) {
				SmsMessage each = (SmsMessage) eachObj;
				insql.setLong(1, each.getSubmit_sn());
				insql.setInt(2, each.getUser_sn());
				insql.setString(3, each.getUser_id());
				insql.setString(4, each.getService_code());
				insql.setString(5, each.getExt_code());
				insql.setString(6, each.getUser_ext_code());
				insql.setString(7, each.getTd_code());
				insql.setString(8, each.getSp_number());
				insql.setInt(9, each.getFilter_flag());
				insql.setString(10, each.getMobile());
				insql.setString(11, each.getMsg_content());
				insql.setString(12, each.getMsg_id());

				if (each.getMsg_send_time() != null) {
					insql.setString(13, each.getMsg_send_time()
							.substring(0, 19));
				} else {
					insql.setString(13,
							each.getMsg_receive_time().substring(0, 19));
				}

				insql.setInt(14, each.getStatus());
				insql.setInt(15, each.getResponse());
				insql.setString(16, each.getFail_desc());
				insql.setString(17, each.getTmp_msg_id());
				insql.setInt(18, each.getStat_flag());
				insql.setInt(19, each.getPknumber());
				insql.setInt(20, each.getPktotal());
				insql.setInt(21, each.getSub_msg_id());
				insql.setDouble(22, each.getPrice());
				insql.setInt(23, each.getCharge_count());
				insql.setInt(24, each.getMsg_format());
				String err = each.getErr();
				try {
					if (err == null) {
						err = String.valueOf(each.getResponse());
					}
				} catch (Exception e) {
					err = "error";
				}

				insql.setString(25, err);
				insql.setString(26, each.getDest_flag());
				insql.setString(27, each.getMsg_receive_time());
				insql.setString(28, each.getMsg_deal_time());
				insql.setString(29, each.getMsg_scan_time());
				insql.setString(30, each.getMsg_send_time());
				insql.setString(31, each.getMsg_report_time());
				insql.setString(32, each.getCheck_user());
				insql.setInt(33, each.getCache_sn());
				insql.setString(34, each.getCountry_cn());
				insql.setString(35, each.getOri_mobile());
				insql.setLong(36, each.getRpt_seq());
				insql.setString(37, each.getComplete_content());
				insql.setInt(38, each.getIs_encode());
				// 写入省市
//				DaoUtil.setArea(mobileAreaCache.getMobileArea(), insql, each, 39, 40);
				insql.setString(41, each.getMsg_guid());
				insql.setString(42, each.getTemplate_id());
				insql.addBatch();
			}
			insql.executeBatch();
			result = true;
			conn.commit();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}

		return result;
	}

	public boolean insert2SubmitMessageHistory(List<SmsMessage> list) {
		boolean result = false;
		String sql = "insert  ignore into submit_message_send_history "
				+ "(sn, submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, "
				+ "insert_time, update_time, status, response, fail_desc, tmp_msg_id, stat_flag, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, "
				+ "err , dest_flag, msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time,"
				+ " msg_report_time, check_user, cache_sn, country_cn, ori_mobile,rpt_seq,complete_content,is_encode, province, city,msg_guid,template_id)"
				+ " values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement insql = null;
			insql = conn.prepareStatement(sql);
			for (SmsMessage each : list) {
				insql.setLong(1, each.getSubmit_sn());
				insql.setInt(2, each.getUser_sn());
				insql.setString(3, each.getUser_id());
				insql.setString(4, each.getService_code());
				insql.setString(5, each.getExt_code());
				insql.setString(6, each.getUser_ext_code());
				insql.setString(7, each.getTd_code());
				insql.setString(8, each.getSp_number());
				insql.setInt(9, each.getFilter_flag());
				insql.setString(10, each.getMobile());
				insql.setString(11, each.getMsg_content());
				insql.setString(12, each.getMsg_id());

				if (each.getMsg_send_time() != null) {
					insql.setString(13, each.getMsg_send_time()
							.substring(0, 19));
				} else {
					insql.setString(13,
							each.getMsg_receive_time().substring(0, 19));
				}

				insql.setInt(14, each.getStatus());
				insql.setInt(15, each.getResponse());
				insql.setString(16, each.getFail_desc());
				insql.setString(17, each.getTmp_msg_id());
				insql.setInt(18, each.getStat_flag());
				insql.setInt(19, each.getPknumber());
				insql.setInt(20, each.getPktotal());
				insql.setInt(21, each.getSub_msg_id());
				insql.setDouble(22, each.getPrice());
				insql.setInt(23, each.getCharge_count());
				insql.setInt(24, each.getMsg_format());
				String err = each.getErr();
				try {
					if (err == null) {
						err = String.valueOf(each.getResponse());
					}
				} catch (Exception e) {
					err = "error";
				}

				insql.setString(25, err);
				insql.setString(26, each.getDest_flag());
				insql.setString(27, each.getMsg_receive_time());
				insql.setString(28, each.getMsg_deal_time());
				insql.setString(29, each.getMsg_scan_time());
				insql.setString(30, each.getMsg_send_time());
				insql.setString(31, each.getMsg_report_time());
				insql.setString(32, each.getCheck_user());
				insql.setInt(33, each.getCache_sn());
				insql.setString(34, each.getCountry_cn());
				insql.setString(35, each.getOri_mobile());
				insql.setLong(36, each.getRpt_seq());
				insql.setString(37, each.getComplete_content());
				insql.setInt(38, each.getIs_encode());
				// 写入省市
//				DaoUtil.setArea(mobileAreaCache.getMobileArea(), insql, each, 39, 40);
				insql.setString(41, each.getMsg_guid());
				insql.setString(42, each.getTemplate_id());
				 
				insql.addBatch();
			}
			insql.executeBatch();
			result = true;
			conn.commit();
		} catch (Exception e) {
			log.error("SQL = " + sql + "|" + list, e);
		} finally {
			DBUtil.freeConnection(conn, ps);
		}

		return result;
	}

	public void insertIntoSubmitMessageCatch(List<Object> list) {
		String sql = "insert ignore into submit_message_send_catch "
				+ "(submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, "
				+ "insert_time, update_time, status, response, fail_desc, tmp_msg_id, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, "
				+ "msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time, check_user,"
				+ " cache_sn, country_cn, ori_mobile,complete_content,is_encode, province, city,msg_guid,template_id)"
				+ " values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?,?,?)";

		Connection conn = null;
		PreparedStatement insql = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			insql = conn.prepareStatement(sql);
			for (Object eachObj : list) {
				SmsMessage each = (SmsMessage) eachObj;

				insql.setInt(1, each.getUser_sn());
				insql.setString(2, each.getUser_id());
				insql.setString(3, each.getService_code());
				insql.setString(4, each.getExt_code());
				insql.setString(5, each.getUser_ext_code());
				insql.setString(6, each.getTd_code());
				insql.setString(7, each.getSp_number());
				insql.setInt(8, each.getFilter_flag());
				insql.setString(9, each.getMobile());
				insql.setString(10, each.getMsg_content());
				insql.setString(11, each.getMsg_id());
				insql.setInt(12, each.getStatus());
				insql.setInt(13, each.getResponse());
				insql.setString(14, each.getFail_desc());
				insql.setString(15, each.getTmp_msg_id());
				insql.setInt(16, each.getPknumber());
				insql.setInt(17, each.getPktotal());
				insql.setInt(18, each.getSub_msg_id());
				insql.setDouble(19, each.getPrice());
				insql.setInt(20, each.getCharge_count());
				insql.setInt(21, each.getMsg_format());
				insql.setString(22, each.getDest_flag());
				insql.setString(23, each.getMsg_receive_time());
				insql.setString(24, each.getMsg_deal_time());
				insql.setString(25, each.getMsg_scan_time());
				insql.setString(26, each.getMsg_send_time());
				insql.setString(27, each.getCheck_user());
				insql.setInt(28, each.getCache_sn());
				insql.setString(29, each.getCountry_cn());
				insql.setString(30, each.getOri_mobile());
				insql.setString(31, each.getComplete_content());
				insql.setInt(32, each.getIs_encode());
				// 写入省市
//				DaoUtil.setArea(mobileAreaCache.getMobileArea(), insql, each, 33, 34);
				insql.setString(35, each.getMsg_guid());
				insql.setString(36, each.getTemplate_id());
				insql.addBatch();
			}
			insql.executeBatch();
			conn.commit();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, insql);
		}
	}

	public List<SmsMessage> selectLongMsgTimeOut(int limit) {
		List<SmsMessage> resultList = null;
		String sql = "select submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, msg_receive_time, country_cn, ori_mobile "
				+ " from submit_message_long  where insert_time < now() - interval 1450 minute limit "
				+ limit; // 1450minute 为1天 10分钟 60*24=1440
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, SmsMessage.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}

	public List<SmsMessage> selectCatchTimeOut() {
		String sql_select = "select submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id,  insert_time, update_time, status, response, fail_desc, tmp_msg_id, pknumber, pktotal, sub_msg_id, price, charge_count, msg_format, dest_flag, msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time, check_user, cache_sn, country_cn, ori_mobile,template_id  from submit_message_send_catch  where insert_time < now() - interval 3 day limit  1000";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<SmsMessage> catch_list = new ArrayList<SmsMessage>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(sql_select);
			rs = stmt.executeQuery();
			catch_list = ResultUtil.assemble(rs, SmsMessage.class);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			DBUtil.freeConnection(conn, stmt, rs);
		}
		return catch_list;
	}
	
	public int updateMsgTimingBatchDetail(String number, BatchModel model) {
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "update msg_timing_batch_detail set success_count = success_count+"+model.getSuccess_count()+",fail_count=fail_count+"+model.getFail_count() +" where batch_number = ?" ;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, number);
			count = ps.executeUpdate();
		} catch (Exception e) {
			log.error("sql= "+sql, e);
		}finally{
			DBUtil.freeConnection(conn, ps);
		}
		return count;
	}

	
	public List<BatchModel> getBatchList(int limit, int status) {
		List<BatchModel> resultList = new ArrayList<BatchModel>();
		String sql = "select batch_number,batch_count,success_count,fail_count,send_time,send_status from  msg_timing_batch_detail where send_status!=? limit ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setInt(2, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				BatchModel tmpForm = new BatchModel();
				tmpForm.setBatch_number(rs.getString("batch_number"));
				tmpForm.setBatch_count(rs.getInt("batch_count"));
				tmpForm.setSuccess_count(rs.getInt("success_count"));
				tmpForm.setFail_count(rs.getInt("fail_count"));
				tmpForm.setSend_status(rs.getInt("send_status"));
				tmpForm.setSend_time(rs.getString("send_time"));
				resultList.add(tmpForm);;
			}
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		return resultList;
	}
	
	
	public void insertIntoSubmitMessageDisturb(List<?> smsList, String tableName) {
		String sql = "insert  into " + tableName
				+ "(user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, response, fail_desc, tmp_msg_id, stat_flag, sub_msg_id,pknumber, pktotal, price, charge_count, msg_format, err , dest_flag, msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time, msg_report_time, check_user, cache_sn, country_cn, ori_mobile, signature,operator_signature,istest,area_code,complete_content,report_fail_desc,try_times,md5_index,src_number,do_times, rpt_seq, is_encode,msg_guid,template_id,extraFields,queue_name,disturb_start_time,disturb_end_time,disturb_insert_time)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())";
		Connection conn = null;
		PreparedStatement insql = null;
		try {
			conn = dataSource.getConnection();
			insql = conn.prepareStatement(sql);
			for (Object eachObj : smsList) {
				SmsMessage each = (SmsMessage) eachObj;
				insql.setInt(1, each.getUser_sn());
				insql.setString(2, each.getUser_id());
				insql.setString(3, each.getService_code());
				insql.setString(4, each.getExt_code());
				insql.setString(5, each.getUser_ext_code());
				insql.setString(6, each.getTd_code());
				insql.setString(7, each.getSp_number());
				insql.setInt(8, each.getFilter_flag());
				insql.setString(9, each.getMobile());
				insql.setString(10, each.getMsg_content());
				insql.setString(11, each.getMsg_id());
				insql.setString(12, each.getInsert_time());
				insql.setString(13, each.getUpdate_time());
				insql.setInt(14, each.getStatus());
				insql.setInt(15, each.getResponse());
				insql.setString(16, each.getFail_desc());
				insql.setString(17, each.getTmp_msg_id());
				insql.setInt(18, each.getStat_flag());
				insql.setInt(19, each.getSub_msg_id());
				insql.setInt(20, each.getPknumber());
				insql.setInt(21, each.getPktotal());
				insql.setDouble(22, each.getPrice());
				insql.setInt(23, each.getCharge_count());
				insql.setInt(24, each.getMsg_format());
				insql.setString(25, each.getErr());
				insql.setString(26, each.getDest_flag());
				insql.setString(27, each.getMsg_receive_time());
				insql.setString(28, each.getMsg_deal_time());
				insql.setString(29, each.getMsg_scan_time());
				insql.setString(30, each.getMsg_send_time());
				insql.setString(31, each.getMsg_report_time());
				insql.setString(32, each.getCheck_user());
				insql.setInt(33, each.getCache_sn());
				insql.setString(34, each.getCountry_cn());
				insql.setString(35, each.getOri_mobile());
//				insql.setString(36, each.getSignature());
//				insql.setString(37, each.getOperator_signature());
//				insql.setString(38, each.getIstest());
				insql.setString(39, each.getArea_code());
				insql.setString(40, each.getComplete_content());
				insql.setString(41, each.getReport_fail_desc());
				insql.setInt(42, each.getTry_times());
				insql.setString(43, each.getMd5_index());
				insql.setString(44, each.getSrc_number());
				insql.setInt(45, each.getDo_times());
				insql.setLong(46, each.getRpt_seq());
				insql.setInt(47, each.getIs_encode());
				insql.setString(48, each.getMsg_guid());
				insql.setString(49, each.getTemplate_id());
				insql.setObject(50, each.getExtraFields());
				insql.setString(51, (String) each.getExtraField("queue_name"));
				insql.setString(52, (String) each.getExtraField("disturb_start_time"));
				insql.setString(53, (String) each.getExtraField("disturb_end_time"));
				insql.addBatch();
			}
			
			insql.executeBatch();
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, insql);
		}
	}
	
	public List<SmsMessage> querySubmitMessageDisturbForSend(int limit) {
		List<SmsMessage> resultList = new ArrayList<SmsMessage>();
		
		String selectCols = "submit_sn, user_sn, user_id, service_code, ext_code, user_ext_code, td_code, sp_number, filter_flag, mobile, msg_content, msg_id, insert_time, update_time, status, response, fail_desc, tmp_msg_id, stat_flag, sub_msg_id,pknumber, pktotal, price, charge_count, msg_format, err , dest_flag, msg_receive_time, msg_deal_time, msg_scan_time, msg_send_time, msg_report_time, check_user, cache_sn, country_cn, ori_mobile, signature,operator_signature,istest,area_code,complete_content,report_fail_desc,try_times,md5_index,src_number,do_times, rpt_seq, is_encode,msg_guid,template_id,extraFields";
		String sql =  "select " + selectCols + " from ((select " + selectCols + " from submit_message_disturb where disturb_start_time <= ? and disturb_end_time >= ?) union all (select " + selectCols + " from submit_message_disturb where disturb_start_time <= ? and disturb_end_time >= ?)) t1 limit "  + limit;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
//			String curHm = DateUtil.currentHourMin();
//			ps.setString(1, curHm);
//			ps.setString(2, curHm);
//			String incr24Hm = DateUtil.addHour(curHm, 24);
//			ps.setString(3, incr24Hm);
//			ps.setString(4, incr24Hm);
			rs = ps.executeQuery();
			resultList = ResultUtil.assemble(rs, SmsMessage.class);
		} catch (Exception e) {
			log.error("SQL = " + sql, e);
		} finally {
			DBUtil.freeConnection(conn, ps, rs);
		}
		
		return resultList;
	}

	
	public boolean insert2ExceptionalMobileDeal(List<SmsMessage> list) {
		boolean result = false;
		String sql = "insert ignore into exceptional_mobile_deal (sn,mobile,user_id,template_id,err_code,err_msg,operator,insert_time,update_time) values (null,?,?,?,?,?,?,now(),now())";
		Connection conn = null;
		PreparedStatement insql = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			insql = conn.prepareStatement(sql);
			for (SmsMessage each : list) {
				insql.setString(1, each.getMobile());
				insql.setString(2, each.getUser_id());
				insql.setString(3, each.getTemplate_id());
				insql.setString(4, each.getErr());
				insql.setString(5, each.getFail_desc());
				insql.setString(6, "system");
				insql.addBatch();
			}
			insql.executeBatch();
			result = true;
			conn.commit();
		} catch (Exception e) {
			log.error("SQL = " + sql + "|" + list, e);
		} finally {
			DBUtil.freeConnection(conn, insql);
		}
		return result;
	}

	
	public void insertIntoFixedTimeMessage(List<?> smsList, String tableName) {
		
	}


	
	public void insertIntoCheckMessage(List<Object> list) {
		
	}

	
	public void insertCheckMessageBatch(
			Map<String, CheckMsgBatch> batch_message_map) {
		
	}

	
	public List<CheckMsgBatch> getAuditedCheckMsgBatch(int i) {
		return null;
	}

	/**
	 * 通过check_msg_batch表中的信息更新check_message表中的状态和timing_time
	 */
	
	public void updateCheckMessage(List<CheckMsgBatch> checkMsgBatchs) {
		
	}
	
	/**
	 * 从check_message表中取出status=1和status=2的短信
	 */
	
	public List<CheckMessage> getAuditedCheckMessage(int i) {
		return null;
	}


	
	public List<CheckMsgBatch> getUnCheckedMsgBatch(int size) {
		return null;
	}

	
	public void updateMessageCheckFlag(String condition) {
		
	}

	
	public List<TmpInformCust> getContractConfirmMsg(int size) {
		return null;
	}

	
	public void updateTmpInformCust(String condition) {
		
	}

	
	public void updateCustInfoStatus(List<String> updateCustInfoList) {
		
	}

	
	public void updateTmpInfoCustStatus(List<String> updateCustInfoList) {
		
	}

	
	public List<SmsMessage> selectSubmitMessageSendHistory(int limit) {
		return null;
	}

	
	public void copySendHisToSendHisCache(String condition) {
		
	}

	
	public Map<String, Integer> geContractConfirmMsg() {
		return new HashMap<String, Integer>();
	}

	
	public List<TmpInformCust> getContractConfirmMsg(int start, int end) {
		return null;
	}
	
}
