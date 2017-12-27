package sunnyday.controller.DAO;

import org.springframework.stereotype.Repository;

@Repository
public interface ICheckMsgBatchDAO {
	/**
	 * 检验该批次号是否已存在数据
	 * @return
	 */
	public boolean isBatchNumberLegal(String batchNumber);
	
	public boolean updateStatusAndRemarkByBatchNumber(String batchNumber, int status, String remark,String mobile);
	
	public int getCheckMsgCountByBatchNumber(String batchNumber);
	
	public String getCheckMsgBatchDescByBatchNumber(String batchNumber);
	
}
