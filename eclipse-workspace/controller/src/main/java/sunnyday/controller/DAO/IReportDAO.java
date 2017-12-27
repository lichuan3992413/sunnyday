package sunnyday.controller.DAO;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import sunnyday.common.model.ReportBean;
import sunnyday.common.model.SmsMessage;

@Repository
public interface IReportDAO {

	public void insertIntoReceiveReport(List<Object> list);

	public Map<ReportBean, SmsMessage> fetchReceiveReport(long receive_sn, int limit);
	
	public Map<String ,Integer> getReceiveReportSn();

	public void insertIntoReportMessage(List<Object> list, String tableName);
	
	public boolean insertIntoReportMessageNew(List<ReportBean> list, String tableName) ;
	
	public void insertIntoReportMessage(ReportBean each, String tableName) ;
	
	public void updateReportMessage(List<Object> list, String tableName);
	
	public void updateReportMessage(ReportBean report, String tableName) ;

	public void insertIntoReceiveReportCache(List<Object> list);
	
	public boolean insertIntoReceiveReportCacheNew(List<ReportBean> list) ;	
	
	public void execute(String sql);
}
