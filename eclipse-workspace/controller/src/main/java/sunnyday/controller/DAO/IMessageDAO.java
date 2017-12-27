package sunnyday.controller.DAO;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import sunnyday.common.model.BatchModel;
import sunnyday.common.model.CheckMessage;
import sunnyday.common.model.CheckMsgBatch;
import sunnyday.common.model.DeliverBean;
import sunnyday.common.model.GroupCheckForm;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;
import sunnyday.common.model.TmpInformCust;

@Repository
public interface IMessageDAO {

	public List<ReportBean> queryReportsByStatus(int send_status) ;

	public List<DeliverBean> queryDeleversByStatus(int send_status) ;
	
	public int  deleteTableSendStatus(String snList,String tableName, int sendStatus) ;
	
	public List<SmsMessage> querySubmitMessageTest(int limit) ;
	public void insertIntoSubmitMessageLong(List<?> smsList, String tableName) ;

	public List<SmsMessage> querySplitLongMsg(int limit) ;
	
	public void insertIntoSubmitMessageCheck(List<Object> smsList) ;
	public void insertCheckGroupByBatch(List<Object> checkList);
	
	public boolean queryCheckedData(Map<Integer, List<SmsMessage>> resultMap, int limit) ;
	
	public List<GroupCheckForm> queryCheckedMsgInGroupTable();
	
	public void updateCheckTableByMd5(List<GroupCheckForm> groupList) ;
	
	public void insertIntoGroupHistory(List<GroupCheckForm> groupList) ;
	
	public void insertIntoMassCache(List<GroupCheckForm> groupList);
	
	public void deleteFormGroupTable(List<GroupCheckForm> groupList) ;
	
	public boolean insertIntoSubmitMessageHistory(List<Object> list) ;

	public boolean insert2SubmitMessageHistory(List<SmsMessage> list) ;
	
	public boolean insert2ExceptionalMobileDeal(List<SmsMessage> list) ;

	public void insertIntoSubmitMessageCatch(List<Object> list) ;

	public List<SmsMessage> selectLongMsgTimeOut(int limit) ;

	public List<SmsMessage> selectCatchTimeOut() ;
	
	public int deleteFromTable(String srcTable, String conditions);
	
	public void copyToCacheTable(String srcTable, String destTable, String conditions) ;
	
	public void execute(String sql);
	
	/**
	 * 更新统计明细表
	 * @param number
	 * @param model
	 * @return
	 */
	public int updateMsgTimingBatchDetail(String number,BatchModel model) ;
	
	public List<BatchModel> getBatchList(int limit,int status);
	/**
	 * 保存定时短信记录
	 * 
	 * @param smsList
	 * @param tableName
	 */
	public void insertIntoFixedTimeMessage(List<?> smsList, String tableName);
	
	/**
	 * 保存SubmitMessageDisturb记录
	 * 
	 * @param smsList
	 * @param tableName
	 */
	public void insertIntoSubmitMessageDisturb(List<?> smsList, String tableName);
	
	
	
	/**
	 * 查询已经处于允许下发时间段的打扰短信
	 * 
	 * @param limit
	 *            单次查询最大条数
	 * @return
	 */
	public List<SmsMessage> querySubmitMessageDisturbForSend(int limit);


	/**
	 * 插入待审核表
	 * @param list
	 */
	public void insertIntoCheckMessage(List<Object> list);

	/**
	 * 插入审核批次表
	 * @param batch_message_map
	 */
	public void insertCheckMessageBatch(Map<String, CheckMsgBatch> batch_message_map);
	
	/**
	 * 获取check_msg_batch表信息
	 * @param i
	 * @return
	 */
	public List<CheckMsgBatch> getAuditedCheckMsgBatch(int i);


	
	/**
	 * 通过check_msg_batch表中的信息更新check_message表中的状态和timing_time
	 */
	public void updateCheckMessage(List<CheckMsgBatch> checkMsgBatchs);
	
	/**
	 * 获取check_message表信息
	 * @param i
	 * @return
	 */
	public List<CheckMessage> getAuditedCheckMessage(int i);
	
	/**
	 * 获取check_msg_batch表未审核信息
	 * @param i
	 * @return
	 */
	public List<CheckMsgBatch> getUnCheckedMsgBatch(int size);
	
	
	/**
	 * 获取check_msg_batch表未审核信息
	 * @param i
	 * @return
	 */
	public void updateMessageCheckFlag(String condition);
	
	/**
	 * 获取check_msg_batch表未审核信息
	 * @param i
	 * @return
	 */
	public List<TmpInformCust> getContractConfirmMsg(int size);

	public void updateTmpInformCust(String condition);

	public void updateCustInfoStatus(List<String> updateCustInfoList);

	public void updateTmpInfoCustStatus(List<String> updateCustInfoList);
	
	public List<SmsMessage> selectSubmitMessageSendHistory(int limit);
	/**
	 * 将表从submit_message_send_history拷贝至submit_message_send_history_cache表
	 * 
	 * @param condition
	 */
	public void copySendHisToSendHisCache(String condition);
	
	public Map<String, Integer> geContractConfirmMsg();
	
	public List<TmpInformCust> getContractConfirmMsg(int start,int end);
	
	
	
}
