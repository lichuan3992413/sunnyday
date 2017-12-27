package sunnyday.controller.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import sunnyday.common.model.HistoryMessageCountForm;
import sunnyday.common.model.StatisticsHistoryModel;
import sunnyday.common.model.StatisticsModel;

@Repository
public interface IDataStatisticsDAO {

	/**
	 * 账单统计数据
	 * 
	 * @param tableName
	 * @param submit_sn
	 * @param limit
	 * @return
	 */
	public List<HistoryMessageCountForm> statisticsCatch(String tableName,
			long submit_sn, int limit);

	public List<HistoryMessageCountForm> statisticsHistory(String tableName);

	public String insertStatisticsByBatch(List<HistoryMessageCountForm> list);

	public int insertStatisticsHistoryByBatch(List<StatisticsHistoryModel> list);

	public int insertStatisticsHistoryByBatch(StatisticsHistoryModel each);

	public int isExist(HistoryMessageCountForm form);

	public int isExist(StatisticsHistoryModel form);

	public void updateStatistics(StatisticsHistoryModel form, int sn);

	public void updateStatistics(HistoryMessageCountForm form, int sn);

	public void updateStatisticsCatch(HistoryMessageCountForm form, int sn);

	public void updateStatisticsModel(List<StatisticsModel> list,
			String table_name);

	public void updateStatisticsModel(StatisticsModel form, String table_name);
}
